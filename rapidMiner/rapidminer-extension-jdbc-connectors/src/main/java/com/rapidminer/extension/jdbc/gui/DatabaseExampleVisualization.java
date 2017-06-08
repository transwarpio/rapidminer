package com.rapidminer.extension.jdbc.gui;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.ObjectVisualizer;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.ExtendedJTable;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.LoggingHandler;
import com.rapidminer.tools.Tools;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class DatabaseExampleVisualization implements ObjectVisualizer {
    private DatabaseHandler handler;
    private PreparedStatement statement;

    public DatabaseExampleVisualization(String databaseURL, String userName, String password, int databaseSystem, String tableName, String columnName, LoggingHandler logging) {
        try {
            this.handler = DatabaseHandler.getConnectedDatabaseHandler(databaseURL, userName, password);
            String e = "SELECT * FROM " + this.handler.getStatementCreator().makeIdentifier(tableName) + " WHERE " + this.handler.getStatementCreator().makeIdentifier(columnName) + " = ?";
            this.statement = this.handler.createPreparedStatement(e, false);
        } catch (OperatorException var9) {
            logging.logError("Cannot connect to database: " + var9.getMessage());
        } catch (SQLException var10) {
            logging.logError("Cannot connect to database: " + var10.getMessage());
        }

    }

    public void finalize() throws Throwable {
        super.finalize();
        if(this.statement != null) {
            try {
                this.statement.close();
            } catch (SQLException var3) {
                ;
            }
        }

        if(this.handler != null) {
            try {
                this.handler.disconnect();
            } catch (SQLException var2) {
                ;
            }
        }

    }

    public void startVisualization(Object objId) {
        if(this.handler != null && this.statement != null) {
            try {
                this.statement.setObject(1, objId);
                ResultSet e = this.statement.executeQuery();
                final JDialog dialog = new JDialog(RapidMinerGUI.getMainFrame(), "Example: " + objId, false);
                dialog.getContentPane().setLayout(new BorderLayout());
                if(e == null) {
                    JLabel var12 = new JLabel("No information available for object \'" + objId + "\'.");
                    dialog.getContentPane().add(var12, "Center");
                } else {
                    boolean buttons = e.next();
                    if(!buttons) {
                        JLabel var14 = new JLabel("No information available for object \'" + objId + "\'.");
                        dialog.getContentPane().add(var14, "Center");
                    } else {
                        ResultSetMetaData okButton = e.getMetaData();
                        String[] columnNames = new String[]{"Attribute", "Value"};
                        String[][] data = new String[okButton.getColumnCount()][2];

                        for(int table = 1; table <= data.length; ++table) {
                            data[table - 1][0] = okButton.getColumnName(table);
                            Object tableModel = e.getObject(table);
                            String scrollPane = "?";
                            if(tableModel != null) {
                                if(tableModel instanceof Number) {
                                    scrollPane = Tools.formatIntegerIfPossible(((Number)tableModel).doubleValue());
                                } else {
                                    scrollPane = tableModel.toString();
                                }
                            }

                            data[table - 1][1] = scrollPane;
                        }

                        ExtendedJTable var16 = new ExtendedJTable();
                        var16.setDefaultEditor(Object.class, (TableCellEditor)null);
                        DefaultTableModel var17 = new DefaultTableModel(data, columnNames);
                        var16.setModel(var17);
                        ExtendedJScrollPane var18 = new ExtendedJScrollPane(var16);
                        dialog.getContentPane().add(var18, "Center");
                    }
                }

                JPanel var13 = new JPanel(new FlowLayout());
                JButton var15 = new JButton("Ok");
                var15.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });
                var13.add(var15);
                dialog.getContentPane().add(var13, "South");
                dialog.pack();
                dialog.setLocationRelativeTo(RapidMinerGUI.getMainFrame());
                dialog.setVisible(true);
                if(e != null) {
                    e.close();
                }
            } catch (SQLException var11) {
                SwingTools.showSimpleErrorMessage("cannot_retrieve_obj_inf", var11, new Object[]{objId});
            }

        }
    }

    public String getDetailData(Object objId, String fieldName) {
        if(this.handler != null && this.statement != null) {
            try {
                this.statement.setObject(1, objId);
                ResultSet e = this.statement.executeQuery();
                String resultString = null;
                if(e != null) {
                    boolean dataAvailable = e.next();
                    if(dataAvailable) {
                        Object result = e.getObject(fieldName);
                        String value = "?";
                        if(result != null) {
                            if(result instanceof Number) {
                                value = Tools.formatIntegerIfPossible(((Number)result).doubleValue());
                            } else {
                                value = result.toString();
                            }
                        }

                        resultString = value;
                    }
                }

                if(e != null) {
                    e.close();
                }

                return resultString;
            } catch (SQLException var8) {
                return null;
            }
        } else {
            return null;
        }
    }

    public String[] getFieldNames(Object objId) {
        if(this.handler != null && this.statement != null) {
            try {
                this.statement.setObject(1, objId);
                ResultSet e = this.statement.executeQuery();
                LinkedList result = new LinkedList();
                if(e != null) {
                    boolean resultArray = e.next();
                    if(resultArray) {
                        ResultSetMetaData metaData = e.getMetaData();

                        for(int c = 1; c <= metaData.getColumnCount(); ++c) {
                            result.add(metaData.getColumnName(c));
                        }
                    }
                }

                if(e != null) {
                    e.close();
                }

                String[] var8 = new String[result.size()];
                result.toArray(var8);
                return var8;
            } catch (SQLException var7) {
                return new String[0];
            }
        } else {
            return new String[0];
        }
    }

    public String getTitle(Object objId) {
        return objId instanceof String?(String)objId:((Double)objId).toString();
    }

    public boolean isCapableToVisualize(Object id) {
        return true;
    }

    public void stopVisualization(Object objId) {
    }
}
