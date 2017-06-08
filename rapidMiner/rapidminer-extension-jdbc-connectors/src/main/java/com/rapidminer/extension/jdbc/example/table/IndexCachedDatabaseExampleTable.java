package com.rapidminer.extension.jdbc.example.table;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.example.Attribute;
import com.rapidminer.example.table.AbstractExampleTable;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.DataRowFactory;
import com.rapidminer.example.table.DataRowReader;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.example.table.NonWritableDataRow;
import com.rapidminer.example.table.ResultSetDataRowReader;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.tools.LoggingHandler;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IndexCachedDatabaseExampleTable extends AbstractExampleTable {
    private static final long serialVersionUID = -3514641049341063136L;
    public static final int DEFAULT_BATCH_SIZE = 1500;
    public static final String INDEX_COLUMN_NAME = "RM_INDEX";
    public static final String MAPPING_TABLE_NAME_PREFIX = "RM_MAPPING_";
    private DatabaseHandler databaseHandler;
    private String tableName;
    private MemoryExampleTable batchExampleTable;
    private int currentBatchStartCursor = -1;
    private int size = -1;
    private int dataManagementType;
    private String mappingTableName;
    private String mappingPrimaryKey;

    public IndexCachedDatabaseExampleTable(DatabaseHandler databaseHandler, String tableName, int dataManagementType, boolean dropMappingTable, LoggingHandler logging) throws SQLException {
        super(new ArrayList());
        this.databaseHandler = databaseHandler;
        this.tableName = tableName;
        this.dataManagementType = dataManagementType;
        this.size = this.getSizeForTable(this.tableName);
        this.createIndex(dropMappingTable, logging);
        this.initAttributes();
        this.updateBatchAndCursors(0);
    }

    private void createIndex(boolean dropMappingTable, LoggingHandler logging) throws SQLException {
        String primaryKeyName = this.getPrimaryKeyName(this.tableName);
        if(primaryKeyName == null) {
            this.mappingTableName = null;
            this.mappingPrimaryKey = null;
            logging.logNote("No primary key found: creating a new primary key with name \'RM_INDEX\' for table \'" + this.tableName + "\'. This might take some time...");
            this.createRMPrimaryKeyIndex(this.databaseHandler, this.tableName);
            logging.logNote("Creation of primary key \'RM_INDEX\' for table \'" + this.tableName + "\' finished.");
        } else if(!primaryKeyName.equals("RM_INDEX")) {
            this.mappingTableName = "RM_MAPPING_" + this.tableName;
            this.mappingPrimaryKey = primaryKeyName;
            boolean exists = false;

            try {
                Statement copyKeyQuery = this.databaseHandler.createStatement(false);
                Throwable ex = null;

                try {
                    ResultSet statement = copyKeyQuery.executeQuery(this.databaseHandler.getStatementCreator().makeSelectEmptySetStatement(this.mappingTableName));
                    if(statement.getMetaData().getColumnCount() > 0) {
                        exists = true;
                    }

                    statement.close();
                } catch (Throwable var89) {
                    ex = var89;
                    throw var89;
                } finally {
                    if(copyKeyQuery != null) {
                        if(ex != null) {
                            try {
                                copyKeyQuery.close();
                            } catch (Throwable var88) {
                                ex.addSuppressed(var88);
                            }
                        } else {
                            copyKeyQuery.close();
                        }
                    }

                }
            } catch (SQLException var95) {
                ;
            }

            if(exists) {
                int copyKeyQuery1 = this.getSizeForTable(this.mappingTableName);
                if(copyKeyQuery1 != this.size) {
                    logging.logWarning("Size of internal mapping table \'" + this.mappingTableName + "\' and data table \'" + this.tableName + "\' differs. Recreate new mapping table!");
                    dropMappingTable = true;
                }
            }

            Throwable statement1;
            Statement ex1;
            String copyKeyQuery2;
            if(exists && dropMappingTable) {
                copyKeyQuery2 = this.databaseHandler.getStatementCreator().makeDropStatement(this.mappingTableName);
                ex1 = this.databaseHandler.createStatement(false);
                statement1 = null;

                try {
                    ex1.executeUpdate(copyKeyQuery2);
                } catch (Throwable var87) {
                    statement1 = var87;
                    throw var87;
                } finally {
                    if(ex1 != null) {
                        if(statement1 != null) {
                            try {
                                ex1.close();
                            } catch (Throwable var83) {
                                statement1.addSuppressed(var83);
                            }
                        } else {
                            ex1.close();
                        }
                    }

                }

                exists = false;
            }

            if(!exists) {
                logging.logNote("Primary key \'" + primaryKeyName + "\' found: creating a new mapping table \'" + this.mappingTableName + "\' which maps from the index \'" + "RM_INDEX" + "\' to the primary key. This might take some time...");
                copyKeyQuery2 = "CREATE TABLE " + this.databaseHandler.getStatementCreator().makeIdentifier(this.mappingTableName) + " AS ( SELECT " + this.databaseHandler.getStatementCreator().makeIdentifier(primaryKeyName) + " FROM " + this.databaseHandler.getStatementCreator().makeIdentifier(this.tableName) + " )";

                try {
                    ex1 = this.databaseHandler.createStatement(true);
                    statement1 = null;

                    try {
                        ex1.execute(copyKeyQuery2);
                    } catch (Throwable var86) {
                        statement1 = var86;
                        throw var86;
                    } finally {
                        if(ex1 != null) {
                            if(statement1 != null) {
                                try {
                                    ex1.close();
                                } catch (Throwable var85) {
                                    statement1.addSuppressed(var85);
                                }
                            } else {
                                ex1.close();
                            }
                        }

                    }
                } catch (SQLException var93) {
                    logging.logWarning("Failed to create mapping table using standard method, attempting secondary option");
                    copyKeyQuery2 = "SELECT " + this.databaseHandler.getStatementCreator().makeIdentifier(primaryKeyName) + " INTO " + this.databaseHandler.getStatementCreator().makeIdentifier(this.mappingTableName) + " FROM " + this.databaseHandler.getStatementCreator().makeIdentifier(this.tableName);
                    Statement statement2 = this.databaseHandler.createStatement(true);
                    Throwable var8 = null;

                    try {
                        statement2.execute(copyKeyQuery2);
                    } catch (Throwable var84) {
                        var8 = var84;
                        throw var84;
                    } finally {
                        if(statement2 != null) {
                            if(var8 != null) {
                                try {
                                    statement2.close();
                                } catch (Throwable var82) {
                                    var8.addSuppressed(var82);
                                }
                            } else {
                                statement2.close();
                            }
                        }

                    }
                }

                logging.logNote("Creating new primary key for mapping table \'" + this.mappingTableName + "\'...");
                this.createRMPrimaryKeyIndex(this.databaseHandler, this.mappingTableName);
                logging.logNote("Creation of mapping table \'" + this.mappingTableName + "\' finished.");
            }
        } else {
            this.mappingTableName = null;
            this.mappingPrimaryKey = null;
        }

    }

    protected void createRMPrimaryKeyIndex(DatabaseHandler databaseHandler, String tableName) throws SQLException {
        String addKeyQuery = "ALTER TABLE " + databaseHandler.getStatementCreator().makeIdentifier(tableName) + " ADD " + databaseHandler.getStatementCreator().makeIdentifier("RM_INDEX") + " INT NOT NULL AUTO_INCREMENT PRIMARY KEY";

        try {
            Statement ex = databaseHandler.createStatement(true, true);
            Throwable statement1 = null;

            try {
                ex.execute(addKeyQuery);
            } catch (Throwable var31) {
                statement1 = var31;
                throw var31;
            } finally {
                if(ex != null) {
                    if(statement1 != null) {
                        try {
                            ex.close();
                        } catch (Throwable var30) {
                            statement1.addSuppressed(var30);
                        }
                    } else {
                        ex.close();
                    }
                }

            }
        } catch (SQLException var34) {
            addKeyQuery = "ALTER TABLE " + databaseHandler.getStatementCreator().makeIdentifier(tableName) + " ADD " + databaseHandler.getStatementCreator().makeIdentifier("RM_INDEX") + " INT NOT NULL IDENTITY(1,1) PRIMARY KEY";
            Statement statement = databaseHandler.createStatement(true, true);
            Throwable var6 = null;

            try {
                statement.execute(addKeyQuery);
            } catch (Throwable var29) {
                var6 = var29;
                throw var29;
            } finally {
                if(statement != null) {
                    if(var6 != null) {
                        try {
                            statement.close();
                        } catch (Throwable var28) {
                            var6.addSuppressed(var28);
                        }
                    } else {
                        statement.close();
                    }
                }

            }
        }

    }

    private String getPrimaryKeyName(String tableName) throws SQLException {
        DatabaseMetaData meta = this.databaseHandler.getConnection().getMetaData();
        ResultSet primaryKeys = meta.getPrimaryKeys((String)null, (String)null, tableName);
        String primaryKeyName = null;
        if(primaryKeys.next()) {
            primaryKeyName = primaryKeys.getString(4);
        }

        primaryKeys.close();
        return primaryKeyName;
    }

    private void initAttributes() throws SQLException {
        String limitedQuery = this.databaseHandler.getStatementCreator().makeSelectEmptySetStatement(this.tableName);
        Statement attributeStatement = this.databaseHandler.createStatement(false);
        Throwable var3 = null;

        try {
            ResultSet attributeResultSet = attributeStatement.executeQuery(limitedQuery);
            Throwable var5 = null;

            try {
                List attributes = DatabaseHandler.createAttributes(attributeResultSet);
                Iterator a = attributes.iterator();

                while(a.hasNext()) {
                    if(((Attribute)a.next()).getName().equals("RM_INDEX")) {
                        a.remove();
                    }
                }

                this.addAttributes(attributes);
            } catch (Throwable var29) {
                var5 = var29;
                throw var29;
            } finally {
                if(attributeResultSet != null) {
                    if(var5 != null) {
                        try {
                            attributeResultSet.close();
                        } catch (Throwable var28) {
                            var5.addSuppressed(var28);
                        }
                    } else {
                        attributeResultSet.close();
                    }
                }

            }
        } catch (Throwable var31) {
            var3 = var31;
            throw var31;
        } finally {
            if(attributeStatement != null) {
                if(var3 != null) {
                    try {
                        attributeStatement.close();
                    } catch (Throwable var27) {
                        var3.addSuppressed(var27);
                    }
                } else {
                    attributeStatement.close();
                }
            }

        }
    }

    private void updateBatchAndCursors(int desiredRow) throws SQLException {
        ++desiredRow;
        boolean newBatch = false;
        int newOffset = this.currentBatchStartCursor;
        if(desiredRow > this.currentBatchStartCursor + 1350) {
            newOffset = desiredRow - 150;
            newBatch = true;
        } else if(desiredRow < this.currentBatchStartCursor) {
            newOffset = desiredRow - 1050;
            newBatch = true;
        }

        if(newOffset < 1) {
            newOffset = 1;
            newBatch = true;
        }

        if(newBatch) {
            String limitedQuery;
            Statement batchStatement;
            Throwable var6;
            ResultSet batchResultSet;
            Throwable var8;
            if(this.mappingTableName == null) {
                limitedQuery = "SELECT * FROM " + this.databaseHandler.getStatementCreator().makeIdentifier(this.tableName) + " WHERE " + this.databaseHandler.getStatementCreator().makeIdentifier("RM_INDEX") + " >= " + newOffset + " AND " + this.databaseHandler.getStatementCreator().makeIdentifier("RM_INDEX") + " < " + (newOffset + 1500);
                batchStatement = this.databaseHandler.createStatement(false);
                var6 = null;

                try {
                    batchResultSet = batchStatement.executeQuery(limitedQuery);
                    var8 = null;

                    try {
                        this.batchExampleTable = this.createExampleTableFromBatch(batchResultSet);
                    } catch (Throwable var80) {
                        var8 = var80;
                        throw var80;
                    } finally {
                        if(batchResultSet != null) {
                            if(var8 != null) {
                                try {
                                    batchResultSet.close();
                                } catch (Throwable var78) {
                                    var8.addSuppressed(var78);
                                }
                            } else {
                                batchResultSet.close();
                            }
                        }

                    }
                } catch (Throwable var83) {
                    var6 = var83;
                    throw var83;
                } finally {
                    if(batchStatement != null) {
                        if(var6 != null) {
                            try {
                                batchStatement.close();
                            } catch (Throwable var77) {
                                var6.addSuppressed(var77);
                            }
                        } else {
                            batchStatement.close();
                        }
                    }

                }

                this.currentBatchStartCursor = newOffset;
            } else {
                limitedQuery = "SELECT * FROM " + this.databaseHandler.getStatementCreator().makeIdentifier(this.tableName) + "," + this.databaseHandler.getStatementCreator().makeIdentifier(this.mappingTableName) + " WHERE " + this.databaseHandler.getStatementCreator().makeIdentifier("RM_INDEX") + " >= " + newOffset + " AND " + this.databaseHandler.getStatementCreator().makeIdentifier("RM_INDEX") + " < " + (newOffset + 1500) + " AND " + this.databaseHandler.getStatementCreator().makeIdentifier(this.tableName) + "." + this.databaseHandler.getStatementCreator().makeIdentifier(this.mappingPrimaryKey) + " = " + this.databaseHandler.getStatementCreator().makeIdentifier(this.mappingTableName) + "." + this.databaseHandler.getStatementCreator().makeIdentifier(this.mappingPrimaryKey);
                batchStatement = this.databaseHandler.createStatement(false);
                var6 = null;

                try {
                    batchResultSet = batchStatement.executeQuery(limitedQuery);
                    var8 = null;

                    try {
                        this.batchExampleTable = this.createExampleTableFromBatch(batchResultSet);
                    } catch (Throwable var81) {
                        var8 = var81;
                        throw var81;
                    } finally {
                        if(batchResultSet != null) {
                            if(var8 != null) {
                                try {
                                    batchResultSet.close();
                                } catch (Throwable var79) {
                                    var8.addSuppressed(var79);
                                }
                            } else {
                                batchResultSet.close();
                            }
                        }

                    }
                } catch (Throwable var86) {
                    var6 = var86;
                    throw var86;
                } finally {
                    if(batchStatement != null) {
                        if(var6 != null) {
                            try {
                                batchStatement.close();
                            } catch (Throwable var76) {
                                var6.addSuppressed(var76);
                            }
                        } else {
                            batchStatement.close();
                        }
                    }

                }

                this.currentBatchStartCursor = newOffset;
            }
        }

    }

    private MemoryExampleTable createExampleTableFromBatch(ResultSet batchResultSet) {
        ArrayList attributes = new ArrayList(this.getAttributes().length);
        Attribute[] reader = this.getAttributes();
        int var4 = reader.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Attribute attribute = reader[var5];
            attributes.add(attribute);
        }

        ResultSetDataRowReader var7 = new ResultSetDataRowReader(new DataRowFactory(this.dataManagementType, '.'), attributes, batchResultSet);
        return new MemoryExampleTable(attributes, var7);
    }

    public DataRow getDataRow(int index) {
        try {
            this.updateBatchAndCursors(index);
            return new NonWritableDataRow(this.batchExampleTable.getDataRow(index - this.currentBatchStartCursor + 1));
        } catch (SQLException var3) {
            throw new RuntimeException("Cannot retrieve data from database: " + var3, var3);
        }
    }

    public DataRowReader getDataRowReader() {
        return new IndexCachedDatabaseExampleTable.CachedDataRowReader();
    }

    private int getSizeForTable(String sizeTable) {
        int size = 0;
        String countQuery = "SELECT count(*) FROM " + this.databaseHandler.getStatementCreator().makeIdentifier(sizeTable);

        try {
            Statement countStatement = this.databaseHandler.createStatement(false);
            Throwable var5 = null;

            try {
                ResultSet countResultSet = countStatement.executeQuery(countQuery);
                Throwable var7 = null;

                try {
                    countResultSet.next();
                    size = countResultSet.getInt(1);
                } catch (Throwable var32) {
                    var7 = var32;
                    throw var32;
                } finally {
                    if(countResultSet != null) {
                        if(var7 != null) {
                            try {
                                countResultSet.close();
                            } catch (Throwable var31) {
                                var7.addSuppressed(var31);
                            }
                        } else {
                            countResultSet.close();
                        }
                    }

                }
            } catch (Throwable var34) {
                var5 = var34;
                throw var34;
            } finally {
                if(countStatement != null) {
                    if(var5 != null) {
                        try {
                            countStatement.close();
                        } catch (Throwable var30) {
                            var5.addSuppressed(var30);
                        }
                    } else {
                        countStatement.close();
                    }
                }

            }
        } catch (SQLException var36) {
            ;
        }

        return size;
    }

    public int size() {
        return this.size;
    }

    private class CachedDataRowReader implements DataRowReader {
        private int currentTotalCursor;

        private CachedDataRowReader() {
            this.currentTotalCursor = 0;
        }

        public boolean hasNext() {
            return this.currentTotalCursor < IndexCachedDatabaseExampleTable.this.size();
        }

        public DataRow next() {
            DataRow dataRow = IndexCachedDatabaseExampleTable.this.getDataRow(this.currentTotalCursor);
            ++this.currentTotalCursor;
            return dataRow;
        }

        public void remove() {
            throw new UnsupportedOperationException("The method \'remove\' is not supported by DataRowReaders on databases!");
        }
    }
}
