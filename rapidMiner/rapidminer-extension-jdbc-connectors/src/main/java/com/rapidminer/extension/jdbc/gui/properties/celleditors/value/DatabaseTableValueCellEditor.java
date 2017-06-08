package com.rapidminer.extension.jdbc.gui.properties.celleditors.value;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseSchema;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseTable;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.TableMetaDataCache;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import com.rapidminer.gui.properties.celleditors.value.PropertyValueCellEditor;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.autocomplete.AutoCompleteComboBoxAddition;
import com.rapidminer.operator.Operator;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.tools.Tools;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class DatabaseTableValueCellEditor extends AbstractCellEditor implements PropertyValueCellEditor {
    private static final long serialVersionUID = -771727412083431607L;
    private DatabaseTableValueCellEditor.Mode mode;
    private DatabaseTableValueCellEditor.DatabaseTableComboBoxModel model = new DatabaseTableValueCellEditor.DatabaseTableComboBoxModel();
    private JComboBox comboBox = new DatabaseTableValueCellEditor.DatabaseTableComboBox();
    private Operator operator;
    private ParameterType type;
    private ConnectionProvider connectionProvider;
    private JPanel panel = new JPanel();

    public DatabaseTableValueCellEditor(ParameterTypeDatabaseSchema type) {
        this.type = type;
        this.mode = DatabaseTableValueCellEditor.Mode.SCHEMA;
        new AutoCompleteComboBoxAddition(this.comboBox);
        this.setupGUI();
    }

    public DatabaseTableValueCellEditor(ParameterTypeDatabaseTable type) {
        this.type = type;
        this.mode = DatabaseTableValueCellEditor.Mode.TABLE;
        new AutoCompleteComboBoxAddition(this.comboBox);
        this.setupGUI();
    }

    private void setupGUI() {
        this.panel.setLayout(new GridBagLayout());
        this.panel.setToolTipText(this.type.getDescription());
        this.comboBox.setToolTipText(this.type.getDescription());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 1;
        c.weighty = 1.0D;
        c.weightx = 1.0D;
        this.panel.add(this.comboBox, c);
        JButton button = new JButton(new ResourceAction(true, "clear_db_cache", new Object[0]) {
            private static final long serialVersionUID = 8510147303889637712L;

            {
                this.putValue("Name", "");
            }

            public void actionPerformed(ActionEvent e) {
                ProgressThread t = new ProgressThread("db_clear_cache") {
                    public void run() {
                        TableMetaDataCache.getInstance().clearCache();
                        DatabaseTableValueCellEditor.this.model.lastURL = null;
                        DatabaseTableValueCellEditor.this.model.updateModel();
                    }
                };
                t.start();
            }
        });
        button.setMargin(new Insets(0, 0, 0, 0));
        c.weightx = 0.0D;
        c.insets = new Insets(0, 5, 0, 0);
        this.panel.add(button, c);
    }

    private String getValue() {
        String value = null;
        value = this.operator.getParameters().getParameterOrNull(this.type.getKey());
        return value;
    }

    public boolean rendersLabel() {
        return false;
    }

    public boolean useEditorAsRenderer() {
        return true;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.model.updateModel();
        this.comboBox.setSelectedItem(value);
        return this.panel;
    }

    public Object getCellEditorValue() {
        return this.comboBox.getSelectedItem();
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.model.updateModel();
        this.comboBox.setSelectedItem(value);
        return this.panel;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
        if(operator != null && operator instanceof ConnectionProvider) {
            this.connectionProvider = (ConnectionProvider)operator;
        }

    }

    class DatabaseTableComboBox extends JComboBox {
        private static final long serialVersionUID = 7641636749562465262L;

        private DatabaseTableComboBox() {
            super(DatabaseTableValueCellEditor.this.model);
            this.setEditable(true);
            this.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DatabaseTableValueCellEditor.this.fireEditingStopped();
                }
            });
            this.addFocusListener(new FocusListener() {
                public void focusLost(FocusEvent e) {
                    DatabaseTableValueCellEditor.this.fireEditingStopped();
                }

                public void focusGained(FocusEvent e) {
                    DatabaseTableValueCellEditor.this.model.updateModel();
                }
            });
            this.addPopupMenuListener(new PopupMenuListener() {
                public void popupMenuCanceled(PopupMenuEvent e) {
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                }

                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    if(DatabaseTableValueCellEditor.this.model.updateModel()) {
                        DatabaseTableComboBox.this.hidePopup();
                        DatabaseTableComboBox.this.showPopup();
                    }

                }
            });
        }
    }

    class DatabaseTableComboBoxModel extends AbstractListModel implements ComboBoxModel, Serializable {
        private static final long serialVersionUID = -2984664300141879731L;
        private String lastURL = null;
        private String lastSelectedSchema = null;
        private LinkedList<Object> list = new LinkedList();
        private Object selected = null;

        DatabaseTableComboBoxModel() {
        }

        public boolean updateModel() {
            final String selected = DatabaseTableValueCellEditor.this.getValue();
            boolean schemaChanged = false;
            if(DatabaseTableValueCellEditor.this.mode == DatabaseTableValueCellEditor.Mode.TABLE) {
                String entry = null;
                if(DatabaseTableValueCellEditor.this.operator != null && !DatabaseTableValueCellEditor.this.operator.getParameterAsBoolean("use_default_schema")) {
                    try {
                        entry = DatabaseTableValueCellEditor.this.operator.getParameterAsString("schema_name");
                        if(entry != null && entry.isEmpty()) {
                            entry = null;
                        }
                    } catch (UndefinedParameterError var5) {
                        entry = null;
                    }
                }

                schemaChanged = !Tools.equals(entry, this.lastSelectedSchema);
                this.lastSelectedSchema = entry;
            }

            if(DatabaseTableValueCellEditor.this.connectionProvider != null) {
                final ConnectionEntry entry1 = DatabaseTableValueCellEditor.this.connectionProvider.getConnectionEntry();
                if(entry1 != null && (schemaChanged || this.lastURL == null || !this.lastURL.equals(entry1.getURL()))) {
                    this.lastURL = entry1.getURL();
                    ProgressThread t = new ProgressThread("fetching_database_tables") {
                        public void run() {
                            this.getProgressListener().setTotal(100);
                            this.getProgressListener().setCompleted(10);

                            try {
                                DatabaseTableComboBoxModel.this.list.clear();
                                DatabaseHandler handler = null;

                                try {
                                    handler = DatabaseHandler.getConnectedDatabaseHandler(entry1);
                                } catch (SQLException var11) {
                                    return;
                                }

                                this.getProgressListener().setCompleted(20);
                                if(handler != null) {
                                    try {
                                        Map tableMap = TableMetaDataCache.getInstance().getAllTableMetaData(handler.getDatabaseUrl(), handler, this.getProgressListener(), 20, 90);
                                        Iterator e = tableMap.keySet().iterator();

                                        while(true) {
                                            if(!e.hasNext()) {
                                                this.getProgressListener().setCompleted(90);
                                                break;
                                            }

                                            TableName tn = (TableName)e.next();
                                            switch(DatabaseTableValueCellEditor.this.mode) {
                                                case TABLE:
                                                    if(DatabaseTableComboBoxModel.this.lastSelectedSchema == null || DatabaseTableComboBoxModel.this.lastSelectedSchema.equals(tn.getSchema())) {
                                                        DatabaseTableComboBoxModel.this.list.add(tn.getTableName());
                                                    }
                                                    break;
                                                case SCHEMA:
                                                    if(!DatabaseTableComboBoxModel.this.list.contains(tn.getSchema())) {
                                                        DatabaseTableComboBoxModel.this.list.add(tn.getSchema());
                                                    }
                                                    break;
                                                default:
                                                    throw new RuntimeException("Illegal mode: " + DatabaseTableValueCellEditor.this.mode);
                                            }
                                        }
                                    } catch (SQLException var12) {
                                        return;
                                    }

                                    try {
                                        handler.disconnect();
                                    } catch (SQLException var10) {
                                        ;
                                    }
                                }

                                if(DatabaseTableComboBoxModel.this.getSelectedItem() == null) {
                                    if(DatabaseTableValueCellEditor.this.model.getSize() == 0) {
                                        DatabaseTableComboBoxModel.this.setSelectedItem((Object)null);
                                    } else if(selected != null) {
                                        DatabaseTableComboBoxModel.this.setSelectedItem(selected);
                                    }
                                }

                                DatabaseTableComboBoxModel.this.fireContentsChanged(this, 0, DatabaseTableComboBoxModel.this.list.size() - 1);
                            } finally {
                                this.getProgressListener().complete();
                            }

                        }
                    };
                    t.start();
                    return true;
                }
            }

            return false;
        }

        public Object getSelectedItem() {
            return this.selected;
        }

        public void setSelectedItem(Object object) {
            if(this.selected != null && !this.selected.equals(object) || this.selected == null && object != null) {
                this.selected = object;
                this.fireContentsChanged(this, -1, -1);
            }

        }

        public Object getElementAt(int index) {
            return index >= 0 && index < this.list.size()?this.list.get(index):null;
        }

        public int getSize() {
            return this.list.size();
        }
    }

    private static enum Mode {
        TABLE,
        SCHEMA;

        private Mode() {
        }
    }
}
