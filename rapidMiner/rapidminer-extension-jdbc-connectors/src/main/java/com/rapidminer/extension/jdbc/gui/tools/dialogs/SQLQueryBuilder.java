package com.rapidminer.extension.jdbc.gui.tools.dialogs;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.ColumnIdentifier;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.TableMetaDataCache;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.SQLEditor;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.ParameterService;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.fife.ui.rtextarea.RTextScrollPane;

public class SQLQueryBuilder extends ButtonDialog {
    private static final long serialVersionUID = 1779762368364719191L;
    private final JList<TableName> tableList;
    private final JList<ColumnIdentifier> attributeList;
    private final JTextArea whereTextArea;
    private final SQLEditor sqlQueryTextArea;
    private ButtonDialog surroundingDialog;
    private JLabel connectionStatus;
    private JPanel gridPanel;
    private final Map<TableName, List<ColumnIdentifier>> attributeNameMap;
    private DatabaseHandler databaseHandler;
    private TableMetaDataCache cache;

    public SQLQueryBuilder(DatabaseHandler databaseHandler) {
        this(ApplicationFrame.getApplicationFrame(), databaseHandler);
    }

    public SQLQueryBuilder(Window owner, DatabaseHandler databaseHandler) {
        super(owner, "build_sql_query", ModalityType.APPLICATION_MODAL, new Object[0]);
        this.tableList = new JList(new DefaultListModel());
        this.attributeList = new JList(new DefaultListModel());
        this.whereTextArea = new JTextArea(4, 15);
        this.sqlQueryTextArea = new SQLEditor();
        this.surroundingDialog = null;
        this.connectionStatus = new JLabel();
        this.gridPanel = new JPanel(createGridLayout(1, 3));
        this.attributeNameMap = new LinkedHashMap();
        this.databaseHandler = databaseHandler;
        this.cache = TableMetaDataCache.getInstance();
        if(!"false".equals(ParameterService.getParameterValue("rapidminer.gui.fetch_data_base_table_names"))) {
            try {
                this.retrieveTableNames();
            } catch (SQLException var4) {
                SwingTools.showSimpleErrorMessage("db_connection_failed_simple", var4, new Object[0]);
                this.databaseHandler = null;
            }
        }

    }

