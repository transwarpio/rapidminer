package com.rapidminer.extension.jdbc.repository.db;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.operator.io.DatabaseDataReader;
import com.rapidminer.extension.jdbc.tools.jdbc.ColumnIdentifier;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.db.AttributeStore;
import com.rapidminer.extension.jdbc.tools.jdbc.db.AttributeStoreFactory;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.metadata.AttributeMetaData;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.tools.OperatorService;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

public class StandardDBConnectionToIOObjectConverter implements DBConnectionToIOObjectConverter {
    public StandardDBConnectionToIOObjectConverter() {
    }

    public IOObject convert(ConnectionEntry connection, TableName tableName) throws OperatorException {
        DatabaseDataReader reader;
        try {
            AttributeStore attributeStore = AttributeStoreFactory.getAttributeStore(connection);
            reader = (DatabaseDataReader)OperatorService.createOperator(DatabaseDataReader.class);
            reader.setAttributeStore(attributeStore);
            reader.setTable(tableName.getTableName());
        } catch (OperatorCreationException var5) {
            throw new OperatorException("Failed to create database reader: " + var5, var5);
        }

        reader.setParameter("connection", connection.getName());
        reader.setParameter("define_connection", DatabaseHandler.CONNECTION_MODES[0]);
        reader.setParameter("table_name", tableName.getTableName());
        if(tableName.getSchema() != null) {
            reader.setParameter("use_default_schema", String.valueOf(false));
            reader.setParameter("schema_name", tableName.getSchema());
        }

        reader.setParameter("define_query", DatabaseHandler.QUERY_MODES[2]);
        return reader.read();
    }

    public MetaData convertMetaData(ConnectionEntry entry, Connection connection, TableName tableName, List<ColumnIdentifier> columns) {
        AttributeStore attributeStore = AttributeStoreFactory.getAttributeStore(entry);
        return attributeStore.convertMetaData(connection, tableName, columns);
    }

    public String getSuffix() {
        return "Example Sets";
    }

    @Override
    public long countWithoutRead(ConnectionEntry connection, TableName tableName) throws OperatorCreationException, OperatorException {
        DatabaseDataReader reader;
        AttributeStore attributeStore = AttributeStoreFactory.getAttributeStore(connection);
        reader = (DatabaseDataReader)OperatorService.createOperator(DatabaseDataReader.class);
        reader.setAttributeStore(attributeStore);

        reader.setParameter("connection", connection.getName());
        reader.setParameter("define_connection", DatabaseHandler.CONNECTION_MODES[0]);
        reader.setParameter("table_name", tableName.getTableName());
        if(tableName.getSchema() != null) {
            reader.setParameter("use_default_schema", String.valueOf(false));
            reader.setParameter("schema_name", tableName.getSchema());
        }

        reader.setParameter("define_query", DatabaseHandler.QUERY_MODES[2]);
        long count = reader.count();
        return count;
    }
}
