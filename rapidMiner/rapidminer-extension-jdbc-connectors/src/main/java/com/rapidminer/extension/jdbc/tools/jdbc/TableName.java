package com.rapidminer.extension.jdbc.tools.jdbc;

/**
 * Created by mk on 3/15/16.
 */
public class TableName {
    private final String tableName;
    private final String schema;
    private final String catalog;
    private String comment;

    public TableName(String tableName) {
        this(tableName, (String)null, (String)null);
    }

    public TableName(String tableName, String schemaName, String catalogName) {
        this.tableName = tableName;
        this.schema = schemaName;
        this.catalog = catalogName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getSchema() {
        return this.schema;
    }

    public String getCatalog() {
        return this.catalog;
    }

    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (this.catalog == null?0:this.catalog.hashCode());
        result1 = 31 * result1 + (this.schema == null?0:this.schema.hashCode());
        result1 = 31 * result1 + (this.tableName == null?0:this.tableName.hashCode());
        return result1;
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(obj == null) {
            return false;
        } else if(this.getClass() != obj.getClass()) {
            return false;
        } else {
            TableName other = (TableName)obj;
            if(this.catalog == null) {
                if(other.catalog != null) {
                    return false;
                }
            } else if(!this.catalog.equals(other.catalog)) {
                return false;
            }

            if(this.schema == null) {
                if(other.schema != null) {
                    return false;
                }
            } else if(!this.schema.equals(other.schema)) {
                return false;
            }

            if(this.tableName == null) {
                if(other.tableName != null) {
                    return false;
                }
            } else if(!this.tableName.equals(other.tableName)) {
                return false;
            }

            return true;
        }
    }

    public String toString() {
        return this.schema != null?this.schema + "." + this.tableName:this.tableName;
    }

    public void setComment(String comment) {
        if(comment != null && comment.isEmpty()) {
            comment = null;
        }

        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }
}

