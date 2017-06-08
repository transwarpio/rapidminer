package com.rapidminer.extension.jdbc.gui.tools.dialogs;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.I18N;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.DriverPropertyInfo;
import java.util.*;
import java.util.List;

public class DatabaseAdvancedConnectionDialog extends ButtonDialog {
    private static final long serialVersionUID = -3287030968059122084L;
    private JTable table;
    private int returnValue = 2;

    public DatabaseAdvancedConnectionDialog(Window owner, String i18nKey, DriverPropertyInfo[] propertyInfos, Properties currentProperties, Object... i18nArgs) {
        super(owner, i18nKey, ModalityType.APPLICATION_MODAL, i18nArgs);
        this.setupGUI(propertyInfos, currentProperties);
    }

    private void setupGUI(final DriverPropertyInfo[] propInfo, Properties currentProperties) {
        this.table = new JTable(new DatabaseAdvancedConnectionDialog.DriverPropertyInfoTableModel(propInfo, currentProperties)) {
            private static final long serialVersionUID = 1L;
            private Map<Integer, JComboBox> mapOfBoxes = new HashMap(propInfo.length);

            public TableCellEditor getCellEditor(int row, int col) {
                if(this.getModel().getValueAt(this.convertRowIndexToModel(row), this.convertColumnIndexToModel(col)) instanceof String[]) {
                    JComboBox box = (JComboBox)this.mapOfBoxes.get(Integer.valueOf(this.convertRowIndexToModel(row)));
                    if(box == null) {
                        this.mapOfBoxes.put(Integer.valueOf(this.convertRowIndexToModel(row)), new JComboBox((String[])((String[])this.getModel().getValueAt(this.convertRowIndexToModel(row), this.convertColumnIndexToModel(col)))));
                        box = (JComboBox)this.mapOfBoxes.get(Integer.valueOf(this.convertRowIndexToModel(row)));
                    }

                    box.setSelectedItem(((DatabaseAdvancedConnectionDialog.DriverPropertyInfoTableModel)DatabaseAdvancedConnectionDialog.this.table.getModel()).getComboValue(DatabaseAdvancedConnectionDialog.this.table.convertRowIndexToModel(row)));
                    return new DefaultCellEditor(box);
                } else {
                    return this.getModel().getValueAt(this.convertRowIndexToModel(row), this.convertColumnIndexToModel(col)) instanceof Boolean?this.getDefaultEditor(Boolean.class):this.getDefaultEditor(String.class);
                }
            }

            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(this.columnModel) {
                    private static final long serialVersionUID = 1L;

                    public String getToolTipText(MouseEvent e) {
                        Point p = e.getPoint();
                        int index = this.columnModel.getColumnIndexAtX(p.x);
                        int realIndex = this.columnModel.getColumn(index).getModelIndex();
                        switch(realIndex) {
                            case 0:
                                return I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.db_connection_advanced.table.key.tooltip", new Object[0]);
                            case 1:
                                return I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.db_connection_advanced.table.value.tooltip", new Object[0]);
                            case 2:
                                return I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.db_connection_advanced.table.override.tooltip", new Object[0]);
                            default:
                                return null;
                        }
                    }
                };
            }
        };
        this.table.setAutoCreateRowSorter(true);
        ((DefaultRowSorter)this.table.getRowSorter()).setMaxSortKeys(1);
        this.table.setRowHeight(this.table.getRowHeight() + 4);
        this.table.setAutoResizeMode(1);
        this.table.getColumnModel().getColumn(0).setPreferredWidth(200);
        this.table.getColumnModel().getColumn(1).setPreferredWidth(200);
        this.table.getColumnModel().getColumn(2).setPreferredWidth(10);
        this.table.setDefaultRenderer(Object.class, new DatabaseAdvancedConnectionDialog.DriverPropertyInfoTableDefaultCellRenderer());
        this.table.getTableHeader().setReorderingAllowed(false);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        ExtendedJScrollPane scrollPane = new ExtendedJScrollPane(this.table);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0D;
        gbc.weighty = 1.0D;
        gbc.fill = 1;
        scrollPane.setBorder((Border)null);
        panel.add(scrollPane, gbc);
        LinkedList list = new LinkedList();
        JButton okButton = this.makeOkButton();
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DatabaseAdvancedConnectionDialog.this.returnValue = 0;
            }
        });
        list.add(okButton);
        list.add(this.makeCancelButton());
        this.layoutDefault(panel, 1, list);
    }

    public Properties getConnectionProperties() {
        return this.returnValue == 0?((DatabaseAdvancedConnectionDialog.DriverPropertyInfoTableModel)this.table.getModel()).getProperties():null;
    }

    private class DriverPropertyInfoTableDefaultCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = -3376218040898856384L;
        JComboBox box;

        private DriverPropertyInfoTableDefaultCellRenderer() {
            this.box = new JComboBox();
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if(value instanceof String[]) {
                this.box.setModel(new DefaultComboBoxModel((String[])(value)));
                this.box.setSelectedItem(((DatabaseAdvancedConnectionDialog.DriverPropertyInfoTableModel)table.getModel()).getComboValue(table.convertRowIndexToModel(row)));
                return this.box;
            } else {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(component instanceof DefaultTableCellRenderer && table.convertColumnIndexToModel(column) == 0) {
                    String tooltip = ((DatabaseAdvancedConnectionDialog.DriverPropertyInfoTableModel)table.getModel()).getTooltip(table.convertRowIndexToModel(row));
                    tooltip = "<html><div width = 300px>" + tooltip + "</div></html>";
                    ((DefaultTableCellRenderer)component).setToolTipText(tooltip);
                }

                return component;
            }
        }
    }

    private class DriverPropertyInfoTableModel extends AbstractTableModel {
        private static final long serialVersionUID = -6521100131706498109L;
        private DriverPropertyInfo[] propInfo;
        private List<Boolean> override;

        private DriverPropertyInfoTableModel(DriverPropertyInfo[] propInfo, Properties currentProperties) {
            this.propInfo = propInfo;
            this.override = new ArrayList(propInfo.length);

            for(int i = 0; i < this.propInfo.length; ++i) {
                if(currentProperties.get(propInfo[i].name) != null) {
                    this.override.add(Boolean.valueOf(true));
                } else {
                    this.override.add(Boolean.valueOf(false));
                }
            }

        }

        public int getRowCount() {
            return this.propInfo.length;
        }

        public int getColumnCount() {
            return 3;
        }

        public boolean isCellEditable(int row, int col) {
            return col == 1 || col == 2;
        }

        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0?String.class:(columnIndex == 1?Object.class:Boolean.class);
        }

        public String getColumnName(int col) {
            return col == 0?"Key":(col == 2?"Override":"Value");
        }

        public String getComboValue(int row) {
            return this.propInfo[row].value;
        }

        public String getTooltip(int row) {
            return this.propInfo[row].description;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return columnIndex == 0?this.propInfo[rowIndex].name:(columnIndex == 2?this.override.get(rowIndex):(this.propInfo[rowIndex].choices == null?this.propInfo[rowIndex].value:this.propInfo[rowIndex].choices));
        }

        public void setValueAt(Object value, int row, int col) {
            if(col == 1 || col == 2) {
                if(col == 1) {
                    if(this.propInfo[row].choices != null) {
                        boolean found = false;
                        String[] var5 = this.propInfo[row].choices;
                        int var6 = var5.length;

                        for(int var7 = 0; var7 < var6; ++var7) {
                            String choice = var5[var7];
                            if(choice.equals(value)) {
                                found = true;
                                break;
                            }
                        }

                        if(!found) {
                            return;
                        }
                    }

                    this.propInfo[row].value = String.valueOf(value);
                    this.override.set(row, Boolean.TRUE);
                    this.fireTableCellUpdated(row, 2);
                } else if(col == 2) {
                    this.override.set(row, Boolean.valueOf(Boolean.parseBoolean(String.valueOf(value))));
                }

            }
        }

        public Properties getProperties() {
            Properties props = new Properties();

            for(int i = 0; i < this.getRowCount(); ++i) {
                if(this.propInfo[i].value != null && !"".equals(this.propInfo[i].value) && ((Boolean)this.override.get(i)).booleanValue()) {
                    props.put(this.propInfo[i].name, this.propInfo[i].value);
                }
            }

            return props;
        }
    }
}
