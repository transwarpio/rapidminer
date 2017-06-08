package io.transwarp.midas.ui.actions;

import com.rapidminer.gui.tools.ResourceAction;
import io.transwarp.midas.ui.dialog.ManageCustomOpDialog;

import java.awt.event.ActionEvent;

public class ManageCustomOpAction extends ResourceAction {

    private static final long serialVersionUID = 4675057674892640002L;

    public ManageCustomOpAction() {
        super("manage_custom_op");
    }

    /**
     * Opens the settings dialog
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        new ManageCustomOpDialog().setVisible(true);
    }
}
