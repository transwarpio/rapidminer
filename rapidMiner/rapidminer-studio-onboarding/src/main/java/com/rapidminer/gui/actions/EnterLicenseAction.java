package com.rapidminer.gui.actions;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.gui.license.LicenseEnteringDialog;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.license.LicenseValidationException;
import com.rapidminer.license.UnknownProductException;
import com.rapidminer.license.location.LicenseStoringException;
import java.awt.Window;
import java.awt.event.ActionEvent;

public class EnterLicenseAction extends ResourceAction {
    private static final long serialVersionUID = 1L;
    private Window owner;

    public EnterLicenseAction() {
        this((Window)null);
    }

    public EnterLicenseAction(Window owner) {
        super("enter_license", new Object[0]);
        this.owner = owner;
    }

    public void actionPerformed(ActionEvent e) {
        LicenseEnteringDialog enterLicenseDialog = new LicenseEnteringDialog(this.owner, new Object[0]);
        enterLicenseDialog.setVisible(true);
        if(enterLicenseDialog.wasConfirmed()) {
            this.installNewLicense(enterLicenseDialog.getInputText());
        }

    }

    private void installNewLicense(String licenseString) {
        if(licenseString != null && !licenseString.isEmpty()) {
            try {
                ProductConstraintManager.INSTANCE.installNewLicense(licenseString);
            } catch (UnknownProductException | LicenseValidationException | LicenseStoringException var3) {
                SwingTools.showSimpleErrorMessage("license.storing_failed", var3, new Object[0]);
            }

        }
    }
}
