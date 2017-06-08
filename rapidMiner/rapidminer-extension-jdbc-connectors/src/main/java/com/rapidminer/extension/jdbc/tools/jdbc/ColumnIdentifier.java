package com.rapidminer.extension.jdbc.tools.jdbc;

/**
 * Created by mk on 3/15/16.
 */
public class ColumnIdentifier {
    private final TableName tableName;
    private final String columnName;
    private final int sqlType;
    private final String sqlTypeName;
    private final String remarks;
    private DatabaseHandler databaseHandler;

    public ColumnIdentifier(DatabaseHandler databaseHandler, TableName tableName, String columnName, int sqlType, String sqlTypeName, String remarks) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.databaseHandler = databaseHandler;
        this.sqlType = sqlType;
        this.sqlTypeName = sqlTypeName;
        this.remarks = remarks;
    }

    public TableName getTableName() {
        return this.tableName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public String getFullName(boolean singleTable) {
        return singleTable?this.databaseHandler.getStatementCreator().makeIdentifier(this.columnName):this.databaseHandler.getStatementCreator().makeIdentifier(this.tableName + "." + this.columnName);
    }

    public String getAliasName(boolean singleTable) {
        return singleTable?this.databaseHandler.getStatementCreator().makeIdentifier(this.columnName):this.databaseHandler.getStatementCreator().makeIdentifier(this.tableName + "__" + this.columnName);
    }

    public String getRemarks() {
        return this.remarks;
    }

    public String toString() {
        return this.tableName + "." + this.columnName;
    }

    public int getSqlType() {
        return this.sqlType;
    }

    public String getSqlTypeName() {
        return this.sqlTypeName;
    }
}
