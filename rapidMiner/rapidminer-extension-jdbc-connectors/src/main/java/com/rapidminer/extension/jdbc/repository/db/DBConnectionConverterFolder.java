package com.rapidminer.extension.jdbc.repository.db;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.ColumnIdentifier;
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.TableName;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.db.AttributeStoreFactory;
import com.rapidminer.extension.jdbc.tools.jdbc.db.InceptorAttributeStore;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.metadata.MetaData;
import com.rapidminer.repository.*;
import com.rapidminer.tools.ProgressListener;
import io.transwarp.midas.utils.Column;
import io.transwarp.midas.utils.Table;
import io.transwarp.midas.utils.TablePropertiesUtils;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

public class DBConnectionConverterFolder implements Folder {
    private final ConnectionEntry entry;
    private final DBConnectionToIOObjectConverter converter;
    private final DefaultDBRepository repository;
    private final DBConnectionFolder parent;
    private Map<TableName, List<ColumnIdentifier>> allTableMetaData;
    private Connection connection = null;
    private List<DataEntry> entries;

    public DBConnectionConverterFolder(DefaultDBRepository dbRepository, DBConnectionFolder parent, ConnectionEntry dbConEntry, DBConnectionToIOObjectConverter converter) throws RepositoryException {
        this.repository = dbRepository;
        this.parent = parent;
        this.entry = dbConEntry;
        this.converter = converter;
        this.ensureLoaded();
    }

    public String getName() {
        return this.converter.getSuffix();
    }

    public String getType() {
        return "folder";
    }

    public String getOwner() {
        return null;
    }

    public String getDescription() {
        return this.getName() + " (" + this.entry.getURL() + ")";
    }

    public boolean isReadOnly() {
        return true;
    }

    public boolean rename(String newName) throws RepositoryException {
        throw new RepositoryException("Cannot rename connection entry.");
    }

    public boolean move(Folder newParent) throws RepositoryException {
        throw new RepositoryException("Cannot move connection entry.");
    }

    public boolean move(Folder newParent, String newName) throws RepositoryException {
        throw new RepositoryException("Cannot move connection entry.");
    }

    public Folder getContainingFolder() {
        return this.parent;
    }

    public boolean willBlock() {
        return false;
    }

    public RepositoryLocation getLocation() {
        try {
            return new RepositoryLocation(this.parent.getLocation(), this.getName());
        } catch (MalformedRepositoryLocationException var2) {
            throw new RuntimeException(var2);
        }
    }

    public void delete() throws RepositoryException {
        throw new RepositoryException("Cannot delete connection entry.");
    }

    public Collection<Action> getCustomActions() {
        return Collections.emptyList();
    }

    public List<DataEntry> getDataEntries() throws RepositoryException {
        return this.entries;
    }

    public List<Folder> getSubfolders() throws RepositoryException {
        return Collections.emptyList();
    }

    public void refresh() throws RepositoryException {
        this.entries = null;
        this.ensureLoaded();
        this.repository.fireRefreshed(this);
    }

    public boolean containsEntry(String name) throws RepositoryException {
        Iterator var2 = this.entries.iterator();

        DataEntry entry;
        do {
            if(!var2.hasNext()) {
                return false;
            }

            entry = (DataEntry)var2.next();
        } while(!entry.getName().equals(name));

        return true;
    }

    public Folder createFolder(String name) throws RepositoryException {
        throw new RepositoryException("Cannot create folder in connection entry.");
    }

    public IOObjectEntry createIOObjectEntry(String name, IOObject ioobject, Operator callingOperator, ProgressListener progressListener) throws RepositoryException {
        throw new RepositoryException("Cannot create items in connection entry.");
    }

    public ProcessEntry createProcessEntry(String name, String processXML) throws RepositoryException {
        throw new RepositoryException("Cannot create items in connection entry.");
    }

    public BlobEntry createBlobEntry(String name) throws RepositoryException {
        throw new RepositoryException("Cannot create items in connection entry.");
    }

    protected ConnectionEntry getConnectionEntry() {
        return this.entry;
    }

    private void initConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String jdbcUrl = entry.getURL();
            String username = entry.getUser();
            String password = new String(entry.getPassword());

            connection = DriverManager.getConnection(jdbcUrl, username, password);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void ensureLoaded() throws RepositoryException {
        if(this.entries == null) {
            this.entries = new LinkedList();
            boolean useInceptor = AttributeStoreFactory.useInceptorStore(this.getConnectionEntry());

            try {
                initConnection();
                DatabaseHandler handler = DatabaseHandler.getHandler(this.connection);

                if (useInceptor) {
                    Table[] tables = TablePropertiesUtils.getTableDesc(connection, connection.getSchema(), null);

                    Map<String, MetaData> metadatas = new HashMap<>();
                    Map<String, Map<String, Map<String, String>>> properties =
                            TablePropertiesUtils.getMetaJava(connection, connection.getSchema(), null);
                    for (String table : properties.keySet()) {
                        MetaData meta = InceptorAttributeStore.genMeta(properties.get(table));
                        metadatas.put(table, meta);
                    }

                    for (Table table : tables) {
                        TableName tName = new TableName(table.name(), connection.getSchema(), connection.getCatalog());
                        MetaData meta = metadatas.get(table.name());
                        List<ColumnIdentifier> columns = new ArrayList<>();
                        for (Column c : table.columns()) {
                            columns.add(new ColumnIdentifier(handler, tName, c.name(), c.dataType(), c.typeName(), null));
                        }
                        if (meta == null) {
                            meta = InceptorAttributeStore.genDefaultMeta(columns);
                        }

                        DBConnectionEntry entry = new DBConnectionEntry(this, this.converter, tName, columns, meta);
                        this.entries.add(entry);
                    }
                } else {
                    this.allTableMetaData = handler.getAllTableMetaData();
                    Iterator<Entry<TableName, List<ColumnIdentifier>>> entryIter = this.allTableMetaData.entrySet().iterator();

                    while(entryIter.hasNext()) {
                        Entry<TableName, List<ColumnIdentifier>> tableEntry = entryIter.next();
                        DBConnectionEntry entry =
                                new DBConnectionEntry(this, this.converter, tableEntry.getKey(), tableEntry.getValue());
                        this.entries.add(entry);
                    }
                }

                // sort the entries
                Collections.sort(this.entries, new Comparator<DataEntry>() {
                    @Override
                    public int compare(DataEntry o1, DataEntry o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            } catch (SQLException e) {
                throw new RepositoryException("fail to load table" + e, e);
            } catch (OperatorException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (this.connection != null){
                        this.connection.close();
                        this.connection = null;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public boolean canRefreshChild(String childName) throws RepositoryException {
        return this.containsEntry(childName);
    }
}
