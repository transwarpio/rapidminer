package com.rapidminer.extension.jdbc.example.table;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.example.Attribute;
import com.rapidminer.example.table.AbstractExampleTable;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.DataRowReader;
import com.rapidminer.example.table.DatabaseDataRow;
import com.rapidminer.example.table.DatabaseDataRowReader;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.tools.LogService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;

public class DatabaseExampleTable extends AbstractExampleTable {
    private static final long serialVersionUID = -3683705313093987482L;
    private transient ResultSet resultSet;
    private transient DatabaseHandler databaseHandler;
    private transient Statement statement;
    private String tableName;
    private int size = 0;

    private DatabaseExampleTable(List<Attribute> attributes, DatabaseHandler databaseHandler, String tableName) throws SQLException {
        super(attributes);
        this.databaseHandler = databaseHandler;
        this.tableName = tableName;
        this.resetResultSet();
    }

    public static DatabaseExampleTable createDatabaseExampleTable(DatabaseHandler databaseHandler, String tableName) throws SQLException {
        Statement statement = databaseHandler.createStatement(false);
        Throwable var3 = null;

        DatabaseExampleTable var8;
        try {
            ResultSet rs = statement.executeQuery(databaseHandler.getStatementCreator().makeSelectEmptySetStatement(tableName));
            Throwable var5 = null;

            try {
                List attributes = DatabaseHandler.createAttributes(rs);
                DatabaseExampleTable table = new DatabaseExampleTable(attributes, databaseHandler, tableName);
                var8 = table;
            } catch (Throwable var31) {
                var5 = var31;
                throw var31;
            } finally {
                if(rs != null) {
                    if(var5 != null) {
                        try {
                            rs.close();
                        } catch (Throwable var30) {
                            var5.addSuppressed(var30);
                        }
                    } else {
                        rs.close();
                    }
                }

            }
        } catch (Throwable var33) {
            var3 = var33;
            throw var33;
        } finally {
            if(statement != null) {
                if(var3 != null) {
                    try {
                        statement.close();
                    } catch (Throwable var29) {
                        var3.addSuppressed(var29);
                    }
                } else {
                    statement.close();
                }
            }

        }

        return var8;
    }

    private void resetResultSet() throws SQLException {
        if(this.statement != null) {
            this.statement.close();
            this.statement = null;
        }

        this.statement = this.databaseHandler.createStatement(true, true);
        this.resultSet = this.statement.executeQuery(this.databaseHandler.getStatementCreator().makeSelectAllStatement(this.tableName));
    }

    public DataRowReader getDataRowReader() {
        try {
            return new DatabaseDataRowReader(this.resultSet);
        } catch (SQLException var2) {
            throw new RuntimeException("Error while creating database DataRowReader: " + var2, var2);
        }
    }

    public DataRow getDataRow(int index) {
        try {
            this.resultSet.absolute(index + 1);
            DatabaseDataRow e = new DatabaseDataRow(this.resultSet);
            return e;
        } catch (SQLException var3) {
            LogService.getRoot().log(Level.WARNING, "com.rapidminer.example.table.DatabaseExampleTable.retrieving_data_row_error", var3.getMessage());
            return null;
        }
    }

    public int addAttribute(Attribute attribute) {
        int index = super.addAttribute(attribute);
        if(this.databaseHandler == null) {
            return index;
        } else {
            try {
                this.close();
                this.databaseHandler.addColumn(attribute, this.tableName);
                this.resetResultSet();
                return index;
            } catch (SQLException var4) {
                throw new RuntimeException("Error while adding a column \'" + attribute.getName() + "\'to database: " + var4, var4);
            }
        }
    }

    public void removeAttribute(Attribute attribute) {
        super.removeAttribute(attribute);

        try {
            this.close();
            this.databaseHandler.removeColumn(attribute, this.tableName);
            this.resetResultSet();
        } catch (SQLException var3) {
            throw new RuntimeException("Error while removing a column \'" + attribute.getName() + "\' from database: " + var3, var3);
        }
    }

    public int size() {
        if(this.size < 0) {
            String countQuery = this.databaseHandler.getStatementCreator().makeSelectSizeStatement(this.tableName);

            try {
                Statement countStatement = this.databaseHandler.createStatement(false);
                Throwable var3 = null;

                try {
                    ResultSet countResultSet = countStatement.executeQuery(countQuery);
                    Throwable var5 = null;

                    try {
                        countResultSet.next();
                        this.size = countResultSet.getInt(1);
                    } catch (Throwable var30) {
                        var5 = var30;
                        throw var30;
                    } finally {
                        if(countResultSet != null) {
                            if(var5 != null) {
                                try {
                                    countResultSet.close();
                                } catch (Throwable var29) {
                                    var5.addSuppressed(var29);
                                }
                            } else {
                                countResultSet.close();
                            }
                        }

                    }
                } catch (Throwable var32) {
                    var3 = var32;
                    throw var32;
                } finally {
                    if(countStatement != null) {
                        if(var3 != null) {
                            try {
                                countStatement.close();
                            } catch (Throwable var28) {
                                var3.addSuppressed(var28);
                            }
                        } else {
                            countStatement.close();
                        }
                    }

                }
            } catch (SQLException var34) {
                ;
            }
        }

        return this.size;
    }

    private void close() {
        if(this.statement != null) {
            try {
                this.statement.close();
                this.statement = null;
            } catch (SQLException var2) {
                LogService.getRoot().log(Level.WARNING, "com.rapidminer.example.table.DatabaseExampleTable.closing_result_set_error", var2.getMessage());
            }
        }

    }

    protected void finalize() {
        this.close();
    }
}
