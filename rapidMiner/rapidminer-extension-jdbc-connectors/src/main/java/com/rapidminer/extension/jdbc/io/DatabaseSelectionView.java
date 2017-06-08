package com.rapidminer.extension.jdbc.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.gui.tools.dialogs.DatabaseConnectionDialog;
import com.rapidminer.extension.jdbc.gui.tools.dialogs.ManageDatabaseConnectionsDialog;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.DatabaseConnectionService;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.components.FixedWidthLabel;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

final class DatabaseSelectionView extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String LOADING_TEXT = I18N.getGUILabel("manage_db_connections.testing.label", new Object[0]);
    private static final String NO_SELECTION_MESSAGE = I18N.getGUILabel("manage_db_connections.no_selection.label", new Object[0]);
    private static final ImageIcon LOADING_ICON = SwingTools.createIcon("16/loading.gif");
    private final ManageDatabaseConnectionsDialog manageConnectionsdialog;
    private final JLabel testLabel;
    private List<ChangeListener> changeListeners = new LinkedList();
    private boolean testFailed = false;
    private JButton editButton;
    private JButton testButton;

    DatabaseSelectionView(Window owner) {
        this.manageConnectionsdialog = new ManageDatabaseConnectionsDialog(owner);
        this.setLayout(new BorderLayout());
        JList connectionList = this.manageConnectionsdialog.getConnectionListCopy();
        ExtendedJScrollPane connectionsPanel = new ExtendedJScrollPane(connectionList);
        connectionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 250, 10, 250), BorderFactory.createLineBorder(Color.LIGHT_GRAY)));
        this.add(connectionsPanel, "Center");
        this.testLabel = new FixedWidthLabel(370, "", (Icon)null);
        this.testLabel.setIconTextGap(10);
        connectionList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                DatabaseSelectionView.this.resetTestStatus();
                DatabaseSelectionView.this.testButton.setEnabled(DatabaseSelectionView.this.getSelectedConnection() != null);
                DatabaseSelectionView.this.editButton.setEnabled(DatabaseSelectionView.this.getSelectedConnection() != null);
                DatabaseSelectionView.this.fireStateChanged();
            }
        });
        this.createButtonPanel();
    }

    private void resetTestStatus() {
        this.testLabel.setText("");
        this.testLabel.setIcon((Icon)null);
        this.testFailed = false;
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel upperPanel = new JPanel(ButtonDialog.createGridLayout(1, 3));
        ResourceAction newAction = new ResourceAction("dataimport.db_connections.new", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                DatabaseSelectionView.this.manageConnectionsdialog.newConnectionAction.actionPerformed(e);
                DatabaseSelectionView.this.manageConnectionsdialog.setVisible(true);
            }
        };
        JButton newButton = new JButton(newAction);
        upperPanel.add(newButton);
        ResourceAction editAction = new ResourceAction("dataimport.db_connections.edit", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                DatabaseSelectionView.this.resetTestStatus();
                DatabaseSelectionView.this.manageConnectionsdialog.setVisible(true);
                DatabaseSelectionView.this.fireStateChanged();
            }
        };
        this.editButton = new JButton(editAction);
        this.editButton.setEnabled(this.getSelectedConnection() != null);
        upperPanel.add(this.editButton);
        ResourceAction testAction = new ResourceAction("dataimport.db_connections.test", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                DatabaseSelectionView.this.testConnection();
            }
        };
        this.testButton = new JButton(testAction);
        this.testButton.setEnabled(this.getSelectedConnection() != null);
        upperPanel.add(this.testButton);
        upperPanel.setBorder(BorderFactory.createEmptyBorder(0, 250, 0, 250));
        JPanel lowerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0D;
        gbc.fill = 2;
        gbc.insets = new Insets(25, 0, 25, 0);
        lowerPanel.add(new JLabel(" "), gbc);
        gbc.weightx = 0.0D;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = 0;
        lowerPanel.add(this.testLabel, gbc);
        buttonPanel.add(upperPanel, "North");
        buttonPanel.add(lowerPanel, "Center");
        this.add(buttonPanel, "South");
    }

    ConnectionEntry getSelectedConnection() {
        return this.manageConnectionsdialog.getSelectedEntry();
    }

    void setSelectedConnection(ConnectionEntry selectedConnection) {
        this.manageConnectionsdialog.setSelectedEntry(selectedConnection);
    }

    void setConnectionErrorText(String errorMessage) {
        this.testLabel.setIcon(DatabaseConnectionDialog.ICON_CONNECTION_STATUS_ERROR);
        this.testLabel.setText(errorMessage);
        this.testFailed = true;
        this.fireStateChanged();
    }

    private void setConnectionSuccessfulText() {
        this.testLabel.setText(DatabaseConnectionDialog.TEXT_CONNECTION_STATUS_OK);
        this.testLabel.setIcon(DatabaseConnectionDialog.ICON_CONNECTION_STATUS_OK);
        this.testFailed = false;
        this.fireStateChanged();
    }

    void addChangeListener(ChangeListener changeListener) {
        this.changeListeners.add(changeListener);
    }

    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent(this);
        Iterator var2 = this.changeListeners.iterator();

        while(var2.hasNext()) {
            ChangeListener listener = (ChangeListener)var2.next();

            try {
                listener.stateChanged(event);
            } catch (RuntimeException var5) {
                LogService.getRoot().log(Level.WARNING, "com.rapidminer.gui.io.dataimport.AbstractWizardStep.changelistener_failed", var5);
            }
        }

    }

    public boolean hasTestFailed() {
        return this.testFailed;
    }

    private void testConnection() {
        this.testLabel.setText(LOADING_TEXT);
        this.testLabel.setIcon(LOADING_ICON);
        ProgressThread t = new ProgressThread("test_database_connection") {
            public void run() {
                this.getProgressListener().setTotal(100);
                this.getProgressListener().setCompleted(10);

                try {
                    ConnectionEntry exception = DatabaseSelectionView.this.manageConnectionsdialog.getSelectedEntry();
                    if(exception == null) {
                        DatabaseSelectionView.this.setConnectionErrorText(DatabaseSelectionView.NO_SELECTION_MESSAGE);
                        DatabaseSelectionView.this.testLabel.setText(DatabaseSelectionView.NO_SELECTION_MESSAGE);
                        return;
                    }

                    DatabaseConnectionService.testConnection(exception);
                    DatabaseSelectionView.this.setConnectionSuccessfulText();
                } catch (SQLException var5) {
                    DatabaseSelectionView.this.setConnectionErrorText(var5.getLocalizedMessage());
                } finally {
                    this.getProgressListener().complete();
                }

            }
        };
        t.start();
    }
}
