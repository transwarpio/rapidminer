package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.dialogs.ConfirmDialog;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.KeyStroke;

public class UpdateConfirmDialog extends ConfirmDialog {
    private static final long serialVersionUID = 1L;

    public UpdateConfirmDialog() {
        super(ApplicationFrame.getApplicationFrame(), MarketplaceUpdateManager.useOSXUpdateMechansim()?"updates_exist_osx":"updates_exist", 0, false, new Object[0]);
    }

    protected JButton makeYesButton() {
        final String i18n = MarketplaceUpdateManager.useOSXUpdateMechansim()?"update.osx.confirm.yes":"update.confirm.yes";
        JButton yesButton = new JButton(new ResourceAction(i18n, new Object[0]) {
            private static final long serialVersionUID = -8887199234055845095L;

            public void actionPerformed(ActionEvent e) {
                UpdateConfirmDialog.this.returnOption = 0;
                UpdateConfirmDialog.this.yes();
            }
        });
        this.getRootPane().setDefaultButton(yesButton);
        return yesButton;
    }

    protected JButton makeNoButton() {
        ResourceAction noAction = new ResourceAction("update.confirm.no", new Object[0]) {
            private static final long serialVersionUID = -8887199234055845095L;

            public void actionPerformed(ActionEvent e) {
                UpdateConfirmDialog.this.returnOption = 1;
                UpdateConfirmDialog.this.no();
            }
        };
        this.getRootPane().getInputMap(2).put(KeyStroke.getKeyStroke(27, 0, false), "NO");
        this.getRootPane().getActionMap().put("NO", noAction);
        return new JButton(noAction);
    }
}
