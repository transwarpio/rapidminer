package com.rapidminer.extension.jdbc.gui.tools.dialogs;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseService;
import com.rapidminer.extension.jdbc.tools.jdbc.JDBCProperties;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.DatabaseConnectionService;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.FieldConnectionEntry;
import com.rapidminer.gui.tools.*;
import com.rapidminer.gui.tools.components.FixedWidthLabel;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.gui.tools.dialogs.ConfirmDialog;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class DatabaseConnectionDialog extends ButtonDialog {
    private static final long serialVersionUID = 1L;
    private static final String TEXT_CONNECTION_STATUS_UNKNOWN = I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.manage_db_connections.status.unknown.label", new Object[0]);
    public static final String TEXT_CONNECTION_STATUS_OK = I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.manage_db_connections.status.ok.label", new Object[0]);
    private static final Icon ICON_CONNECTION_STATUS_UNKNOWN = SwingTools.createIcon("16/" + I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.manage_db_connections.status.unknown.icon", new Object[0]));
    public static final Icon ICON_CONNECTION_STATUS_OK = SwingTools.createIcon("16/" + I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.manage_db_connections.status.ok.icon", new Object[0]));
    public static final Icon ICON_CONNECTION_STATUS_ERROR = SwingTools.createIcon("16/" + I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.manage_db_connections.status.error.icon", new Object[0]));
    private static final Color TEXT_SELECTED_COLOR = UIManager.getColor("Tree.selectionForeground");
    private static final Color TEXT_NON_SELECTED_COLOR = UIManager.getColor("Tree.textForeground");
    private final FilterableListModel model = new FilterableListModel();
    private DefaultListCellRenderer listRenderer;
    private final JList<ConnectionEntry> connectionList;
    private final JList<ConnectionEntry> connectionListCopy;
    private final JTextField aliasTextField;
    private final JComboBox<String> databaseTypeComboBox;
    private final JTextField hostTextField;
    private final JTextField portTextField;
    private final JTextField databaseTextField;
    private final JTextField propertyTextField;
    private final JTextField userTextField;
    private final JPasswordField passwordField;
    private final JTextField urlField;
    private final JLabel testLabel;
    private final Action openConnectionAction;
    protected final Action saveConnectionAction;
    private final Action cloneConnectionAction;
    public final Action newConnectionAction;
    private final Action deleteConnectionAction;
    private final Action testConnectionAction;
    private final Action showAdvancePropertiesAction;
    private FieldConnectionEntry currentlyEditedEntry;

    public DatabaseConnectionDialog(Window owner, String i18nKey, Object... i18nArgs) {
        super(owner, i18nKey, ModalityType.APPLICATION_MODAL, i18nArgs);
        Comparator keyListener = new Comparator() {
            public int compare(Object o1, Object o2) {
                if(o1 instanceof ConnectionEntry && o2 instanceof ConnectionEntry) {
                    ConnectionEntry co1 = (ConnectionEntry)o1;
                    ConnectionEntry co2 = (ConnectionEntry)o2;
                    return co1.isReadOnly() && !co2.isReadOnly()?1:(!co1.isReadOnly() && co2.isReadOnly()?-1:co1.toString().compareTo(co2.toString()));
                } else {
                    return o1.toString().compareTo(o2.toString());
                }
            }
        };
        this.model.setComparator(keyListener);
        Iterator var5 = DatabaseConnectionService.getConnectionEntries().iterator();

        while(var5.hasNext()) {
            ConnectionEntry entry = (ConnectionEntry)var5.next();
            this.model.addElement(entry);
        }

        this.listRenderer = new DefaultListCellRenderer() {
            private static final long serialVersionUID = 4616183160018529751L;
            private final Icon entryIcon = SwingTools.createIcon("16/" + I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.manage_db_connections.connection_entry.icon", new Object[0]));
            private final Icon entryReadOnlyIcon = SwingTools.createIcon("16/" + I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.manage_db_connections.connection_readonly_entry.icon", new Object[0]));

            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(isSelected) {
                    label.setForeground(DatabaseConnectionDialog.TEXT_SELECTED_COLOR);
                } else {
                    label.setForeground(DatabaseConnectionDialog.TEXT_NON_SELECTED_COLOR);
                }

                if(value instanceof FieldConnectionEntry) {
                    FieldConnectionEntry entry = (FieldConnectionEntry)value;
                    String remoteRepo = entry.getRepository() != null?"<br/>Taken from: " + entry.getRepository():"";
                    label.setText("<html>" + entry.getName() + " <small>(" + (entry.getProperties() != null?entry.getProperties().getName() + "; ":"") + entry.getHost() + ":" + entry.getPort() + ")" + remoteRepo + "</small></html>");
                    label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                    if(entry.isReadOnly()) {
                        label.setIcon(this.entryReadOnlyIcon);
                    } else {
                        label.setIcon(this.entryIcon);
                    }
                }

                return label;
            }
        };
        this.connectionList = new JList(this.model);
        this.connectionListCopy = new JList(this.model);
        this.aliasTextField = new JTextField(12);
        this.databaseTypeComboBox = new JComboBox(DatabaseService.getDBSystemNames());
        this.hostTextField = new JTextField(12);
        this.portTextField = new JTextField(4);
        this.databaseTextField = new JTextField(12);
        this.propertyTextField = new JTextField(12);
        this.userTextField = new JTextField(12);
        this.passwordField = new JPasswordField(12);
        this.urlField = new JTextField(12);
        this.testLabel = new FixedWidthLabel(180, TEXT_CONNECTION_STATUS_UNKNOWN, ICON_CONNECTION_STATUS_UNKNOWN);
        this.urlField.setEditable(false);
        this.databaseTypeComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                DatabaseConnectionDialog.this.updateDefaults();
                DatabaseConnectionDialog.this.updateURL((FieldConnectionEntry)null);
            }
        });
        KeyListener keyListener1 = new KeyListener() {
            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                DatabaseConnectionDialog.this.updateURL((FieldConnectionEntry)null);
            }

            public void keyTyped(KeyEvent e) {
            }
        };
        this.portTextField.addKeyListener(keyListener1);
        this.hostTextField.addKeyListener(keyListener1);
        this.databaseTextField.addKeyListener(keyListener1);
        this.propertyTextField.addKeyListener(keyListener1);
        this.userTextField.addKeyListener(keyListener1);
        this.passwordField.addKeyListener(keyListener1);
        this.openConnectionAction = new ResourceAction("manage_db_connections.open", new Object[0]) {
            private static final long serialVersionUID = 2451337494765496601L;

            public void actionPerformed(ActionEvent e) {
                Object value = DatabaseConnectionDialog.this.connectionList.getSelectedValue();
                if(value instanceof FieldConnectionEntry) {
                    FieldConnectionEntry entry = (FieldConnectionEntry)value;
                    DatabaseConnectionDialog.this.aliasTextField.setText(entry.getName());
                    if(entry.getProperties() != null) {
                        DatabaseConnectionDialog.this.databaseTypeComboBox.setSelectedItem(entry.getProperties().getName());
                    } else {
                        DatabaseConnectionDialog.this.databaseTypeComboBox.setSelectedIndex(0);
                    }

                    DatabaseConnectionDialog.this.hostTextField.setText(entry.getHost());
                    DatabaseConnectionDialog.this.portTextField.setText(entry.getPort());
                    DatabaseConnectionDialog.this.databaseTextField.setText(entry.getDatabase());
                    DatabaseConnectionDialog.this.propertyTextField.setText(entry.getProperty());
                    DatabaseConnectionDialog.this.userTextField.setText(entry.getUser());
                    if(entry.getPassword() == null) {
                        DatabaseConnectionDialog.this.passwordField.setText("");
                    } else {
                        DatabaseConnectionDialog.this.passwordField.setText(new String(entry.getPassword()));
                    }

                    DatabaseConnectionDialog.this.aliasTextField.setEditable(!entry.isReadOnly());
                    DatabaseConnectionDialog.this.databaseTypeComboBox.setEnabled(!entry.isReadOnly());
                    DatabaseConnectionDialog.this.hostTextField.setEditable(!entry.isReadOnly());
                    DatabaseConnectionDialog.this.portTextField.setEditable(!entry.isReadOnly());
                    DatabaseConnectionDialog.this.databaseTextField.setEditable(!entry.isReadOnly());
                    DatabaseConnectionDialog.this.propertyTextField.setEditable(!entry.isReadOnly());
                    DatabaseConnectionDialog.this.userTextField.setEditable(!entry.isReadOnly());
                    DatabaseConnectionDialog.this.passwordField.setEditable(!entry.isReadOnly());
                    DatabaseConnectionDialog.this.saveConnectionAction.setEnabled(!entry.isReadOnly());
                    DatabaseConnectionDialog.this.showAdvancePropertiesAction.setEnabled(!entry.isReadOnly());
                    DatabaseConnectionDialog.this.updateURL(entry);
                    DatabaseConnectionDialog.this.currentlyEditedEntry = new FieldConnectionEntry(entry.getName(), entry.getProperties(), entry.getHost(), entry.getPort(), entry.getDatabase(), entry.getProperty(), entry.getUser(), entry.getPassword());
                    DatabaseConnectionDialog.this.currentlyEditedEntry.setConnectionProperties(entry.getConnectionProperties());
                }

            }
        };
        this.saveConnectionAction = new ResourceAction("manage_db_connections.save", new Object[0]) {
            private static final long serialVersionUID = -8477647509533859436L;

            public void actionPerformed(ActionEvent e) {
                FieldConnectionEntry entry = DatabaseConnectionDialog.this.checkFields(true);
                if(entry != null) {
                    ConnectionEntry sameNameEntry = null;

                    for(int i = 0; i < DatabaseConnectionDialog.this.model.getSize(); ++i) {
                        ConnectionEntry compareEntry = (ConnectionEntry)DatabaseConnectionDialog.this.model.getElementAt(i);
                        if(compareEntry.getName().equals(entry.getName())) {
                            sameNameEntry = compareEntry;
                            break;
                        }
                    }

                    if(sameNameEntry == null || sameNameEntry != null && sameNameEntry.equals(DatabaseConnectionDialog.this.currentlyEditedEntry)) {
                        if(DatabaseConnectionDialog.this.currentlyEditedEntry != null) {
                            DatabaseConnectionService.deleteConnectionEntry(DatabaseConnectionDialog.this.currentlyEditedEntry);
                            DatabaseConnectionDialog.this.model.removeElement(DatabaseConnectionDialog.this.currentlyEditedEntry);
                        }

                        DatabaseConnectionDialog.this.model.addElement(entry);
                        DatabaseConnectionService.addConnectionEntry(entry);
                        DatabaseConnectionDialog.this.connectionList.clearSelection();
                        DatabaseConnectionDialog.this.connectionList.setSelectedValue(entry, true);
                        DatabaseConnectionDialog.this.openConnectionAction.actionPerformed(null);
                    } else if(SwingTools.showConfirmDialog("manage_db_connections.overwrite", 0, new Object[]{entry.getName()}) == 0) {
                        DatabaseConnectionService.deleteConnectionEntry(sameNameEntry);
                        DatabaseConnectionDialog.this.model.removeElement(sameNameEntry);
                        if(DatabaseConnectionDialog.this.currentlyEditedEntry != null) {
                            DatabaseConnectionService.deleteConnectionEntry(DatabaseConnectionDialog.this.currentlyEditedEntry);
                            DatabaseConnectionDialog.this.model.removeElement(DatabaseConnectionDialog.this.currentlyEditedEntry);
                        }

                        DatabaseConnectionDialog.this.model.addElement(entry);
                        DatabaseConnectionService.addConnectionEntry(entry);
                        DatabaseConnectionDialog.this.connectionList.clearSelection();
                        DatabaseConnectionDialog.this.connectionList.setSelectedValue(entry, true);
                        DatabaseConnectionDialog.this.openConnectionAction.actionPerformed((ActionEvent)null);
                    }
                }

            }
        };
        this.cloneConnectionAction = new ResourceAction("manage_db_connections.clone", new Object[0]) {
            private static final long serialVersionUID = -6286464201049577441L;

            public void actionPerformed(ActionEvent e) {
                Object value = DatabaseConnectionDialog.this.connectionList.getSelectedValue();
                if(value instanceof FieldConnectionEntry) {
                    FieldConnectionEntry selectedEntry = (FieldConnectionEntry)value;
                    String alias = "Copy of " + selectedEntry.getName();
                    boolean unique = false;
                    int copyIndex = 0;

                    do {
                        for(int newEntry = 0; newEntry < DatabaseConnectionDialog.this.model.getSize(); ++newEntry) {
                            unique = true;
                            ConnectionEntry compareEntry = (ConnectionEntry)DatabaseConnectionDialog.this.model.getElementAt(newEntry);
                            if(compareEntry.getName().equals(alias)) {
                                unique = false;
                                ++copyIndex;
                                alias = "Copy(" + copyIndex + ") of " + selectedEntry.getName();
                                break;
                            }
                        }
                    } while(!unique);

                    FieldConnectionEntry var9 = new FieldConnectionEntry(alias, selectedEntry.getProperties(), selectedEntry.getHost(), selectedEntry.getPort(), selectedEntry.getDatabase(), selectedEntry.getProperty(), selectedEntry.getUser(), selectedEntry.getPassword());
                    var9.setConnectionProperties(selectedEntry.getConnectionProperties());
                    DatabaseConnectionDialog.this.model.addElement(var9);
                    DatabaseConnectionService.addConnectionEntry(var9);
                    DatabaseConnectionDialog.this.connectionList.setSelectedValue(var9, true);
                    DatabaseConnectionDialog.this.openConnectionAction.actionPerformed((ActionEvent)null);
                }

            }
        };
        this.newConnectionAction = new ResourceAction("manage_db_connections.new", new Object[0]) {
            private static final long serialVersionUID = 7979548709619302219L;

            public void actionPerformed(ActionEvent e) {
                String alias = "New connection";
                boolean unique = false;
                int appendIndex = 1;

                do {
                    for(int newEntry = 0; newEntry < DatabaseConnectionDialog.this.model.getSize(); ++newEntry) {
                        unique = true;
                        ConnectionEntry compareEntry = (ConnectionEntry)DatabaseConnectionDialog.this.model.getElementAt(newEntry);
                        if(compareEntry.getName().equals(alias + appendIndex)) {
                            unique = false;
                            ++appendIndex;
                            break;
                        }
                    }
                } while(!unique && DatabaseConnectionDialog.this.model.getSize() > 0);

                FieldConnectionEntry var7 = new FieldConnectionEntry(alias + appendIndex, DatabaseConnectionDialog.this.getJDBCProperties(), "localhost", DatabaseConnectionDialog.this.getJDBCProperties().getDefaultPort(), "", "", "", "".toCharArray());
                DatabaseConnectionDialog.this.model.addElement(var7);
                DatabaseConnectionService.addConnectionEntry(var7);
                DatabaseConnectionDialog.this.connectionList.setSelectedValue(var7, true);
                DatabaseConnectionDialog.this.openConnectionAction.actionPerformed((ActionEvent)null);
            }
        };
        this.deleteConnectionAction = new ResourceAction("manage_db_connections.delete", new Object[0]) {
            private static final long serialVersionUID = 1155260480975020776L;

            public void actionPerformed(ActionEvent e) {
                Object[] selectedValues = DatabaseConnectionDialog.this.connectionList.getSelectedValues();
                boolean applyToAll = false;
                int returnOption = 2;

                for(int i = 0; i < selectedValues.length; ++i) {
                    ConnectionEntry entry = (ConnectionEntry)selectedValues[i];
                    if(!applyToAll) {
                        ConfirmDialog j = new ConfirmDialog(DatabaseConnectionDialog.this, "manage_db_connections.delete", 0, false, new Object[]{entry.getName()});
                        j.setVisible(true);
                        returnOption = j.getReturnOption();
                    }

                    if(returnOption == 2) {
                        break;
                    }

                    if(returnOption == 0) {
                        DatabaseConnectionService.deleteConnectionEntry(entry);
                        DatabaseConnectionDialog.this.model.removeElement(entry);
                        DatabaseConnectionDialog.this.connectionList.clearSelection();

                        for(int var9 = 0; var9 < selectedValues.length; ++var9) {
                            int index = DatabaseConnectionDialog.this.model.indexOf(selectedValues[var9]);
                            DatabaseConnectionDialog.this.connectionList.getSelectionModel().addSelectionInterval(index, index);
                        }
                    }
                }

                if(DatabaseConnectionDialog.this.connectionList.getModel().getSize() > 0) {
                    DatabaseConnectionDialog.this.connectionList.setSelectedIndex(0);
                    DatabaseConnectionDialog.this.openConnectionAction.actionPerformed((ActionEvent)null);
                } else if (DatabaseConnectionDialog.this.connectionList.getModel().getSize() == 0) {
                    aliasTextField.setText("");
                    databaseTypeComboBox.setSelectedIndex(0);
                    hostTextField.setText("");
                    portTextField.setText("");
                    databaseTextField.setText("");
                    propertyTextField.setText("");
                    userTextField.setText("");
                    passwordField.setText("");
                    urlField.setText("");
                }

            }
        };
        this.testConnectionAction = new ResourceAction("manage_db_connections.test", new Object[0]) {
            private static final long serialVersionUID = -25485375154547037L;

            public void actionPerformed(ActionEvent e) {
                ProgressThread t = new ProgressThread("test_database_connection") {
                    public void run() {
                        this.getProgressListener().setTotal(100);
                        this.getProgressListener().setCompleted(10);

                        try {
                            FieldConnectionEntry exception = DatabaseConnectionDialog.this.checkFields(false);
                            if(exception == null) {
                                return;
                            }

                            if(!DatabaseConnectionService.testConnection(exception)) {
                                throw new SQLException();
                            }

                            DatabaseConnectionDialog.this.testLabel.setText(DatabaseConnectionDialog.TEXT_CONNECTION_STATUS_OK);
                            DatabaseConnectionDialog.this.testLabel.setIcon(DatabaseConnectionDialog.ICON_CONNECTION_STATUS_OK);
                        } catch (SQLException var6) {
                            String errorMessage = var6.getLocalizedMessage();
                            if(errorMessage.length() > 100) {
                                errorMessage = var6.getLocalizedMessage().substring(0, 100) + "...";
                            }

                            DatabaseConnectionDialog.this.testLabel.setText(errorMessage);
                            DatabaseConnectionDialog.this.testLabel.setIcon(DatabaseConnectionDialog.ICON_CONNECTION_STATUS_ERROR);
                        } finally {
                            this.getProgressListener().complete();
                        }

                    }
                };
                t.start();
            }
        };
        this.showAdvancePropertiesAction = new ResourceAction("manage_db_connections.advanced", new Object[0]) {
            private static final long serialVersionUID = 7641194296960014681L;

            public void actionPerformed(ActionEvent e) {
                if(DatabaseConnectionDialog.this.currentlyEditedEntry != null) {
                    DriverPropertyInfo[] propInfo = DatabaseConnectionDialog.this.getPropertyInfos();
                    if(propInfo == null) {
                        SwingTools.showSimpleErrorMessage("db_driver_not_found", "", new Object[]{String.valueOf(DatabaseConnectionDialog.this.databaseTypeComboBox.getSelectedItem())});
                    } else {
                        DatabaseAdvancedConnectionDialog advancedDiag = new DatabaseAdvancedConnectionDialog(DatabaseConnectionDialog.this, "db_connection_advanced", propInfo, DatabaseConnectionDialog.this.currentlyEditedEntry.getConnectionProperties(), new Object[0]);
                        advancedDiag.setVisible(true);
                        Properties connectionProperties = advancedDiag.getConnectionProperties();
                        if(connectionProperties != null) {
                            DatabaseConnectionDialog.this.currentlyEditedEntry.setConnectionProperties(connectionProperties);
                        }

                    }
                }
            }
        };
        this.currentlyEditedEntry = null;
        this.openConnectionAction.setEnabled(false);
        this.deleteConnectionAction.setEnabled(false);
        this.cloneConnectionAction.setEnabled(false);
        this.setupConnectionLists();
    }

    private void setupConnectionLists() {
        this.connectionList.setCellRenderer(this.listRenderer);
        this.connectionList.setSelectionMode(0);
        this.connectionList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                DatabaseConnectionDialog.this.openConnectionAction.actionPerformed((ActionEvent)null);
            }
        });
        this.connectionList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                boolean selected = DatabaseConnectionDialog.this.connectionList.getSelectedValue() != null;
                DatabaseConnectionDialog.this.openConnectionAction.setEnabled(selected);
                DatabaseConnectionDialog.this.cloneConnectionAction.setEnabled(selected);
                if(selected) {
                    selected = !((ConnectionEntry)DatabaseConnectionDialog.this.connectionList.getSelectedValue()).isReadOnly();
                }

                DatabaseConnectionDialog.this.deleteConnectionAction.setEnabled(selected);
                DatabaseConnectionDialog.this.connectionListCopy.setSelectedIndex(DatabaseConnectionDialog.this.connectionList.getSelectedIndex());
            }
        });
        this.connectionListCopy.setCellRenderer(this.listRenderer);
        this.connectionListCopy.setSelectionMode(0);
        this.connectionListCopy.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                DatabaseConnectionDialog.this.connectionList.setSelectedIndex(DatabaseConnectionDialog.this.connectionListCopy.getSelectedIndex());
            }
        });
    }

    public Collection<AbstractButton> makeButtons() {
        LinkedList list = new LinkedList();
        list.add(new JButton(this.saveConnectionAction));
        list.add(new JButton(this.newConnectionAction));
        list.add(new JButton(this.cloneConnectionAction));
        list.add(new JButton(this.deleteConnectionAction));
        return list;
    }

    private JPanel makeConnectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(createTitledBorder(I18N.getMessage(I18N.getGUIBundle(), "gui.border.manage_db_connections.details", new Object[0])));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0D;
        c.weighty = 0.0D;
        c.fill = 2;
        c.insets = new Insets(0, 6, 0, 6);
        panel.add(new ResourceLabel("manage_db_connections.name", new Object[0]), c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0D;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.fill = 0;
        panel.add(new JButton(this.showAdvancePropertiesAction), c);
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 6, 6, 6);
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = 2;
        panel.add(this.aliasTextField, c);
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0D;
        c.gridwidth = 3;
        c.insets = new Insets(6, 6, 0, 6);
        panel.add(new ResourceLabel("manage_db_connections.system", new Object[0]), c);
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 6, 0, 6);
        panel.add(this.databaseTypeComboBox, c);
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 1.0D;
        c.gridwidth = 2;
        c.insets = new Insets(6, 6, 0, 6);
        panel.add(new ResourceLabel("manage_db_connections.host", new Object[0]), c);
        c.gridx = 2;
        c.gridy = 4;
        c.gridwidth = 1;
        c.weightx = 0.0D;
        panel.add(new ResourceLabel("manage_db_connections.port", new Object[0]), c);
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 1.0D;
        c.gridwidth = 2;
        c.insets = new Insets(0, 6, 0, 6);
        panel.add(this.hostTextField, c);
        c.gridx = 2;
        c.gridy = 5;
        c.gridwidth = 1;
        c.weightx = 0.0D;
        panel.add(this.portTextField, c);
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 1.0D;
        c.insets = new Insets(6, 6, 0, 6);
        c.gridwidth = 3;
        panel.add(new ResourceLabel("manage_db_connections.database", new Object[0]), c);
        c.gridx = 0;
        c.gridy = 7;
        c.insets = new Insets(0, 6, 0, 6);
        panel.add(this.databaseTextField, c);
        c.gridx = 0;
        c.gridy = 8;
        c.weightx = 1.0D;
        c.insets = new Insets(6, 6, 0, 6);
        c.gridwidth = 3;
        panel.add(new ResourceLabel("manage_db_connections.property", new Object[0]), c);
        c.gridx = 0;
        c.gridy = 9;
        c.insets = new Insets(0, 6, 0, 6);
        panel.add(this.propertyTextField, c);
        c.gridx = 0;
        c.gridy = 10;
        c.insets = new Insets(6, 6, 0, 6);
        panel.add(new ResourceLabel("manage_db_connections.user", new Object[0]), c);
        c.gridx = 0;
        c.gridy = 11;
        c.insets = new Insets(0, 6, 0, 6);
        panel.add(this.userTextField, c);
        c.gridx = 0;
        c.gridy = 12;
        c.insets = new Insets(6, 6, 0, 6);
        panel.add(new ResourceLabel("manage_db_connections.password", new Object[0]), c);
        c.gridx = 0;
        c.gridy = 13;
        c.insets = new Insets(0, 6, 6, 6);
        panel.add(this.passwordField, c);
        c.gridx = 0;
        c.gridy = 14;
        c.insets = new Insets(6, 6, 0, 6);
        panel.add(new ResourceLabel("manage_db_connections.url", new Object[0]), c);
        c.gridx = 0;
        c.gridy = 15;
        c.insets = new Insets(0, 6, 6, 6);
        panel.add(this.urlField, c);
        JPanel scrollPanel = new JPanel();
        scrollPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = 1;
        gbc.weightx = 1.0D;
        gbc.weighty = 1.0D;
        gbc.insets = new Insets(6, 6, 6, 6);
        scrollPanel.add(this.testLabel, gbc);
        ExtendedJScrollPane ejsp = new ExtendedJScrollPane(scrollPanel);
        ejsp.setBorder(BorderFactory.createEtchedBorder(1));
        c.gridx = 0;
        c.gridy = 16;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        c.fill = 1;
        c.insets = new Insets(6, 12, 6, 6);
        panel.add(ejsp, c);
        c.gridx = 2;
        c.gridy = 16;
        c.weightx = 0.0D;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = 0;
        c.anchor = 10;
        panel.add(new JButton(this.testConnectionAction), c);
        this.updateDefaults();
        this.updateURL((FieldConnectionEntry)null);
        return panel;
    }

    public void setVisible(boolean b) {
        if(this.connectionList.getModel().getSize() > 0 && this.connectionList.getSelectedIndex() < 0) {
            this.connectionList.setSelectedIndex(0);
        }

        this.openConnectionAction.actionPerformed((ActionEvent)null);
        super.setVisible(b);
    }

    public JPanel makeConnectionManagementPanel() {
        JPanel panel = new JPanel(createGridLayout(1, 2));
        ExtendedJScrollPane connectionListPane = new ExtendedJScrollPane(this.connectionList);
        connectionListPane.setBorder(createTitledBorder(I18N.getMessage(I18N.getGUIBundle(), "gui.border.manage_db_connections.connections", new Object[0])));
        panel.add(connectionListPane);
        panel.add(this.makeConnectionPanel());
        return panel;
    }

    private JDBCProperties getJDBCProperties() {
        return DatabaseService.getJDBCProperties((String)this.databaseTypeComboBox.getSelectedItem());
    }

    private DriverPropertyInfo[] getPropertyInfos() {
        try {
            String e = this.hostTextField.getText();
            if(e == null || "".equals(e)) {
                e = "192.168.0.0";
            }

            String port = this.portTextField.getText();
            if(port == null || "".equals(port)) {
                port = "1234";
            }

            String db = this.databaseTextField.getText();
            if(db == null || "".equals(db)) {
                db = "test";
            }

            String prop = this.propertyTextField.getText();
            if (prop == null || "".equals(prop)) {
                prop = "";
            }

            String driverURL = FieldConnectionEntry.createURL(this.getJDBCProperties(), e, port, db, prop);
            Driver driver = DriverManager.getDriver(driverURL);
            Properties givenProperties = this.currentlyEditedEntry.getConnectionProperties();
            DriverPropertyInfo[] propertyInfo = driver.getPropertyInfo(driverURL, givenProperties);
            if(propertyInfo == null) {
                propertyInfo = new DriverPropertyInfo[0];
            }

            return propertyInfo;
        } catch (SQLException var8) {
            LogService.getRoot().log(Level.SEVERE, "com.rapidminer.gui.tools.dialogs.DatabaseConnectionDialog.loading_jdbc_driver_properties_error", var8);
            return null;
        }
    }

    private void updateDefaults() {
        this.portTextField.setText(this.getJDBCProperties().getDefaultPort());
    }

    private void updateURL(FieldConnectionEntry entry) {
        if(entry != null && entry.isReadOnly()) {
            this.urlField.setText(entry.getURL());
        } else {
            this.urlField.setText(FieldConnectionEntry.createURL(this.getJDBCProperties(), this.hostTextField.getText(), this.portTextField.getText(), this.databaseTextField.getText(), this.propertyTextField.getText()));
        }

        this.testLabel.setText(TEXT_CONNECTION_STATUS_UNKNOWN);
        this.testLabel.setIcon(ICON_CONNECTION_STATUS_UNKNOWN);
        this.fireStateChanged();
    }

    protected FieldConnectionEntry checkFields(boolean save) {
        String alias = this.aliasTextField.getText();
        if(!save || alias != null && !"".equals(alias.trim())) {
            String host = this.hostTextField.getText();
            if(host != null && !"".equals(host)) {
                String port = this.portTextField.getText();
                String database = this.databaseTextField.getText();
                if(database == null) {
                    database = "";
                }
                String property = this.propertyTextField.getText();
                if (property == null) {
                    property = "";
                }

                String user = this.userTextField.getText();
                char[] password = this.passwordField.getPassword();
                FieldConnectionEntry entry = new FieldConnectionEntry(alias, this.getJDBCProperties(), host, port, database, property, user, password);
                if(this.currentlyEditedEntry != null) {
                    entry.setConnectionProperties(this.currentlyEditedEntry.getConnectionProperties());
                }

                return entry;
            } else {
                SwingTools.showVerySimpleErrorMessage("manage_db_connections.missing", new Object[]{I18N.getMessage(I18N.getGUIBundle(), "gui.label.manage_db_connections.host.label", new Object[0])});
                this.hostTextField.requestFocusInWindow();
                return null;
            }
        } else {
            SwingTools.showVerySimpleErrorMessage("manage_db_connections.missing", new Object[]{I18N.getMessage(I18N.getGUIBundle(), "gui.label.manage_db_connections.name.label", new Object[0])});
            this.aliasTextField.requestFocusInWindow();
            return null;
        }
    }

    public FieldConnectionEntry getConnectionEntry(boolean save) {
        String alias = this.aliasTextField.getText();
        if(!save || alias != null && !"".equals(alias.trim())) {
            String host = this.hostTextField.getText();
            if(host != null && !"".equals(host)) {
                String port = this.portTextField.getText();
                if(port == null) {
                    port = "";
                }

                String database = this.databaseTextField.getText();
                if(database == null) {
                    database = "";
                }

                String property = this.propertyTextField.getText();
                if (property == null) {
                    property = "";
                }

                String user = this.userTextField.getText();
                char[] password = this.passwordField.getPassword();
                return new FieldConnectionEntry(alias, this.getJDBCProperties(), host, port, database, property, user, password);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public ConnectionEntry getSelectedEntry() {
        return (ConnectionEntry)this.connectionList.getSelectedValue();
    }

    public JList<ConnectionEntry> getConnectionListCopy() {
        return this.connectionListCopy;
    }

    public void setSelectedEntry(ConnectionEntry selectedConnection) {
        this.connectionList.setSelectedValue(selectedConnection, true);
        this.connectionListCopy.setSelectedValue(selectedConnection, true);
    }
}
