package com.rapidminer.extension.jdbc.tools.jdbc.db;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.extension.jdbc.operator.io.DatabaseDataReader;
import com.rapidminer.extension.jdbc.tools.jdbc.ColumnIdentifier;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.repository.RepositoryException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by liusheng on 5/12/16.
 */
public abstract class AttributeStore {
    public abstract void save(IOObject object, ConnectionEntry connection, String table) throws RepositoryException;
    public abstract MetaData convertMetaData(Connection connection, TableName tableName, List<ColumnIdentifier> columns);
    public abstract ExampleSet getExampleSet(Connection connection,
                                             String table,
                                             ResultSet resultSet,
                                             DatabaseDataReader reader) throws SQLException, OperatorException;
}
