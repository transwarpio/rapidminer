package com.rapidminer.launcher;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.BreakpointListener;
import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.RepositoryProcessLocation;
import com.rapidminer.core.license.DatabaseConstraintViolationException;
import com.rapidminer.core.license.LicenseViolationException;
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.license.LicenseConstants;
import com.rapidminer.license.LicenseManager;
import com.rapidminer.license.LicenseManagerRegistry;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.Operator;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.repository.RepositoryLocation;
import com.rapidminer.tools.*;
import com.rapidminer.tools.container.Pair;
import com.rapidminer.tools.usagestats.ActionStatisticsCollector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class CommandLineLauncher extends RapidMiner implements BreakpointListener {
    private static final String LICENSE = "RapidMiner Studio Free" + RapidMiner.getLongVersion() + ", Copyright (C) 2001 - 2016 RapidMiner GmbH" + Tools.getLineSeparator() + "See End User License Agreement information in the file named EULA.";
    private String repositoryLocation = null;
    private boolean readFromFile = false;
    private final List<Pair<String, String>> macros = new ArrayList();

    public CommandLineLauncher() {
    }

    public void breakpointReached(Process process, Operator operator, IOContainer container, int location) {
        System.out.println("Results in application " + operator.getApplyCount() + " of " + operator.getName() + ":" + Tools.getLineSeparator() + container);
        System.out.println("Breakpoint reached " + (location == 0?"before ":"after ") + operator.getName() + ", press enter...");
        (new CommandLineLauncher.WaitForKeyThread(process)).start();
    }

    public void resume() {
    }

    private void parseArguments(String[] argv) {
        this.repositoryLocation = null;
        String[] var2 = argv;
        int var3 = argv.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String element = var2[var4];
            if(element != null) {
                if("-f".equals(element)) {
                    this.readFromFile = true;
                } else if(element.startsWith("-M")) {
                    String elementSubstring = element.substring(2);
                    String[] split = elementSubstring.split("=");
                    this.macros.add(new Pair(split[0], split[1]));
                } else if(this.repositoryLocation == null) {
                    this.repositoryLocation = element;
                }
            }
        }

        if(this.repositoryLocation == null) {
            printUsage();
        }

    }

    private static void printUsage() {
        System.err.println("Usage: " + CommandLineLauncher.class.getName() + " [-f] PROCESS [-Mname=value]\n" + "  PROCESS       a repository location containing a process\n" + "  -f            interpret PROCESS as a file rather than a repository location (deprecated)\n" + "  -Mname=value  sets the macro \'name\' with the value \'value\'");
        System.exit(1);
    }

    private void run() {
        PlatformUtilities.initialize();
        RapidMiner.init();
        LicenseManager manager = LicenseManagerRegistry.INSTANCE.get();
        boolean compatibleLicense = manager.isAllowed(ProductConstraintManager.INSTANCE.getProduct(), LicenseConstants.SERVER_EDITION_CONSTRAINT, "deployment");
        if(!compatibleLicense) {
            System.err.println("Your license does not allow for using the CLI.");
            System.exit(1);
        }

        Process process = null;

        try {
            if(this.readFromFile) {
                process = RapidMiner.readProcessFile(new File(this.repositoryLocation));
            } else {
                RepositoryProcessLocation e = new RepositoryProcessLocation(new RepositoryLocation(this.repositoryLocation));
                process = e.load((ProgressListener)null);
            }
        } catch (Exception var20) {
            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.RapidMinerCommandLine.reading_process_setup_error", new Object[]{this.repositoryLocation, var20.getMessage()}), var20);
            RapidMiner.quit(ExitMode.ERROR);
        }

        if(process != null) {
            try {
                Iterator e3 = this.macros.iterator();

                while(e3.hasNext()) {
                    Pair debugProperty1 = (Pair)e3.next();
                    process.getContext().addMacro(debugProperty1);
                }

                process.addBreakpointListener(this);
                IOContainer e4 = process.run();
                process.getRootOperator().sendEmail(e4, (Throwable)null);
                LogService.getRoot().log(Level.INFO, "com.rapidminer.RapidMinerCommandLine.process_finished");
                RapidMiner.quit(ExitMode.NORMAL);
            } catch (OutOfMemoryError var21) {
                OutOfMemoryError e2 = var21;
                LogService.getRoot().log(Level.SEVERE, "com.rapidminer.RapidMinerCommandLine.out_of_memory");
                ActionStatisticsCollector.getInstance().log("error", "out_of_memory", String.valueOf(SystemInfoUtilities.getMaxHeapMemorySize()));
                process.getLogger().log(Level.SEVERE, "Here: " + process.getRootOperator().createMarkedProcessTree(10, "==>", process.getCurrentOperator()));

                try {
                    process.getRootOperator().sendEmail((IOContainer)null, e2);
                } catch (UndefinedParameterError var19) {
                    ;
                }
            } catch (DatabaseConstraintViolationException var22) {
                if(var22.getOperatorName() != null) {
                    LogService.getRoot().log(Level.SEVERE, "com.rapidminer.RapidMinerCommandLine.database_constraint_violation_exception_in_operator", new Object[]{var22.getDatabaseURL(), var22.getOperatorName()});
                } else {
                    LogService.getRoot().log(Level.SEVERE, "com.rapidminer.RapidMinerCommandLine.database_constraint_violation_exception", new Object[]{var22.getDatabaseURL()});
                }
            } catch (LicenseViolationException var23) {
                LogService.getRoot().log(Level.SEVERE, "com.rapidminer.RapidMinerCommandLine.operator_constraint_violation_exception", new Object[]{var23.getOperatorName()});
            } catch (Throwable var24) {
                Throwable e1 = var24;
                String debugProperty = ParameterService.getParameterValue("rapidminer.general.debugmode");
                boolean debugMode = Tools.booleanValue(debugProperty, false);
                String message = var24.getMessage();
                if(!debugMode && var24 instanceof RuntimeException) {
                    if(var24.getMessage() != null) {
                        message = "operator cannot be executed (" + var24.getMessage() + "). Check the log messages...";
                    } else {
                        message = "operator cannot be executed. Check the log messages...";
                    }
                }

                process.getLogger().log(Level.SEVERE, "Process failed: " + message, var24);
                process.getLogger().log(Level.SEVERE, "Here: " + process.getRootOperator().createMarkedProcessTree(10, "==>", process.getCurrentOperator()));

                try {
                    process.getRootOperator().sendEmail((IOContainer)null, e1);
                } catch (UndefinedParameterError var18) {
                    ;
                }
            } finally {
                ActionStatisticsCollector.getInstance().log(process.getCurrentOperator(), "FAILURE");
                ActionStatisticsCollector.getInstance().log(process.getCurrentOperator(), "RUNTIME_EXCEPTION");
                LogService.getRoot().severe("Process not successful");
                RapidMiner.quit(ExitMode.ERROR);
            }
        }

    }

    public static void main(String[] argv) {
        setExecutionMode(ExecutionMode.COMMAND_LINE);
//        LicenseManagerRegistry.INSTANCE.set(new DefaultLicenseManager());
//        LicenseManager manager = LicenseManagerRegistry.INSTANCE.get();

//        try {
//            JarVerifier.verify(new Class[]{manager.getClass(), RapidMiner.class, CommandLineLauncher.class});
//        } catch (GeneralSecurityException var3) {
//            System.err.println("Failed to verify RapidMiner Studio installation: " + var3.getMessage());
//            System.exit(1);
//        }

        System.out.println(LICENSE);
        CommandLineLauncher main = new CommandLineLauncher();
        main.parseArguments(argv);
        main.run();
    }

    private static class WaitForKeyThread extends Thread {
        private final Process process;

        public WaitForKeyThread(Process process) {
            this.process = process;
        }

        public void run() {
            try {
                System.in.read();
            } catch (IOException var2) {
                LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.RapidMinerCommandLine.waiting_for_user_input_error", new Object[]{var2.getMessage()}), var2);
            }

            this.process.resume();
        }
    }
}
