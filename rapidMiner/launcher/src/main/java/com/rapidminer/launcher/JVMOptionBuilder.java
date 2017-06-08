package com.rapidminer.launcher;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.tools.FileSystemService;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.PlatformUtilities;
import com.rapidminer.tools.SystemInfoUtilities;
import com.rapidminer.tools.SystemInfoUtilities.JVMArch;
import com.rapidminer.tools.SystemInfoUtilities.OperatingSystem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class JVMOptionBuilder {
    private static final String LAUNCHER_LOG = "launcher.log";
    private static final String ARGUMENTS_PROTOCOL_HANDLER = " -Xmx128m -XX:InitiatingHeapOccupancyPercent=0 -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true";
    private static final long MINIMUM_RM_MEMORY = 384L;
    private static final long MAX_32BIT_MEMORY = 1000L;
    private static final long MEMORY_TRESHOLD = 2000L;
    private static final String WINDOWS_CLASSPATH_SEPARATOR = ";";
    private static final String UNIX_CLASSPATH_SEPARTOR = ":";
    private static final Logger LOGGER = Logger.getLogger(JVMOptionBuilder.class.getSimpleName());
    private static boolean verbose = false;

    private JVMOptionBuilder() {
        throw new AssertionError();
    }

    private static void addSystemSpecificSettings(StringBuilder builder) {
        if(SystemInfoUtilities.getOperatingSystem() == OperatingSystem.OSX) {
            String dockIconPath = null;
            String platformIndependent = PlatformUtilities.getRapidMinerHome() + "/RapidMiner Studio.app/Contents/Resources/rapidminer_frame_icon.icns";
            if(Files.exists(Paths.get(platformIndependent, new String[0]), new LinkOption[0])) {
                dockIconPath = platformIndependent;
            } else {
                dockIconPath = PlatformUtilities.getRapidMinerHome() + "/../rapidminer_frame_icon.icns";
            }

            builder.append(" -Xdock:icon=");
            builder.append(escapeBlanks(dockIconPath));
            builder.append(" -Xdock:name=");
            builder.append(escapeBlanks("RapidMiner"));
            builder.append(" -Dcom.apple.mrj.application.apple.menu.about.name=");
            builder.append(escapeBlanks("RapidMiner"));
            builder.append(" -Dapple.laf.useScreenMenuBar=true");
            builder.append(" -Dcom.apple.mrj.application.growbox.intrudes=true");
            builder.append(" -Dapple.awt.antialiasing=true");
            builder.append(" -Dcom.apple.mrj.application.live-resize=true");
            builder.append(" -Dsun.java2d.opengl=true");
        } else if(SystemInfoUtilities.getOperatingSystem() == OperatingSystem.WINDOWS) {
            builder.append(" -Djava.net.preferIPv4Stack=true");
        }

    }

    private static String escapeBlanks(String path) {
        return SystemInfoUtilities.getOperatingSystem() != OperatingSystem.OSX && SystemInfoUtilities.getOperatingSystem() != OperatingSystem.SOLARIS && SystemInfoUtilities.getOperatingSystem() != OperatingSystem.UNIX?"\"" + path + "\"":path.replace(" ", "\\ ");
    }

    private static void addMemorySettings(StringBuilder builder, long userMaxMemorySetting) {
        log("Calculating JVM memory settings...");
        long totalPhysicalMemorySize = 384L;

        try {
            totalPhysicalMemorySize = SystemInfoUtilities.getTotalPhysicalMemorySize().longValue();
            log("Total physical memory detected: " + totalPhysicalMemorySize);
        } catch (IOException var9) {
            log("Could not detect total physical memory.. assuming at least 384mb");
        }

        long memoryLimit = -1L;
        if((double)totalPhysicalMemorySize * 0.1D > 2000.0D) {
            memoryLimit = totalPhysicalMemorySize - 2000L;
        } else {
            memoryLimit = (long)((double)totalPhysicalMemorySize * 0.9D);
        }

        log("Calculating maximum usable memory for RapidMiner... ");
        log("Set maximum usable memory to " + memoryLimit + "mb");
        if(memoryLimit < 384L) {
            memoryLimit = 384L;
            log("Maximum usable memory is below minimum memory for RapidMiner! Set maximum usable memory to " + memoryLimit);
        } else if(SystemInfoUtilities.getJVMArchitecture() == JVMArch.THIRTY_TWO && memoryLimit > 1000L) {
            memoryLimit = 1000L;
            log("Maxmimum usable memory is above maximum memory for a 32bit JVM. Set maximum to " + memoryLimit);
        }

        log("Using up to " + memoryLimit + "mb of memory.");
        if(SystemInfoUtilities.getJVMArchitecture() == JVMArch.THIRTY_TWO && SystemInfoUtilities.getOperatingSystem() == OperatingSystem.WINDOWS) {
            long maxPermSize = SystemInfoUtilities.getFreePhysicalMemorySize().longValue();
            if(maxPermSize != -1L && maxPermSize >= 384L && memoryLimit > maxPermSize) {
                memoryLimit = maxPermSize;
                log("Only " + maxPermSize + "mb of free memory available, using " + maxPermSize + "mb of memory.");
            }
        }

        if(userMaxMemorySetting >= 384L) {
            if(memoryLimit > userMaxMemorySetting) {
                log("Max allowed memory has been set in the RapidMiner preferences to " + userMaxMemorySetting + "mb. Only using " + memoryLimit + "mb of memory.");
                memoryLimit = userMaxMemorySetting;
            }
        } else {
            log("Max allowed memory has been set to less than 384mb. Ignoring because RapidMiner must use at least 384mb.");
        }

        builder.append(" -Xms");
        builder.append(384L);
        builder.append("m");
        builder.append(" -Xmx");
        builder.append(memoryLimit);
        builder.append("m");
        int maxPermSize1 = Math.min(128, (int)memoryLimit / 2 / 2 / 2);
        maxPermSize1 = Math.max(128, maxPermSize1);
        builder.append(" -XX:MaxPermSize=" + maxPermSize1 + "m");
    }

    private static void addGarbageCollection(StringBuilder builder) {
        int numberOfParallelGCThreads = SystemInfoUtilities.getNumberOfProcessors();
        int numberOfConcGCThreads = numberOfParallelGCThreads - 1;
        if(numberOfConcGCThreads < 1) {
            numberOfConcGCThreads = 1;
        }

        builder.append(" -XX:ConcGCThreads=");
        builder.append(numberOfConcGCThreads);
        builder.append(" -XX:ParallelGCThreads=");
        builder.append(numberOfParallelGCThreads);
        builder.append(" -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:InitiatingHeapOccupancyPercent=0");
        log("Using G1 garbace collection with " + numberOfParallelGCThreads + " parallel" + " and " + numberOfConcGCThreads + " concurrent GC threads.");
    }

    private static void addClassPath(StringBuilder builder) {
        builder.append("-cp ");
        builder.append("\"");
        String classPathSeperator = ";";
        if(SystemInfoUtilities.getOperatingSystem() != OperatingSystem.WINDOWS) {
            classPathSeperator = ":";
        }

        log("Classpath seperator: " + classPathSeperator);
        File libFolder = new File(PlatformUtilities.getRapidMinerHome(), "lib");
        addLibsInFolder(libFolder, builder, classPathSeperator);
        File jdbcFolder = new File(libFolder, "jdbc");
        addLibsInFolder(jdbcFolder, builder, classPathSeperator);
        File freehepFolder = new File(libFolder, "freehep");
        addLibsInFolder(freehepFolder, builder, classPathSeperator);
        builder.append("\"");
    }

    private static void addLibsInFolder(File folder, StringBuilder builder, String classPathSeperator) {
        if(folder.isDirectory()) {
            File[] var3 = folder.listFiles();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File lib = var3[var5];
                if(lib.isFile() && lib.getName().contains(".jar")) {
                    log("Adding lib to classpath: " + lib);
                    builder.append(lib.getAbsolutePath());
                    builder.append(classPathSeperator);
                }
            }

        }
    }

    public static String getJVMOptions(boolean addClasspath, Long userMaxMemorySetting) {
        PlatformUtilities.ensureRapidMinerHomeSet(Level.OFF);
        StringBuilder builder = new StringBuilder();
        if(addClasspath) {
            addClassPath(builder);
        }

        addGarbageCollection(builder);
        addMemorySettings(builder, userMaxMemorySetting.longValue());
        addSystemSpecificSettings(builder);
        return builder.toString();
    }

    public static void main(String[] args) {
        try {
            LogService.getRoot().setLevel(Level.OFF);
            Handler[] addClasspath = LOGGER.getHandlers();
            int jvmOptions = addClasspath.length;

            int var3;
            for(var3 = 0; var3 < jvmOptions; ++var3) {
                Handler entry = addClasspath[var3];
                LOGGER.removeHandler(entry);
            }

            try {
                FileHandler var17 = new FileHandler((new File(FileSystemService.getUserRapidMinerDir(), "launcher.log")).getAbsolutePath(), false);
                var17.setLevel(Level.ALL);
                var17.setFormatter(new SimpleFormatter());
                LOGGER.addHandler(var17);
                LOGGER.setUseParentHandlers(false);
                LOGGER.setLevel(Level.ALL);
            } catch (IOException var13) {
                ;
            }

            StringBuilder var20;
            Iterator var25;
            try {
                List var18 = ManagementFactory.getRuntimeMXBean().getInputArguments();
                if(var18 != null) {
                    var20 = new StringBuilder();
                    var25 = var18.iterator();

                    while(true) {
                        if(!var25.hasNext()) {
                            log("JVM arguments were: " + var20.toString());
                            break;
                        }

                        String var26 = (String)var25.next();
                        var20.append(var26);
                        var20.append(" ");
                    }
                }
            } catch (Throwable var14) {
                log("Failed to read JVM arguments!");
                var14.printStackTrace();
            }

            try {
                Properties var19 = System.getProperties();
                if(var19 != null) {
                    var20 = new StringBuilder();
                    var25 = var19.entrySet().iterator();

                    while(true) {
                        if(!var25.hasNext()) {
                            log("System properties were: " + var20.toString());
                            break;
                        }

                        Entry var27 = (Entry)var25.next();
                        var20.append(var27.getKey());
                        var20.append(":\'" + var27.getValue() + "\'");
                        var20.append(" ");
                    }
                }
            } catch (Throwable var15) {
                log("Failed to read system properties!");
                var15.printStackTrace();
            }

            if(args.length > 0 && args[0] != null && args[0].startsWith("rapidminer://")) {
                Socket var21 = getOtherInstance();
                if(var21 != null) {
                    try {
                        var21.close();
                    } catch (IOException var12) {
                        ;
                    }

                    log("Running instance found. Starting minimal version.");
                    System.out.println(" -Xmx128m -XX:InitiatingHeapOccupancyPercent=0 -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true");
                    System.exit(0);
                } else {
                    log("No running instance found. Regular startup.");
                }
            }

            boolean var22 = false;
            String[] var23 = args;
            var3 = args.length;

            for(int var28 = 0; var28 < var3; ++var28) {
                String element = var23[var28];
                if(element != null) {
                    if("--verbose-startup".equals(element)) {
                        verbose = true;
                    }

                    if("--addcp".equals(element)) {
                        var22 = true;
                    }
                }
            }

            if(verbose) {
                SystemInfoUtilities.logEnvironmentInfos();
            }

            String var24 = getJVMOptions(var22, readUserMaxMemorySetting());
            log("Launch settings are: \'" + var24 + "\'");
            System.out.println(var24);
        } finally {
            System.exit(0);
        }

    }

    private static void log(String toLog) {
        LOGGER.log(Level.INFO, toLog);
    }

    private static Long readUserMaxMemorySetting() {
        Long maxValue = Long.valueOf(9223372036854775807L);
        Properties rmPreferences = new Properties();

        try {
            InputStream e = Files.newInputStream(FileSystemService.getMainUserConfigFile().toPath(), new OpenOption[0]);
            Throwable var3 = null;

            try {
                log("Trying to read user setting for maximum amount of memory.");
                rmPreferences.load(e);
                String maxUserMemory = rmPreferences.getProperty("maxMemory");
                if(maxUserMemory != null && !maxUserMemory.isEmpty()) {
                    maxValue = Long.valueOf(Long.parseLong(maxUserMemory));
                    log("User setting for maximum amount of memory: " + maxValue);
                } else {
                    log("No user setting for maximum amount of memory found.");
                }
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        e.close();
                    }
                }

            }
        } catch (Exception var15) {
            log("Failed to read RM preferences for user specified max memory: " + var15.getMessage());
        }

        return maxValue;
    }

    private static Socket getOtherInstance() {
        File socketFile = FileSystemService.getUserConfigFile("midas.lock");
        if(!socketFile.exists()) {
            return null;
        } else {
            int port;
            try {
                label130: {
                    BufferedReader e = new BufferedReader(new InputStreamReader(new FileInputStream(socketFile), StandardCharsets.UTF_8));
                    Throwable var3 = null;

                    Object var5;
                    try {
                        String portStr = e.readLine();
                        if(portStr != null) {
                            port = Integer.parseInt(portStr);
                            break label130;
                        }

                        log("Faild to retrieve port from socket file \'" + socketFile + "\'. File seems to be empty.");
                        var5 = null;
                    } catch (Throwable var18) {
                        var3 = var18;
                        throw var18;
                    } finally {
                        if(e != null) {
                            if(var3 != null) {
                                try {
                                    e.close();
                                } catch (Throwable var16) {
                                    var3.addSuppressed(var16);
                                }
                            } else {
                                e.close();
                            }
                        }

                    }

                    return (Socket)var5;
                }
            } catch (Exception var20) {
                log("Failed to read socket file \'" + socketFile + "\': " + var20.getMessage());
                return null;
            }

            log("Checking for running instance on port " + port + ".");

            try {
                return new Socket(InetAddress.getLoopbackAddress(), port);
            } catch (IOException var17) {
                log("Found lock file but no other instance running. Assuming unclean shutdown of previous launch.");
                return null;
            }
        }
    }
}
