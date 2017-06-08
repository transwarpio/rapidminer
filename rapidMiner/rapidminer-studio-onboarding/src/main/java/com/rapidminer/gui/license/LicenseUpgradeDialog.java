package com.rapidminer.gui.license;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.components.LinkLocalButton;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.I18N;
import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class LicenseUpgradeDialog extends ButtonDialog {
    private static final long serialVersionUID = 7329771268434788584L;
    private static final String GET_MORE_EDITIONS_URL = I18N.getGUILabel("onboarding.more_editions.url", new Object[0]);
    private final ResourceAction action;
    private final String productName;
    private final String productEdition;
    private boolean useResourceAction;

    public LicenseUpgradeDialog(ResourceAction action, boolean isDowngradeInformation, String key, boolean modal, boolean forceLicenseInstall, Object... arguments) {
        this(action, isDowngradeInformation, key, modal, forceLicenseInstall, false, arguments);
    }

    public LicenseUpgradeDialog(ResourceAction action, boolean isDowngradeInformation, String key, boolean modal, boolean forceLicenseInstall, boolean useResourceAction, Object... arguments) {
        this(action, isDowngradeInformation, key, modal, forceLicenseInstall, useResourceAction, 8, arguments);
    }

    public LicenseUpgradeDialog(ResourceAction action, boolean isDowngradeInformation, String key, boolean modal, boolean forceLicenseInstall, boolean useResourceAction, int size, Object... arguments) {
        super(ApplicationFrame.getApplicationFrame(), key, modal?ModalityType.APPLICATION_MODAL:ModalityType.MODELESS, arguments);
        this.setResizable(false);
        this.action = action;
        this.useResourceAction = useResourceAction;
        if(arguments.length < 2) {
            throw new IllegalArgumentException("You need to specify at least the name and the edition of the product that needs to be upgraded.");
        } else {
            this.setModal(modal);
            this.productName = String.valueOf(arguments[0]);
            this.productEdition = isDowngradeInformation?String.valueOf(arguments[2]):String.valueOf(arguments[1]);
            this.layoutDefault(this.makeButtonPanel(), size, new AbstractButton[0]);
            if(forceLicenseInstall) {
                this.setDefaultCloseOperation(0);
            }

        }
    }

    protected String getProductName() {
        return this.productName;
    }

    protected String getProductEdition() {
        return this.productEdition;
    }

    protected JPanel makeButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(2, 6, 6));
        buttonPanel.add(this.makeContinueButton());
        buttonPanel.add(this.makeUpgradeButton());
        return buttonPanel;
    }

    protected JComponent makeContinueButton() {
        ResourceAction continueAction = new ResourceAction("license.continue_with_link", new Object[]{this.getProductName(), this.getProductEdition()}) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                LicenseUpgradeDialog.this.cancel();
            }
        };
        return new LinkLocalButton(continueAction);
    }

    protected JComponent makeUpgradeButton() {
        JButton upgradeButton = this.makeOkButton(this.action.getKey());
        upgradeButton.requestFocusInWindow();
        return upgradeButton;
    }

    protected void ok() {
        if(this.useResourceAction) {
            this.action.actionPerformed((ActionEvent)null);
        } else {
            OnboardingDialog.openURL(GET_MORE_EDITIONS_URL);
        }

        super.ok();
    }
}
