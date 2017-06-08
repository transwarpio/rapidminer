package com.rapidminer.extension.jdbc.io;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.core.io.data.DataSet;
import com.rapidminer.core.io.data.DataSetException;
import com.rapidminer.core.io.data.DataSetRow;
import com.rapidminer.core.io.data.ParseException;
import com.rapidminer.extension.jdbc.io.DatabaseDataSource;
import com.rapidminer.tools.Tools;
import java.io.IOException;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.NoSuchElementException;

class DatabaseDataSet implements DataSet {
    private final ResultSetMetaData metaData;
    private int limit;
    private final int numberOfColumns;
    private ResultSet resultSet;
    private int currentRowIndex = -1;
    private Exception exceptionForCurrentRow;
    private final DatabaseDataSource dataSource;
    private int lastHasNextCall = -2;
    private final DataSetRow dataRow = new DataSetRow() {
        public Date getDate(int columnIndex) throws ParseException {
            try {
                Timestamp e = DatabaseDataSet.this.resultSet.getTimestamp(this.getDatabaseColumnIndex(columnIndex));
                return DatabaseDataSet.this.resultSet.wasNull()?null:new Date(e.getTime());
            } catch (SQLException var3) {
                throw new ParseException(var3.getMessage(), var3);
            }
        }

        public String getString(int columnIndex) throws ParseException {
            try {
                String valueString;
                if(DatabaseDataSet.this.metaData.getColumnType(this.getDatabaseColumnIndex(columnIndex)) == 2005) {
                    Clob e = DatabaseDataSet.this.resultSet.getClob(this.getDatabaseColumnIndex(columnIndex));
                    if(e != null) {
                        try {
                            valueString = Tools.readTextFile(e.getCharacterStream());
                        } catch (IOException var5) {
                            throw new ParseException(var5.getMessage(), var5);
                        }
                    } else {
                        valueString = null;
                    }
                } else {
                    valueString = DatabaseDataSet.this.resultSet.getString(this.getDatabaseColumnIndex(columnIndex));
                }

                return DatabaseDataSet.this.resultSet.wasNull()?null:valueString;
            } catch (SQLException var6) {
                throw new ParseException(var6.getMessage(), var6);
            }
        }

        public double getDouble(int columnIndex) throws ParseException {
            try {
                double e = DatabaseDataSet.this.resultSet.getDouble(this.getDatabaseColumnIndex(columnIndex));
                if(DatabaseDataSet.this.resultSet.wasNull()) {
                    e = 0.0D / 0.0;
                }

                return e;
            } catch (SQLException var4) {
                throw new ParseException(var4.getMessage(), var4);
            }
        }

        public boolean isMissing(int columnIndex) {
            try {
                DatabaseDataSet.this.resultSet.getObject(this.getDatabaseColumnIndex(columnIndex));
                return DatabaseDataSet.this.resultSet.wasNull();
            } catch (SQLException var3) {
                return false;
            }
        }

        private int getDatabaseColumnIndex(int columnIndex) {
            return columnIndex + 1;
        }
    };

    DatabaseDataSet(DatabaseDataSource dataSource, int limit) throws SQLException {
        this.limit = limit;
        this.dataSource = dataSource;
        this.resultSet = dataSource.getCachedResultSet();
        this.metaData = this.resultSet.getMetaData();
        this.numberOfColumns = this.metaData.getColumnCount();
    }

    public boolean hasNext() {
        if(this.limit > -1 && this.getCurrentRowIndex() >= this.limit - 1) {
            return false;
        } else {
            this.lastHasNextCall = this.getCurrentRowIndex();
            this.exceptionForCurrentRow = null;

            try {
                boolean e = this.resultSet.next();
                if(!e) {
                    this.exceptionForCurrentRow = new NoSuchElementException();
                }

                return e;
            } catch (SQLException var2) {
                this.exceptionForCurrentRow = var2;
                return false;
            }
        }
    }

    public DataSetRow nextRow() throws DataSetException, NoSuchElementException {
        if(this.limit > -1 && this.getCurrentRowIndex() >= this.limit - 1) {
            throw new NoSuchElementException();
        } else {
            if(this.lastHasNextCall == this.getCurrentRowIndex()) {
                if(this.exceptionForCurrentRow != null) {
                    if(this.exceptionForCurrentRow instanceof NoSuchElementException) {
                        throw (NoSuchElementException)this.exceptionForCurrentRow;
                    }

                    throw new DataSetException(this.exceptionForCurrentRow.getMessage(), this.exceptionForCurrentRow);
                }
            } else {
                this.exceptionForCurrentRow = null;

                try {
                    boolean e = this.resultSet.next();
                    if(!e) {
                        throw new NoSuchElementException();
                    }
                } catch (SQLException var2) {
                    throw new DataSetException(var2.getMessage(), var2);
                }
            }

            ++this.currentRowIndex;
            return this.dataRow;
        }
    }

    public int getCurrentRowIndex() {
        return this.currentRowIndex;
    }

    public void reset() throws DataSetException {
        if(this.currentRowIndex >= 0) {
            try {
                this.resultSet = this.dataSource.getNewResultSet();
                this.currentRowIndex = -1;
            } catch (SQLException var2) {
                throw new DataSetException(var2.getMessage(), var2);
            }
        }
    }

    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }

    public int getNumberOfRows() {
        return -1;
    }

    public void close() throws DataSetException {
    }

    void setLimit(int limit) {
        this.limit = limit;
    }
}
