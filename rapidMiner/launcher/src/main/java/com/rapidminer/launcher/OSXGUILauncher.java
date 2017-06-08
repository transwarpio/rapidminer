package com.rapidminer.launcher;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.ToolbarGUIStartupListener;
import com.rapidminer.tools.PlatformUtilities;

import java.util.logging.Logger;

public final class OSXGUILauncher {
    private static final Logger LOGGER = Logger.getLogger(GUILauncher.class.getName());

    private OSXGUILauncher() {
        throw new AssertionError("Utility class must not be instantiated.");
    }

    public static void main(String[] args) throws Exception {
        PlatformUtilities.initialize();
//        LicenseManagerRegistry.INSTANCE.set(new DefaultLicenseManager());

//        try {
//            JarVerifier.verify(new Class[]{LicenseManagerRegistry.INSTANCE.get().getClass(), RapidMiner.class, OSXGUILauncher.class});
//        } catch (GeneralSecurityException var2) {
//            LOGGER.log(Level.SEVERE, "Failed to verify RapidMiner Studio installation: " + var2.getMessage(), var2);
//            System.exit(1);
//        }

        LOGGER.info("Launching RapidMiner, platform " + PlatformUtilities.getReleasePlatform());
//        RapidMinerGUI.registerStartupListener(new MarketplaceGUIStartupListener());
//        RapidMinerGUI.registerStartupListener(new OnboardingGUIStartupListener());
//        RapidMinerGUI.registerStartupListener(new LicenseGUIStartupListener());
        RapidMinerGUI.registerStartupListener(new ToolbarGUIStartupListener());
        System.setProperty("com.apple.macos.useScreenMenuBar", "true");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        RapidMinerGUI.main(args);
    }
}
