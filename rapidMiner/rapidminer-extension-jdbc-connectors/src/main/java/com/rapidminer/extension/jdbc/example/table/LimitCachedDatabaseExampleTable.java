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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class LimitCachedDatabaseExampleTable extends AbstractExampleTable {
    private static final long serialVersionUID = -3514641049341063136L;
    private static final int DEFAULT_BATCH_SIZE = 1500;
    private DatabaseHandler databaseHandler;
    private String tableName;
    private MemoryExampleTable batchExampleTable;
    private int currentBatchStartCursor = -1;
    private int size = -1;
    private int dataManagementType;

    public LimitCachedDatabaseExampleTable(DatabaseHandler databaseHandler, String tableName, int dataManagementType) throws SQLException {
        super(new ArrayList());
        this.databaseHandler = databaseHandler;
        this.tableName = tableName;
        this.dataManagementType = dataManagementType;
        this.initAttributes();
        this.updateBatchAndCursors(0);
    }

    private void initAttributes() throws SQLException {
        String limitedQuery = this.databaseHandler.getStatementCreator().makeSelectEmptySetStatement(this.tableName);
        Statement attributeStatement = this.databaseHandler.createStatement(false);
        Throwable var3 = null;

        try {
            ResultSet attributeResultSet = attributeStatement.executeQuery(limitedQuery);
            Throwable var5 = null;

            try {
                this.addAttributes(DatabaseHandler.createAttributes(attributeResultSet));
            } catch (Throwable var28) {
                var5 = var28;
                throw var28;
            } finally {
                if(attributeResultSet != null) {
                    if(var5 != null) {
                        try {
                            attributeResultSet.close();
                        } catch (Throwable var27) {
                            var5.addSuppressed(var27);
                        }
                    } else {
                        attributeResultSet.close();
                    }
                }

            }
        } catch (Throwable var30) {
            var3 = var30;
            throw var30;
        } finally {
            if(attributeStatement != null) {
                if(var3 != null) {
                    try {
                        attributeStatement.close();
                    } catch (Throwable var26) {
                        var3.addSuppressed(var26);
                    }
                } else {
                    attributeStatement.close();
                }
            }

        }

    }

    private void updateBatchAndCursors(int desiredRow) throws SQLException {
        boolean newBatch = false;
        int newOffset = this.currentBatchStartCursor;
        if(desiredRow > this.currentBatchStartCursor + 1350) {
            newOffset = desiredRow - 150;
            newBatch = true;
        } else if(desiredRow < this.currentBatchStartCursor) {
            newOffset = desiredRow - 1050;
            newBatch = true;
        }

        if(newOffset < 0) {
            newOffset = 0;
            newBatch = true;
        }

        if(newBatch) {
            String limitedQuery = "SELECT * FROM " + this.databaseHandler.getStatementCreator().makeIdentifier(this.tableName) + " LIMIT " + 1500 + " OFFSET " + newOffset;
            Statement batchStatement = this.databaseHandler.createStatement(false);
            Throwable var6 = null;

            try {
                ResultSet batchResultSet = batchStatement.executeQuery(limitedQuery);
                Throwable var8 = null;

                try {
                    this.batchExampleTable = this.createExampleTableFromBatch(batchResultSet);
                } catch (Throwable var31) {
                    var8 = var31;
                    throw var31;
                } finally {
                    if(batchResultSet != null) {
                        if(var8 != null) {
                            try {
                                batchResultSet.close();
                            } catch (Throwable var30) {
                                var8.addSuppressed(var30);
                            }
                        } else {
                            batchResultSet.close();
                        }
                    }

                }
            } catch (Throwable var33) {
                var6 = var33;
                throw var33;
            } finally {
                if(batchStatement != null) {
                    if(var6 != null) {
                        try {
                            batchStatement.close();
                        } catch (Throwable var29) {
                            var6.addSuppressed(var29);
                        }
                    } else {
                        batchStatement.close();
                    }
                }

            }

            this.currentBatchStartCursor = newOffset;
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
            return new NonWritableDataRow(this.batchExampleTable.getDataRow(index - this.currentBatchStartCursor));
        } catch (SQLException var3) {
            throw new RuntimeException("Cannot retrieve data from database: " + var3);
        }
    }

    public DataRowReader getDataRowReader() {
        return new LimitCachedDatabaseExampleTable.CachedDataRowReader();
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

    private class CachedDataRowReader implements DataRowReader {
        private int currentTotalCursor;

        private CachedDataRowReader() {
            this.currentTotalCursor = 0;
        }

        public boolean hasNext() {
            return this.currentTotalCursor < LimitCachedDatabaseExampleTable.this.size();
        }

        public DataRow next() {
            DataRow dataRow = LimitCachedDatabaseExampleTable.this.getDataRow(this.currentTotalCursor);
            ++this.currentTotalCursor;
            return dataRow;
        }

        public void remove() {
            throw new UnsupportedOperationException("The method \'remove\' is not supported by DataRowReaders on databases!");
        }
    }
}
