package com.rapidminer.extension.jdbc.repository.db;

import com.rapidminer.extension.jdbc.tools.jdbc.ColumnIdentifier;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.metadata.MetaData;

import java.sql.Connection;
import java.util.List;

public interface DBConnectionToIOObjectConverter {
    IOObject convert(ConnectionEntry var1, TableName var2) throws OperatorException;

    MetaData convertMetaData(ConnectionEntry entry, Connection var1, TableName var2, List<ColumnIdentifier> var3);

    String getSuffix();

    long countWithoutRead(ConnectionEntry connection, TableName tableName) throws OperatorException, OperatorCreationException;
}