package com.rapidminer.extension.jdbc.repository.db;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseHandler;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.ConnectionEntry;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.FieldConnectionEntry;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.repository.*;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.ProgressListener;

import javax.swing.*;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class DBConnectionFolder implements Folder {
    private ConnectionEntry entry;
    private DefaultDBRepository repository;
    private List<Folder> folders;

    public DBConnectionFolder(DefaultDBRepository dbRepository, FieldConnectionEntry dbConEntry) {
        this.repository = dbRepository;
        this.entry = dbConEntry;
    }

    public String getName() {
        return this.entry.getName();
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
        throw new RepositoryException("This is a read-only view on a database. Cannot rename entries.");
    }

    public boolean move(Folder newParent) throws RepositoryException {
        throw new RepositoryException("This is a read-only view on a database. Cannot move entries.");
    }

    public boolean move(Folder newParent, String newName) throws RepositoryException {
        throw new RepositoryException("This is a read-only view on a database. Cannot move or rename entries");
    }

    public Folder getContainingFolder() {
        return this.repository;
    }

    public boolean willBlock() {
        return this.folders == null;
    }

    public RepositoryLocation getLocation() {
        try {
            return new RepositoryLocation(this.repository.getLocation(), this.getName());
        } catch (MalformedRepositoryLocationException var2) {
            throw new RuntimeException(var2);
        }
    }

    public void delete() throws RepositoryException {
        throw new RepositoryException("This is a read-only view on a database. Cannot delete entries.");
    }

    public Collection<Action> getCustomActions() {
        return Collections.emptyList();
    }

    public List<DataEntry> getDataEntries() throws RepositoryException {
        return Collections.emptyList();
    }

    public List<Folder> getSubfolders() throws RepositoryException {
        this.ensureLoaded();
        return this.folders;
    }

    public void refresh() throws RepositoryException {
        this.folders = null;
        this.ensureLoaded();
        this.repository.fireRefreshed(this);
    }

    public boolean containsEntry(String name) throws RepositoryException {
        this.ensureLoaded();
        Iterator var2 = this.folders.iterator();

        Folder entry;
        do {
            if(!var2.hasNext()) {
                return false;
            }

            entry = (Folder)var2.next();
        } while(!entry.getName().equals(name));

        return true;
    }

    public Folder createFolder(String name) throws RepositoryException {
        throw new RepositoryException("This is a read-only view on a database. Cannot create folders.");
    }

    public IOObjectEntry createIOObjectEntry(String name, IOObject ioobject, Operator callingOperator, ProgressListener progressListener) throws RepositoryException {
        throw new RepositoryException("This is a read-only view on a database. Cannot create new entries.");
    }

    public ProcessEntry createProcessEntry(String name, String processXML) throws RepositoryException {
        throw new RepositoryException("This is a read-only view on a database. Cannot create new entries.");
    }

    public BlobEntry createBlobEntry(String name) throws RepositoryException {
        throw new RepositoryException("This is a read-only view on a database. Cannot create new entries.");
    }

    protected ConnectionEntry getConnectionEntry() {
        return this.entry;
    }

    private void ensureLoaded() throws RepositoryException {
        if(this.folders == null) {
            this.folders = new LinkedList();

            Iterator<DBConnectionToIOObjectConverter> var3 = this.repository.getConverters().iterator();

            while(var3.hasNext()) {
                DBConnectionToIOObjectConverter converter = var3.next();
                this.folders.add(new DBConnectionConverterFolder(this.repository, this, this.entry, converter));
            }
        }

    }

    public boolean canRefreshChild(String childName) throws RepositoryException {
        return this.containsEntry(childName);
    }
}
