package io.transwarp.midas.remoteImpl;

import com.rapidminer.repository.*;
import com.rapidminer.repository.internal.remote.RemoteEntry;
import com.rapidminer.repository.internal.remote.RemoteFolder;
import com.rapidminer.repository.internal.remote.RemoteRepository;
import com.rapidminer.tools.I18N;
import io.transwarp.midas.client.MidasClient;
import io.transwarp.midas.client.MidasClientFactory;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

public abstract class RemoteEntryImpl implements RemoteEntry {
    private String name;
    private RemoteRepository repository;
    private RemoteFolder containingFolder;

    RemoteEntryImpl(String name, RemoteFolder containingFolder, RemoteRepository repository) {
        this.name = name;
        this.repository = repository;
        this.containingFolder = containingFolder;
    }

    @Override
    public RemoteRepository getRepository() {
        return repository;
    }

    protected void setRepository(RemoteRepository repository) {
        this.repository = repository;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Folder getContainingFolder() {
        return containingFolder;
    }

    @Override
    public boolean rename(String newName) throws RepositoryException {
        checkRename(getContainingFolder(), newName);
        MidasClient midasClient = MidasClientFactory.getClientInstance();
        midasClient.move(getPath(), containingFolder.getPath() + "/" + newName);
        this.name = newName;
        ((RemoteRepositoryImpl)getRepository()).fireEntryRenamed(this);
        return true;
    }

    /**
     * Checks if renaming or moving the entry is possible. If it is not possible, a
     * {@link RepositoryException} will be thrown.
     */
    void checkRename(Folder newParent, String newName) throws RepositoryException {

        if (!RepositoryLocation.isNameValid(newName)) {
            throw new RepositoryException(I18N.getMessage(I18N.getErrorBundle(), "repository.illegal_entry_name", newName,
                    getLocation()));
        }

        if (containingFolder != null) {
            List<DataEntry> dataEntries = newParent.getDataEntries();
            for (Entry entry : dataEntries) {
                if (entry.getName().equals(newName)) {
                    throw new RepositoryException(I18N.getMessage(I18N.getErrorBundle(),
                            "repository.repository_entry_with_same_name_already_exists", newName));
                }
            }
            List<Folder> subfolders = newParent.getSubfolders();
            for (Folder folder : subfolders) {
                if (folder.getName().equals(newName)) {
                    throw new RepositoryException(I18N.getMessage(I18N.getErrorBundle(),
                            "repository.repository_entry_with_same_name_already_exists", newName));
                }
            }
        }
    }

    void removeChild(RemoteFolder folder, RemoteEntry child) throws RepositoryException {
        int index;
        if (child instanceof RemoteFolder) {
            index = folder.getSubfolders().indexOf(child);
            folder.getSubfolders().remove(child);
        } else {
            index = folder.getDataEntries().indexOf(child) + folder.getDataEntries().size();
            folder.getDataEntries().remove(child);
        }
        ((RemoteRepositoryImpl)getRepository()).fireEntryRemoved(child, folder, index);
    }

    void addChild(RemoteFolder folder, RemoteEntry child) throws RepositoryException {
        if (child instanceof RemoteFolder) {
            folder.getSubfolders().add((Folder) child);
        } else {
            folder.getDataEntries().add((DataEntry) child);
        }
        ((RemoteRepositoryImpl)getRepository()).fireEntryAdded(child, folder);
    }

    @Override
    public final boolean move(Folder newParent) throws RepositoryException {
        checkRename(newParent, getName());
        handleMove(newParent, getName());
        removeChild(this.containingFolder,this);
        this.containingFolder = (RemoteFolder) newParent;
        addChild(this.containingFolder,this);
        return true;
    }

    @Override
    public final boolean move(Folder newParent, String newName) throws RepositoryException {
        checkRename(newParent, newName);
        handleMove(newParent, newName);

        removeChild(this.containingFolder,this);

        if (newName != null) {
            this.name = newName;
        }

        this.containingFolder = (RemoteFolder) newParent;
        addChild(this.containingFolder, this);

        return true;
    }

    void handleMove(Folder newParent, String newName)
            throws RepositoryException {
        MidasClient midasClient = MidasClientFactory.getClientInstance();
        midasClient.move(getPath(), ((RemoteEntry)newParent).getPath() + "/" + newName);
    }

    @Override
    public void delete() throws RepositoryException {
        MidasClient client = MidasClientFactory.getClientInstance();
        client.deleteSharing(getPath());
        RemoteFolder parent = (RemoteFolder) getContainingFolder();
        if (parent != null) {
            removeChild(this.containingFolder,this);
        }
    }

    @Override
    public RepositoryLocation getLocation() {
        try {
            if (getContainingFolder() != null) {
                return new RepositoryLocation(getContainingFolder().getLocation(), getName());
            } else {
                return new RepositoryLocation(getRepository().getName(), new String[] { getName() });
            }
        } catch (MalformedRepositoryLocationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<Action> getCustomActions() {
        return null;
    }

    @Override
    public String getPath() {

        if (containingFolder != null) {
            String parentPath = containingFolder.getPath();
            return parentPath + "/" + getName();
        } else {
            return "/";
        }

    }
}
