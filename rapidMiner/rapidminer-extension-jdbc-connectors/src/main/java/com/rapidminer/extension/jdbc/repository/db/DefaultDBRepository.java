package com.rapidminer.extension.jdbc.repository.db;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.connection.DatabaseConnectionService;
import com.rapidminer.extension.jdbc.tools.jdbc.connection.FieldConnectionEntry;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.repository.*;
import com.rapidminer.repository.gui.RepositoryConfigurationPanel;
import com.rapidminer.repository.internal.db.DBRepository;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.ProgressListener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.util.*;

public class DefaultDBRepository implements Repository, DBRepository {
    private List<Folder> folders = null;
    private List<RepositoryListener> repositoryListeners = new LinkedList();
    private String name = "DB";
    private static final List<DBConnectionToIOObjectConverter> CONVERTERS = new LinkedList();

    public DefaultDBRepository() {
    }

    public static void registerConverter(DBConnectionToIOObjectConverter converter) {
        CONVERTERS.add(converter);
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
        this.fireRefreshed(this);
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

        return false;
    }

    public Folder createFolder(String name) throws RepositoryException {
        throw new RepositoryException("Cannot create folders in connection entry.");
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

    public String getName() {
        return this.name;
    }

    public String getType() {
        return "folder";
    }

    public String getOwner() {
        return null;
    }

    public String getDescription() {
        return "List of defined database connections";
    }

    public boolean isReadOnly() {
        return true;
    }

    public boolean rename(String newName) throws RepositoryException {
        throw new RepositoryException("Cannot rename items in connection entry.");
    }

    public boolean move(Folder newParent) throws RepositoryException {
        throw new RepositoryException("Cannot move items in connection entry.");
    }

    public boolean move(Folder newParent, String newName) throws RepositoryException {
        throw new RepositoryException("Cannot move items in connection entry.");
    }

    public Folder getContainingFolder() {
        return null;
    }

    public boolean willBlock() {
        return false;
    }

    public RepositoryLocation getLocation() {
        try {
            return new RepositoryLocation(this.name, new String[0]);
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

    public void addRepositoryListener(RepositoryListener l) {
        this.repositoryListeners.add(l);
    }

    public void removeRepositoryListener(RepositoryListener l) {
        this.repositoryListeners.remove(l);
    }

    public Entry locate(String entry) throws RepositoryException {
        return RepositoryManager.getInstance((RepositoryAccessor)null).locate(this, entry, false);
    }

    public String getState() {
        return null;
    }

    public String getIconName() {
        return I18N.getMessage(I18N.getGUIBundle(), "gui.repository.db.icon", new Object[0]);
    }

    public Element createXML(Document doc) {
        return null;
    }

    public boolean shouldSave() {
        return false;
    }

    public void postInstall() {
    }

    public void preRemove() {
    }

    public boolean isConfigurable() {
        return false;
    }

    public RepositoryConfigurationPanel makeConfigurationPanel() {
        throw new RuntimeException("DB connection repository is not configurable.");
    }

    private void ensureLoaded() {
        if(this.folders == null) {
            this.folders = new LinkedList();
            Iterator var1 = DatabaseConnectionService.getConnectionEntries().iterator();

            while(var1.hasNext()) {
                FieldConnectionEntry dbConEntry = (FieldConnectionEntry)var1.next();
                this.folders.add(new DBConnectionFolder(this, dbConEntry));
            }
        }

    }

    protected void fireRefreshed(Folder folder) {
        Iterator var2 = this.repositoryListeners.iterator();

        while(var2.hasNext()) {
            RepositoryListener l = (RepositoryListener)var2.next();
            l.folderRefreshed(folder);
        }

    }

    protected List<DBConnectionToIOObjectConverter> getConverters() {
        return CONVERTERS;
    }

    public boolean canRefreshChild(String childName) throws RepositoryException {
        return this.containsEntry(childName);
    }

    static {
        CONVERTERS.add(new StandardDBConnectionToIOObjectConverter());
    }
}
