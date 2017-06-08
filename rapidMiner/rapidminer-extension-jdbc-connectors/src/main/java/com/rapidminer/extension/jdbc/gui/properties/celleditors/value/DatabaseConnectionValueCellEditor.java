package com.rapidminer.extension.jdbc.gui.properties.celleditors.value;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.gui.tools.dialogs.ManageDatabaseConnectionsDialog;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseConnection;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.DatabaseConnectionService;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.FieldConnectionEntry;
import com.rapidminer.gui.properties.celleditors.value.PropertyValueCellEditor;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.operator.Operator;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;

public class DatabaseConnectionValueCellEditor extends AbstractCellEditor implements PropertyValueCellEditor {
    private static final long serialVersionUID = -771727412083431607L;
    private DatabaseConnectionValueCellEditor.DatabaseConnectionComboBoxModel model = new DatabaseConnectionValueCellEditor.DatabaseConnectionComboBoxModel();
    private JPanel panel = new JPanel();
    private JComboBox comboBox;

    public DatabaseConnectionValueCellEditor(ParameterTypeDatabaseConnection type) {
        this.comboBox = new JComboBox(this.model);
        this.panel.setLayout(new GridBagLayout());
        this.panel.setToolTipText(type.getDescription());
        this.comboBox.setToolTipText(type.getDescription());
        this.comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DatabaseConnectionValueCellEditor.this.fireEditingStopped();
            }
        });
        this.comboBox.addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent e) {
                if(!e.isTemporary()) {
                    DatabaseConnectionValueCellEditor.this.fireEditingStopped();
                }

            }

            public void focusGained(FocusEvent e) {
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 1;
        c.weighty = 1.0D;
        c.weightx = 1.0D;
        this.panel.add(this.comboBox, c);
        JButton button = new JButton(new ResourceAction(true, "manage_db_connections", new Object[0]) {
            private static final long serialVersionUID = 3989811306286704326L;

            {
                this.putValue("Name", "");
            }

            public void actionPerformed(ActionEvent e) {
                class SetDatabaseConnectionDialog extends ManageDatabaseConnectionsDialog {
                    private static final long serialVersionUID = 2306881477330192804L;

                    public SetDatabaseConnectionDialog() {
                    }

                    protected void ok() {
                        FieldConnectionEntry entry = this.checkFields(true);
                        if(entry != null) {
                            boolean existent = false;
                            Iterator var3 = DatabaseConnectionService.getConnectionEntries().iterator();

                            while(var3.hasNext()) {
                                ConnectionEntry listEntry = (ConnectionEntry)var3.next();
                                if(listEntry.getName().equals(entry.getName())) {
                                    existent = true;
                                    break;
                                }
                            }

                            if(!existent) {
                                if(SwingTools.showConfirmDialog("save", 0, new Object[]{entry.getName()}) != 0) {
                                    DatabaseConnectionValueCellEditor.this.fireEditingCanceled();
                                    return;
                                }

                                DatabaseConnectionService.addConnectionEntry(entry);
                            }

                            DatabaseConnectionValueCellEditor.this.model.setSelectedItem(entry.getName());
                            DatabaseConnectionValueCellEditor.this.fireEditingStopped();
                            super.ok();
                        }

                    }
                }

                SetDatabaseConnectionDialog dialog = new SetDatabaseConnectionDialog();
                dialog.setVisible(true);
            }
        });
        button.setMargin(new Insets(0, 0, 0, 0));
        c.weightx = 0.0D;
        c.insets = new Insets(0, 5, 0, 0);
        this.panel.add(button, c);
    }

    public boolean rendersLabel() {
        return false;
    }

    public boolean useEditorAsRenderer() {
        return true;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.comboBox.setSelectedItem(value);
        return this.panel;
    }

    public Object getCellEditorValue() {
        return this.comboBox.getSelectedItem();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.comboBox.setSelectedItem(value);
        return this.panel;
    }

    public void setOperator(Operator operator) {
    }

    class DatabaseConnectionComboBoxModel extends AbstractListModel implements ComboBoxModel {
        private static final long serialVersionUID = 5358838374857978178L;
        private ConnectionEntry selectedConnection;

        DatabaseConnectionComboBoxModel() {
        }

        private List<ConnectionEntry> getList() {
            return new ArrayList(DatabaseConnectionService.getConnectionEntries());
        }

        public void setSelectedItem(Object anItem) {
            if(this.selectedConnection != null && !this.selectedConnection.getName().equals(anItem) || this.selectedConnection == null && anItem != null) {
                this.selectedConnection = DatabaseConnectionService.getConnectionEntry((String)anItem);
                this.fireContentsChanged(this, -1, -1);
            }

        }

        public Object getSelectedItem() {
            return this.selectedConnection == null?null:this.selectedConnection.getName();
        }

        public int getSize() {
            return this.getList().size();
        }

        public Object getElementAt(int index) {
            return index >= 0 && index < this.getList().size()?((ConnectionEntry)this.getList().get(index)).getName():null;
        }
    }
}
