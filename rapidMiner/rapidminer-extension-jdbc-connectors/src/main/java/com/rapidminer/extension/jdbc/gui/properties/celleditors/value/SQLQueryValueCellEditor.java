package com.rapidminer.extension.jdbc.gui.properties.celleditors.value;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.gui.tools.dialogs.SQLQueryBuilder;
import com.rapidminer.extension.jdbc.gui.tools.dialogs.SQLQueryPropertyDialog;
import com.rapidminer.extension.jdbc.parameter.ParameterTypeSQLQuery;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionProvider;
import com.rapidminer.gui.properties.celleditors.value.PropertyValueCellEditor;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.operator.Operator;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.tools.LogService;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class SQLQueryValueCellEditor extends AbstractCellEditor implements PropertyValueCellEditor {
    private static final long serialVersionUID = -771727412083431607L;
    private Operator operator;
    private final JButton button;
    private String sqlQuery;

    public SQLQueryValueCellEditor(final ParameterTypeSQLQuery type) {
        ResourceAction buttonAction = new ResourceAction(true, "build_sql", new Object[0]) {
            private static final long serialVersionUID = -2911499842513746414L;

            public void actionPerformed(ActionEvent e) {
                final SQLQueryBuilder queryBuilder = new SQLQueryBuilder(SwingUtilities.getWindowAncestor(SQLQueryValueCellEditor.this.getTableCellEditorComponent((JTable)null, (Object)null, false, 0, 0)), (DatabaseHandler)null);
                final CountDownLatch latch = new CountDownLatch(1);
                ProgressThread retrieveConnectionThread = new ProgressThread("connection_for_query") {
                    public void run() {
                        try {
                            if(SQLQueryValueCellEditor.this.operator instanceof ConnectionProvider) {
                                ConnectionEntry e2 = ((ConnectionProvider)SQLQueryValueCellEditor.this.operator).getConnectionEntry();
                                queryBuilder.setConnectionEntry(e2, false);
                            }
                        } catch (Exception var5) {
                            LogService.getRoot().log(Level.WARNING, "com.rapidminer.gui.properties.celleditors.value.SQLQueryValueCellEditor.connecting_to_database_error", var5);
                        } finally {
                            latch.countDown();
                        }

                    }
                };
                boolean var12 = false;

                try {
                    var12 = true;
                    SQLQueryPropertyDialog disconnectionThread = new SQLQueryPropertyDialog(type, queryBuilder);
                    if(SQLQueryValueCellEditor.this.operator != null) {
                        String query = null;

                        try {
                            query = SQLQueryValueCellEditor.this.operator.getParameters().getParameter(type.getKey());
                        } catch (UndefinedParameterError var13) {
                            ;
                        }

                        if(query != null) {
                            queryBuilder.setQuery(query);
                        }
                    }

                    retrieveConnectionThread.start();
                    queryBuilder.setSurroundingDialog(disconnectionThread);
                    disconnectionThread.setVisible(true);
                    if(disconnectionThread.isOk()) {
                        SQLQueryValueCellEditor.this.sqlQuery = queryBuilder.getQuery();
                        SQLQueryValueCellEditor.this.fireEditingStopped();
                        var12 = false;
                    } else {
                        SQLQueryValueCellEditor.this.fireEditingCanceled();
                        var12 = false;
                    }
                } finally {
                    if(var12) {
                        ProgressThread disconnectionThread1 = new ProgressThread("disconnect_database") {
                            public void run() {
                                try {
                                    latch.await();
                                } catch (InterruptedException var6) {
                                    ;
                                }

                                try {
                                    DatabaseHandler e2 = queryBuilder.getDatabaseHandler();
                                    if(e2 != null) {
                                        synchronized(e2) {
                                            e2.disconnect();
                                        }
                                    }
                                } catch (SQLException var5) {
                                    LogService.getRoot().log(Level.WARNING, "com.rapidminer.gui.properties.celleditors.value.SQLQueryValueCellEditor.disconnecting_from_database_error", var5);
                                }

                            }
                        };
                        disconnectionThread1.start();
                    }
                }

                ProgressThread disconnectionThread2 = new ProgressThread("disconnect_database") {
                    public void run() {
                        try {
                            latch.await();
                        } catch (InterruptedException var6) {
                            ;
                        }

                        try {
                            DatabaseHandler e2 = queryBuilder.getDatabaseHandler();
                            if(e2 != null) {
                                synchronized(e2) {
                                    e2.disconnect();
                                }
                            }
                        } catch (SQLException var5) {
                            LogService.getRoot().log(Level.WARNING, "com.rapidminer.gui.properties.celleditors.value.SQLQueryValueCellEditor.disconnecting_from_database_error", var5);
                        }

                    }
                };
                disconnectionThread2.start();
            }
        };
        this.button = new JButton(buttonAction);
        this.button.setMargin(new Insets(0, 0, 0, 0));
    }

    public boolean rendersLabel() {
        return false;
    }

    public boolean useEditorAsRenderer() {
        return true;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return this.button;
    }

    public Object getCellEditorValue() {
        return this.sqlQuery;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this.button;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}
