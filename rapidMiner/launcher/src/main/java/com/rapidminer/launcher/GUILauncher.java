package com.rapidminer.launcher;

import com.rapidminer.RapidMiner;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.ToolbarGUIStartupListener;
import com.rapidminer.tools.OperatorService;
import com.rapidminer.tools.PlatformUtilities;
import io.transwarp.midas.MidasRuntimeHook;
import io.transwarp.midas.ui.StartupSessionListener;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class GUILauncher {
    private static final Logger LOGGER = Logger.getLogger(GUILauncher.class.getName());
    private static JProgressBar bar;
    private static JFrame dialog;

    private GUILauncher() {
    }
    private static boolean updateGUI(File rmHome, File updateZip, File updateScript) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    GUILauncher.dialog = (new JFrame("Updating RapidMiner"));
                    GUILauncher.bar = (new JProgressBar(0, 1000));
                    GUILauncher.dialog.setLayout(new BorderLayout());
                    GUILauncher.dialog.add(new JLabel("Updating RapidMiner"), "North");
                    GUILauncher.dialog.add(GUILauncher.bar, "Center");
                    GUILauncher.dialog.pack();
                    GUILauncher.dialog.setLocationRelativeTo(null);
                    GUILauncher.dialog.setVisible(true);
                }
            });
        } catch (Exception var7) {
            LOGGER.log(Level.SEVERE, "Cannot show update dialog.", var7);
            return false;
        }

        boolean success = true;
        if(updateZip != null) {
            try {
                success &= updateDiffZip(rmHome, updateZip, true);
            } catch (Exception var6) {
                LOGGER.log(Level.WARNING, "Update from " + updateZip + " failed: " + var6, var6);
                JOptionPane.showMessageDialog(dialog, "Update from " + updateZip + " failed: " + var6, "Update Failed", 0);
                success = false;
            }
        }

        if(updateScript != null) {
            try {
                success &= executeUpdateScript(rmHome, new FileInputStream(updateScript), true);
                if(!updateScript.delete()) {
                    updateScript.deleteOnExit();
                }
            } catch (IOException var5) {
                LOGGER.log(Level.WARNING, "Update script " + updateScript + " failed: " + var5, var5);
                JOptionPane.showMessageDialog(dialog, "Update from " + updateScript + " failed: " + var5, "Update Failed", 0);
                success = false;
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUILauncher.dialog.dispose();
            }
        });
        return success;
    }

    private static boolean executeUpdateScript(File rmHome, InputStream in, boolean gui) throws IOException {
        boolean updateReader;
        try {
            HashSet e = new HashSet();
            BufferedReader updateReader1 = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line;
            while((line = updateReader1.readLine()) != null) {
                String[] split = line.split(" ", 2);
                if(split.length != 2) {
                    LOGGER.warning("Ignoring unparseable update script entry: " + line);
                }

                if("DELETE".equals(split[0])) {
                    e.add(split[1].trim());
                } else {
                    LOGGER.warning("Ignoring unparseable update script entry: " + line);
                }
            }

            Iterator split1 = e.iterator();

            while(split1.hasNext()) {
                String string = (String)split1.next();
                File file = new File(rmHome, string);
                LOGGER.info("DELETE " + file);
                if(!file.delete()) {
                    file.deleteOnExit();
                }
            }

            boolean split2 = true;
            return split2;
        } catch (IOException var17) {
            LOGGER.log(Level.SEVERE, "Cannot read update script: " + var17, var17);
            if(gui) {
                JOptionPane.showMessageDialog(dialog, "Cannot read update script: " + var17, "Update Failed", 0);
            }

            updateReader = false;
        } finally {
            try {
                in.close();
            } catch (IOException var16) {
                ;
            }

        }

        return updateReader;
    }

    private static boolean updateDiffZip(File rmHome, File updateZip, boolean gui) {
        LOGGER.info("Updating using update file " + updateZip);
        ZipFile zip = null;

        label646: {
            boolean enumeration;
            try {
                zip = new ZipFile(updateZip);
                break label646;
            } catch (Exception var81) {
                LOGGER.log(Level.SEVERE, "Update file corrupt: " + var81, var81);
                if(gui) {
                    JOptionPane.showMessageDialog(dialog, "Update file corrupt: " + var81, "Update Failed", 0);
                }

                enumeration = false;
            } finally {
                if(zip != null) {
                    try {
                        zip.close();
                    } catch (IOException var70) {
                        ;
                    }
                }

            }

            return enumeration;
        }

        final int size = zip.size();
        Enumeration var83 = zip.entries();
        int i = 0;

        while(true) {
            ZipEntry updateEntry;
            String ex;
            do {
                do {
                    if(!var83.hasMoreElements()) {
                        updateEntry = zip.getEntry("META-INF/UPDATE");
                        if(updateEntry != null) {
                            try {
                                executeUpdateScript(rmHome, zip.getInputStream(updateEntry), gui);
                            } catch (IOException var75) {
                                LOGGER.log(Level.SEVERE, "Cannot read update script: " + var75, var75);
                                if(gui) {
                                    JOptionPane.showMessageDialog(dialog, "Cannot read update script: " + var75, "Update Failed", 0);
                                }
                            }
                        }

                        try {
                            zip.close();
                        } catch (IOException var74) {
                            LOGGER.log(Level.WARNING, "Cannot close update file: " + var83, var83);
                        }

                        try {
                            if(updateZip.delete()) {
                                return true;
                            }

                            JOptionPane.showMessageDialog(dialog, "Could not delete update file " + updateZip + ". Probably you do not have administrator permissions. Please delete this file manually.", "Update Failed", 0);
                            return false;
                        } catch (Exception var73) {
                            JOptionPane.showMessageDialog(dialog, "Could not delete update file " + updateZip + ". Probably you do not have administrator permissions. Please delete this file manually.", "Update Failed", 0);
                            return false;
                        }
                    }

                    ++i;
                    updateEntry = (ZipEntry)var83.nextElement();
                } while(updateEntry.isDirectory());

                ex = updateEntry.getName();
            } while("META-INF/UPDATE".equals(ex));

            if(ex.startsWith("rapidminer/")) {
                ex = ex.substring("rapidminer/".length());
            }

            File dest = new File(rmHome, ex);

            try {
                InputStream e2 = zip.getInputStream(updateEntry);
                Throwable var11 = null;

                try {
                    FileOutputStream out = new FileOutputStream(dest);
                    Throwable var13 = null;

                    try {
                        LOGGER.info("UPDATE " + dest);
                        File parent = dest.getParentFile();
                        if(parent != null && !parent.exists() && !parent.mkdirs()) {
                            JOptionPane.showMessageDialog(dialog, "Cannot create directory " + parent.toString(), "Update Failed", 0);
                        }
                        final int fi = i;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                GUILauncher.bar.setValue(fi * 1000 / size);
                            }
                        });
                        byte[] buf = new byte[10240];

                        int length;
                        while((length = e2.read(buf)) != -1) {
                            out.write(buf, 0, length);
                        }
                    } catch (Throwable var76) {
                        var13 = var76;
                        throw var76;
                    } finally {
                        if(out != null) {
                            if(var13 != null) {
                                try {
                                    out.close();
                                } catch (Throwable var72) {
                                    var13.addSuppressed(var72);
                                }
                            } else {
                                out.close();
                            }
                        }

                    }
                } catch (Throwable var78) {
                    var11 = var78;
                    throw var78;
                } finally {
                    if(e2 != null) {
                        if(var11 != null) {
                            try {
                                e2.close();
                            } catch (Throwable var71) {
                                var11.addSuppressed(var71);
                            }
                        } else {
                            e2.close();
                        }
                    }

                }
            } catch (Exception var80) {
                LOGGER.log(Level.WARNING, "Updating " + dest + " failed: " + var80, var80);
                if(gui) {
                    JOptionPane.showMessageDialog(dialog, "Updating " + dest + " failed: " + var80, "Update Failed", 0);
                }

                return false;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        PlatformUtilities.initialize();
//        LicenseManagerRegistry.INSTANCE.set(new DefaultLicenseManager());

//        try {
//            JarVerifier.verify(new Class[]{LicenseManagerRegistry.INSTANCE.get().getClass(), RapidMiner.class, GUILauncher.class});
//        } catch (GeneralSecurityException var7) {
//            LOGGER.log(Level.SEVERE, "Failed to verify RapidMiner Studio installation: " + var7.getMessage(), var7);
//            System.exit(1);
//        }

        LOGGER.info("Launching RapidMiner, platform " + PlatformUtilities.getReleasePlatform());
        String rapidMinerHomeProperty = System.getProperty("rapidminer.home");
        if(rapidMinerHomeProperty == null) {
            LOGGER.info("RapidMiner HOME is not set. Ignoring potential update installation. (If that happens, you weren\'t able to download updates anyway.)");
        } else {
            System.out.println(rapidMinerHomeProperty);
            File rmHome = new File(rapidMinerHomeProperty);
            File updateDir = new File(rmHome, "update");
            File updateScript = new File(updateDir, "UPDATE");
            if(!updateScript.exists()) {
                updateScript = null;
            }

            File[] updates = updateDir.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().startsWith("rmupdate");
                }
            });
            File updateZip = null;
            if(updates != null) {
                switch(updates.length) {
                    case 0:
                        break;
                    case 1:
                        updateZip = updates[0];
                        break;
                    default:
                        LOGGER.warning("Multiple updates found: " + Arrays.toString(updates) + ". Ignoring all.");
                }
            }

            if((updateZip != null || updateScript != null) && updateGUI(rmHome, updateZip, updateScript)) {
                RapidMiner.relaunch();
                return;
            }
        }
        // Init the global variables
        MidasRuntimeHook.init();
        MidasRuntimeHook.setMidasHome(rapidMinerHomeProperty);
        
        registerGUIStartupListeners();
        RapidMinerGUI.main(args);
    }

    private static void registerGUIStartupListeners() {
//        RapidMinerGUI.registerStartupListener(new MarketplaceGUIStartupListener());
//        RapidMinerGUI.registerStartupListener(new OnboardingGUIStartupListener());
//        RapidMinerGUI.registerStartupListener(new LicenseGUIStartupListener());
        RapidMinerGUI.registerStartupListener(new StartupSessionListener());
        RapidMinerGUI.registerStartupListener(new ToolbarGUIStartupListener());
    }

    public static String getLongVersion() {
        String version = GUILauncher.class.getPackage().getImplementationVersion();
        if(version == null) {
            LOGGER.info("Implementation version not set.");
            return "?.?.?";
        } else {
            return version.split("-")[0];
        }
    }

    public static String getShortVersion() {
        String version = getLongVersion();
        int lastDot = version.lastIndexOf(46);
        return lastDot != -1?version.substring(0, lastDot):version;
    }

    public static boolean isDevelopmentBuild() {
        return PlatformUtilities.getReleasePlatform() == null;
    }
}