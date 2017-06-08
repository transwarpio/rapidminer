package com.rapidminer.extension.jdbc.repository.db;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.parameter.ParameterTypeDatabaseConnection;
import com.rapidminer.extension.jdbc.tools.jdbc.ColumnIdentifier;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorCreationException;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.parameter.ParameterTypePassword;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.parameter.Parameters;
import com.rapidminer.repository.*;
import com.rapidminer.tools.ProgressListener;
import com.rapidminer.extension.jdbc.tools.jdbc.db.AttributeStore;
import com.rapidminer.extension.jdbc.tools.jdbc.db.AttributeStoreFactory;
import io.transwarp.midas.constant.midas.OperatorNames;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DBConnectionEntry implements IOObjectEntry {
    private TableName tableName;
    private List<ColumnIdentifier> columns;
    private DBConnectionConverterFolder folder;
    private MetaData metaData;
    private DBConnectionToIOObjectConverter converter;

    public DBConnectionEntry(DBConnectionConverterFolder parent, DBConnectionToIOObjectConverter converter, TableName tableName, List<ColumnIdentifier> columns) {
        this.folder = parent;
        this.columns = columns;
        this.converter = converter;
        this.tableName = tableName;
        this.metaData = converter.convertMetaData(this.folder.getConnectionEntry(),
                this.folder.getConnection(), tableName, columns);
        if(tableName.getComment() != null) {
            this.metaData.getAnnotations().setAnnotation("Comment", tableName.getComment());
        }

        this.metaData.getAnnotations().setAnnotation("Source", tableName.toString());
    }

    public DBConnectionEntry(DBConnectionConverterFolder parent, DBConnectionToIOObjectConverter converter,
                             TableName tableName,
                             List<ColumnIdentifier> columns,
                             MetaData metaData) {
        this.folder = parent;
        this.columns = columns;
        this.converter = converter;
        this.tableName = tableName;
        this.metaData = metaData;
        if (this.metaData != null) {
            if(tableName.getComment() != null) {
                this.metaData.getAnnotations().setAnnotation("Comment", tableName.getComment());
            }

            this.metaData.getAnnotations().setAnnotation("Source", tableName.toString());
        }
    }

    public int getRevision() {
        return 1;
    }

    public long getSize() {
        return -1L;
    }

    public long getDate() {
        return -1L;
    }

    public String getName() {
        return this.tableName.toString();
    }

    public String getType() {
        return "data";
    }

    public String getOwner() {
        return this.folder.getConnectionEntry().getUser();
    }

    public String getDescription() {
        return "Table " + this.getName() + " in " + this.folder.getConnectionEntry().getURL();
    }

    public boolean isReadOnly() {
        return true;
    }

    public boolean rename(String newName) throws RepositoryException {
        throw new RepositoryException("This is a read-only view on a database table. Cannot rename entry.");
    }

    public boolean move(Folder newParent) throws RepositoryException {
        throw new RepositoryException("This is a read-only view on a database table. Cannot move entry.");
    }

    public boolean move(Folder newParent, String newName) throws RepositoryException {
        throw new RepositoryException("This is a read-only view on a database table. Cannot move or rename entry.");
    }

    public Folder getContainingFolder() {
        return this.folder;
    }

    public boolean willBlock() {
        return this.metaData == null;
    }

    public RepositoryLocation getLocation() {
        try {
            return new RepositoryLocation(this.folder.getLocation(), this.getName());
        } catch (MalformedRepositoryLocationException var2) {
            throw new RuntimeException(var2);
        }
    }

    public void delete() throws RepositoryException {
        throw new RepositoryException("Cannot delete items in connection entry.");
    }

    public Collection<Action> getCustomActions() {
        return Collections.emptyList();
    }

    public IOObject retrieveData(ProgressListener l) throws RepositoryException {
        try {
            IOObject e = this.converter.convert(this.folder.getConnectionEntry(), this.tableName);
            e.getAnnotations().setAnnotation("Source", this.tableName.toString());
            if(this.tableName.getComment() != null) {
                e.getAnnotations().setAnnotation("Comment", this.tableName.getComment());
            }

            return e;
        } catch (Exception var3) {
            throw new RepositoryException("Failed to read data: " + var3, var3);
        }
    }

    public MetaData retrieveMetaData() throws RepositoryException {
        if(this.metaData == null) {
            this.metaData = converter.convertMetaData(this.folder.getConnectionEntry(),
                    this.folder.getConnection(), tableName, columns);
            if(tableName.getComment() != null) {
                this.metaData.getAnnotations().setAnnotation("Comment", tableName.getComment());
            }

            this.metaData.getAnnotations().setAnnotation("Source", tableName.toString());
        }

        return this.metaData;
    }

    public void storeData(IOObject data, Operator callingOperator, ProgressListener l) throws RepositoryException {
        AttributeStore store = AttributeStoreFactory.
                getAttributeStore(folder.getConnectionEntry());
        store.save(data, this.folder.getConnectionEntry(), tableName.toString());
    }


    private static String URL = "url";
    private static String connection ="connection";
    private static String table = "table";
    private static String db = "db";


    @Override
    public Parameters getParameters() {
        Parameters parameters = new Parameters();
        ConnectionEntry connectionEntry = folder.getConnectionEntry();

        parameters.addParameterType(new ParameterTypeString(URL, ""));
        parameters.setParameter(URL, connectionEntry.getURL());

        parameters.addParameterType(new ParameterTypeDatabaseConnection(connection, "retrieve data from database"));
        parameters.setParameter(connection, connectionEntry.getName());

        parameters.addParameterType(new ParameterTypeString(table, ""));
        parameters.setParameter(table, tableName.toString());

        parameters.addParameterType(new ParameterTypeString(TYPE_NAME, ""));
        parameters.setParameter(TYPE_NAME, OperatorNames.Inceptor());

        return parameters;
    }

    @Override
    public long countWithoutRead() throws OperatorException, OperatorCreationException {
        return converter.countWithoutRead(folder.getConnectionEntry(), tableName);
    }

    public Class<? extends IOObject> getObjectClass() {
        return this.metaData.getObjectClass();
    }
}
