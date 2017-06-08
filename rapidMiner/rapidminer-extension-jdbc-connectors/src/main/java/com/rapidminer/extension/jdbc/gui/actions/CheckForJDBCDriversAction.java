package com.rapidminer.extension.jdbc.gui.actions;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.gui.tools.JDBCDriverTable;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseService;
import com.rapidminer.extension.jdbc.tools.jdbc.DriverInfo;
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.gui.tools.dialogs.ButtonDialog.ButtonDialogBuilder;
import com.rapidminer.gui.tools.dialogs.ButtonDialog.ButtonDialogBuilder.DefaultButtons;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

public class CheckForJDBCDriversAction extends ResourceAction {
    private static final long serialVersionUID = -3497263063489866721L;

    public CheckForJDBCDriversAction() {
        super("show_database_drivers", new Object[0]);
    }

    public void actionPerformed(ActionEvent e) {
        if(DatabaseService.checkCommercialDatabaseConstraint("jdbc.manage_drivers") == null) {
            DriverInfo[] drivers = DatabaseService.getAllDriverInfos();
            JDBCDriverTable driverTable = new JDBCDriverTable(drivers);
            driverTable.setBorder((Border)null);
            JScrollPane driverTablePane = new JScrollPane(driverTable);
            driverTablePane.setBorder((Border)null);
            ButtonDialogBuilder builder = new ButtonDialogBuilder("jdbc_drivers");
            ButtonDialog dialog = builder.setOwner(ApplicationFrame.getApplicationFrame()).setModalityType(ModalityType.APPLICATION_MODAL).setContent(driverTablePane, 1).setButtons(new DefaultButtons[]{DefaultButtons.CLOSE_BUTTON}).build();
            dialog.setVisible(true);
        }

    }
}
