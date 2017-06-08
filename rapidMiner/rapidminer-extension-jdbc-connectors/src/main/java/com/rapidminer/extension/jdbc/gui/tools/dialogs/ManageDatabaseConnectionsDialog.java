package com.rapidminer.extension.jdbc.gui.tools.dialogs;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.gui.tools.dialogs.DatabaseConnectionDialog;
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.repository.Repository;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.repository.RepositoryManager;
import com.rapidminer.tools.LogService;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ManageDatabaseConnectionsDialog extends DatabaseConnectionDialog {
    private static final long serialVersionUID = -1314039924713463923L;

    public ManageDatabaseConnectionsDialog() {
        this(ApplicationFrame.getApplicationFrame());
    }

    public ManageDatabaseConnectionsDialog(Window owner) {
        super(owner, "manage_db_connections", new Object[0]);
        Collection buttons = this.makeButtons();
        JButton okButton = this.makeOkButton();
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ManageDatabaseConnectionsDialog.this.saveConnectionAction.actionPerformed((ActionEvent)null);
                refreshDB();
            }
        });
        JPanel allButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel entryButtonPanel = new JPanel(new FlowLayout(0, 6, 6));
        Iterator generalButtonPanel = buttons.iterator();

        while(generalButtonPanel.hasNext()) {
            AbstractButton button = (AbstractButton)generalButtonPanel.next();
            if(button != null) {
                entryButtonPanel.add(button);
            }
        }

        JPanel generalButtonPanel1 = new JPanel(new FlowLayout(2, 6, 6));
        generalButtonPanel1.add(okButton);
        generalButtonPanel1.add(this.makeCancelButton());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = 0;
        gbc.weightx = 0.0D;
        allButtonPanel.add(entryButtonPanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = 2;
        gbc.weightx = 1.0D;
        allButtonPanel.add(new JLabel(), gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = 0;
        gbc.weightx = 0.0D;
        allButtonPanel.add(generalButtonPanel1, gbc);
        this.layoutDefault(this.makeConnectionManagementPanel(), allButtonPanel, 3);
    }

    @Override
    protected void cancel() {
        super.cancel();
        refreshDB();
    }

    private void refreshDB() {
        try {
            Repository dbRepository = RepositoryManager.getInstance(null).getRepository("DB");
            dbRepository.refresh();
        } catch (RepositoryException e1) {
            LogService.getRoot().warning("Repository DB is missing");
        }
    }
}