    public void setConnectionEntry(ConnectionEntry entry, boolean showErrorMessage) {
        if(entry == null) {
            this.databaseHandler = null;
            if(!"false".equals(ParameterService.getParameterValue("rapidminer.gui.fetch_data_base_table_names"))) {
                this.connectionStatus.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.error.db_connection_failed_short.message", new Object[0]));
                ImageIcon e = SwingTools.createIcon("16/" + I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.error.db_connection_failed_short.icon", new Object[0]));
                this.connectionStatus.setIcon(e);
            }
        } else if(!"false".equals(ParameterService.getParameterValue("rapidminer.gui.fetch_data_base_table_names"))) {
            try {
                this.databaseHandler = DatabaseHandler.getConnectedDatabaseHandler(entry);
                this.retrieveTableNames();
            } catch (SQLException var5) {
                if(showErrorMessage) {
                    SwingTools.showSimpleErrorMessage("db_connection_failed_url", var5, new Object[]{entry.getURL()});
                }

                this.connectionStatus.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.error.db_connection_failed_short.message", new Object[]{entry.getURL()}));
                ImageIcon connectionErrorIcon = SwingTools.createIcon("16/" + I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.error.db_connection_failed_short.icon", new Object[0]));
                this.connectionStatus.setIcon(connectionErrorIcon);
                this.databaseHandler = null;
            }
        }

    }

    public JPanel makeQueryBuilderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        this.tableList.setSelectionMode(2);
        this.tableList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                SQLQueryBuilder.this.updateAttributeNames();
                if(SQLQueryBuilder.this.tableList.getSelectedValuesList().size() > 0) {
                    SQLQueryBuilder.this.updateSQLQuery();
                }

            }
        });
        ExtendedJScrollPane tablePane = new ExtendedJScrollPane(this.tableList);
        tablePane.setBorder(createTitledBorder("表(Tables)"));
        this.gridPanel.add(tablePane);
        this.attributeList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(SQLQueryBuilder.this.attributeList.getSelectedValuesList().size() > 0) {
                    SQLQueryBuilder.this.updateSQLQuery();
                }

            }
        });
        ExtendedJScrollPane attributePane = new ExtendedJScrollPane(this.attributeList);
        attributePane.setBorder(createTitledBorder("属性(Attributes)"));
        this.gridPanel.add(attributePane);
        this.whereTextArea.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                SQLQueryBuilder.this.updateSQLQuery();
            }
        });
        ExtendedJScrollPane whereTextPane = new ExtendedJScrollPane(this.whereTextArea);
        whereTextPane.setBorder(createTitledBorder("条件(Where Clause)"));
        this.gridPanel.add(whereTextPane);
        JLayer layer = new JLayer(this.gridPanel);
        JPanel glassPane = new JPanel(new GridBagLayout());
        if(!"false".equals(ParameterService.getParameterValue("rapidminer.gui.fetch_data_base_table_names"))) {
            this.connectionStatus.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.db_connection_starting.message", new Object[0]));
            ImageIcon c = SwingTools.createIcon("16/" + I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.db_connection_starting.icon", new Object[0]));
            this.connectionStatus.setIcon(c);
        } else {
            this.connectionStatus.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.db_connection_disabled.message", new Object[0]));
        }

        glassPane.add(this.connectionStatus);
        glassPane.setOpaque(false);
        layer.setGlassPane(glassPane);
        glassPane.setVisible(true);
        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = 1;
        c1.weightx = 1.0D;
        c1.weighty = 0.3D;
        c1.gridwidth = 0;
        panel.add(layer, c1);
        this.setVisibilityOfSelectionComponents(!this.connectionStatus.isVisible());
        c1.weighty = 1.0D;
        this.sqlQueryTextArea.setBorder(createTitledBorder("SQL查询(SQL Query)"));
        this.sqlQueryTextArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                SQLQueryBuilder.this.fireStateChanged();
                if(SwingTools.isControlOrMetaDown(e) && e.getKeyCode() == 10) {
                    SQLQueryBuilder.this.tryFireOK();
                }

            }
        });
        RTextScrollPane textScrollPane = new RTextScrollPane(this.sqlQueryTextArea);
        textScrollPane.setLineNumbersEnabled(true);
        textScrollPane.setVerticalScrollBarPolicy(20);
        panel.add(textScrollPane, c1);
        return panel;
    }

    private void setVisibilityOfSelectionComponents(boolean visible) {
        Component[] var2 = this.gridPanel.getComponents();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Component component = var2[var4];
            component.setEnabled(visible);
        }

        this.tableList.setVisible(visible);
        this.attributeList.setVisible(visible);
        this.whereTextArea.setVisible(visible);
    }

    private void updateAttributeNames() {
        LinkedList allColumnIdentifiers = new LinkedList();
        List selection = this.tableList.getSelectedValuesList();
        Iterator identifierArray = selection.iterator();

        while(true) {
            List attributeNames;
            do {
                do {
                    if(!identifierArray.hasNext()) {
                        this.attributeList.removeAll();
                        ColumnIdentifier[] identifierArray1 = new ColumnIdentifier[allColumnIdentifiers.size()];
                        allColumnIdentifiers.toArray(identifierArray1);
                        this.attributeList.setListData(identifierArray1);
                        return;
                    }

                    TableName tableName = (TableName)identifierArray.next();
                    attributeNames = (List)this.attributeNameMap.get(tableName);
                    if(attributeNames == null || attributeNames.isEmpty()) {
                        this.retrieveColumnNames(tableName);
                    }
                } while(attributeNames == null);
            } while(attributeNames.isEmpty());

            Iterator i = attributeNames.iterator();

            while(i.hasNext()) {
                ColumnIdentifier currentIdentifier = (ColumnIdentifier)i.next();
                allColumnIdentifiers.add(currentIdentifier);
            }
        }
    }

    private void appendAttributeName(StringBuffer result, ColumnIdentifier identifier, boolean first, boolean singleTable) {
        if(!first) {
            result.append(", ");
        }

        if(singleTable) {
            result.append(identifier.getFullName(singleTable));
        } else {
            result.append(identifier.getFullName(singleTable) + " AS " + identifier.getAliasName(singleTable));
        }

    }

    private void updateSQLQuery() {
        this.fireStateChanged();
        List tableSelection = this.tableList.getSelectedValuesList();
        if(tableSelection.size() == 0) {
            this.sqlQueryTextArea.setText("");
        } else {
            boolean singleTable = tableSelection.size() == 1;
            StringBuffer result = new StringBuffer("SELECT ");
            List attributeSelection = this.attributeList.getSelectedValuesList();
            boolean first;
            Iterator var9;
            if(singleTable && (attributeSelection.size() == 0 || attributeSelection.size() == this.attributeList.getModel().getSize())) {
                result.append("*");
            } else {
                ColumnIdentifier o;
                if(attributeSelection.size() != 0 && attributeSelection.size() != this.attributeList.getModel().getSize()) {
                    first = true;

                    for(var9 = attributeSelection.iterator(); var9.hasNext(); first = false) {
                        o = (ColumnIdentifier)var9.next();
                        this.appendAttributeName(result, o, first, singleTable);
                    }
                } else {
                    first = true;

                    for(int whereText = 0; whereText < this.attributeList.getModel().getSize(); ++whereText) {
                        o = (ColumnIdentifier)this.attributeList.getModel().getElementAt(whereText);
                        this.appendAttributeName(result, o, first, singleTable);
                        first = false;
                    }
                }
            }

            result.append("\nFROM ");
            first = true;
            var9 = tableSelection.iterator();

            while(var9.hasNext()) {
                Object var11 = var9.next();
                if(first) {
                    first = false;
                } else {
                    result.append(", ");
                }

                TableName tableName = (TableName)var11;
                result.append(this.databaseHandler.getStatementCreator().makeIdentifier(tableName));
            }

            String var10 = this.whereTextArea.getText().trim();
            if(var10.length() > 0) {
                result.append("\nWHERE " + var10);
            }

            this.sqlQueryTextArea.setText(result.toString());
        }
    }

    private void retrieveColumnNames(final TableName tableName) {
        if(this.databaseHandler != null) {
            ProgressThread retrieveTablesThread = new ProgressThread("fetching_database_tables") {
                public void run() {
                    this.getProgressListener().setTotal(100);
                    this.getProgressListener().setCompleted(10);
                    synchronized(SQLQueryBuilder.this.databaseHandler) {
                        try {
                            if(!SQLQueryBuilder.this.databaseHandler.getConnection().isClosed()) {
                                List attributeNames = SQLQueryBuilder.this.cache.getAllColumnNames(SQLQueryBuilder.this.databaseHandler.getDatabaseUrl(), SQLQueryBuilder.this.databaseHandler, tableName);
                                SQLQueryBuilder.this.attributeNameMap.put(tableName, attributeNames);
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        SQLQueryBuilder.this.updateAttributeNames();
                                    }
                                });
                            }
                        } catch (SQLException var4) {
                            ;
                        }

                    }
                }
            };
            retrieveTablesThread.start();
        }

    }

    private void retrieveTableNames() throws SQLException {
        if(this.databaseHandler != null) {
            ProgressThread retrieveTablesThread = new ProgressThread("fetching_database_tables") {
                public void run() {
                    this.getProgressListener().setTotal(100);
                    this.getProgressListener().setCompleted(10);

                    try {
                        SQLQueryBuilder.this.attributeNameMap.clear();
                        synchronized(SQLQueryBuilder.this.databaseHandler) {
                            try {
                                if(SQLQueryBuilder.this.databaseHandler != null && !SQLQueryBuilder.this.databaseHandler.getConnection().isClosed()) {
                                    Map e = SQLQueryBuilder.this.cache.getAllTableMetaData(SQLQueryBuilder.this.databaseHandler.getDatabaseUrl(), SQLQueryBuilder.this.databaseHandler, this.getProgressListener(), 10, 100);
                                    SQLQueryBuilder.this.attributeNameMap.putAll(e);
                                }
                            } catch (SQLException var8) {
                                LogService.getRoot().log(Level.WARNING, "com.rapidminer.gui.properties.celleditors.value.SQLQueryValueCellEditor.connecting_to_database_error", var8);
                                return;
                            }
                        }

                        final TableName[] allNames = new TableName[SQLQueryBuilder.this.attributeNameMap.size()];
                        SQLQueryBuilder.this.attributeNameMap.keySet().toArray(allNames);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                SQLQueryBuilder.this.tableList.removeAll();
                                SQLQueryBuilder.this.tableList.setListData(allNames);
                                SQLQueryBuilder.this.setVisibilityOfSelectionComponents(true);
                                SQLQueryBuilder.this.connectionStatus.setVisible(false);
                            }
                        });
                    } finally {
                        this.getProgressListener().complete();
                    }
                }
            };
            retrieveTablesThread.start();
        }

    }

    public void setQuery(String query) {
        this.sqlQueryTextArea.setText(query);
    }

    public String getQuery() {
        return this.sqlQueryTextArea.getText();
    }

    public void updateAll() {
        try {
            this.retrieveTableNames();
        } catch (SQLException var2) {
            SwingTools.showSimpleErrorMessage("db_connection_failed_simple", var2, new Object[0]);
            this.databaseHandler = null;
        }

        ProgressThread retrieveTablesThread = new ProgressThread("refreshing") {
            public void run() {
                this.getProgressListener().setTotal(100);
                this.getProgressListener().setCompleted(10);

                try {
                    SQLQueryBuilder.this.updateAttributeNames();
                } finally {
                    this.getProgressListener().complete();
                }

            }
        };
        retrieveTablesThread.start();
    }

    public DatabaseHandler getDatabaseHandler() {
        return this.databaseHandler;
    }

    public void setSurroundingDialog(ButtonDialog dialog) {
        this.surroundingDialog = dialog;
    }

    private void tryFireOK() {
        if(this.surroundingDialog != null) {
            this.surroundingDialog.accept(true);
        }

    }
}
