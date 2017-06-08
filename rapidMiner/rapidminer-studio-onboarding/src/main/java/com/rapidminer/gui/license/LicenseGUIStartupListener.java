//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rapidminer.gui.license;

import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.gui.MainFrame;
import com.rapidminer.gui.actions.LicenseAction;
import com.rapidminer.gui.internal.GUIStartupListener;
import com.rapidminer.gui.license.GUILicenseManagerListener;

public class LicenseGUIStartupListener implements GUIStartupListener {
    public LicenseGUIStartupListener() {
    }

    public void splashWillBeShown() {
    }

    public void mainFrameInitialized(MainFrame mainFrame) {
        mainFrame.getToolsMenu().addSeparator();
        mainFrame.getToolsMenu().add(new LicenseAction());
        mainFrame.addExtendedProcessEditor(new ConstraintValidationProcessEditor());
        ProductConstraintManager.INSTANCE.registerLicenseManagerListener(GUILicenseManagerListener.INSTANCE);
    }

    public void splashWasHidden() {
    }

    public void startupCompleted() {
    }
}
