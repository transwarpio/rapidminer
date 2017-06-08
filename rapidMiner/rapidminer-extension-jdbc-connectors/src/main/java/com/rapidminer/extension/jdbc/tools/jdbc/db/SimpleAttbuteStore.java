package com.rapidminer.extension.jdbc.tools.jdbc.db;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.extension.jdbc.operator.io.DatabaseDataReader;
import com.rapidminer.extension.jdbc.tools.jdbc.ColumnIdentifier;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.metadata.AttributeMetaData;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.repository.RepositoryException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liusheng on 5/13/16.
 */
public class SimpleAttbuteStore extends AttributeStore {
    @Override
    public void save(IOObject object, ConnectionEntry connection, String table) throws RepositoryException {
        throw new RepositoryException("This is a read-only view on a database table. Cannot store data here.");
    }

    @Override
    public MetaData convertMetaData(Connection connection, TableName tableName, List<ColumnIdentifier> columns) {
        ExampleSetMetaData metaData = new ExampleSetMetaData();
        Iterator var5 = columns.iterator();

        while(var5.hasNext()) {
            ColumnIdentifier column = (ColumnIdentifier)var5.next();
            metaData.addAttribute(new AttributeMetaData(column.getColumnName(), DatabaseHandler.getRapidMinerTypeIndex(column.getSqlType())));
        }

        return metaData;
    }

    @Override
    public ExampleSet getExampleSet(Connection connection, String table,
                                    ResultSet resultSet, DatabaseDataReader reader) throws SQLException, OperatorException {
        return DatabaseDataReader.createExampleTable(resultSet,
                DatabaseDataReader.getAttributes(resultSet.getMetaData()),
                reader.getParameterAsInt("datamanagement"),
                reader.getLogger(),
                reader.getProgress()).createExampleSet();
    }


}
