//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.rapidminer.gui.actions;

import com.rapidminer.gui.license.LicenseDialog;
import com.rapidminer.gui.tools.ResourceAction;
import java.awt.event.ActionEvent;

public class LicenseAction extends ResourceAction {
    private static final long serialVersionUID = 1L;

    public LicenseAction() {
        super("license_manage", new Object[0]);
        this.setCondition(9, 0);
    }

    public void actionPerformed(ActionEvent e) {
        (new LicenseDialog(new Object[0])).setVisible(true);
    }
}
