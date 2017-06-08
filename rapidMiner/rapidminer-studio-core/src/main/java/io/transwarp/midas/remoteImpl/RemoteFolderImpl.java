package io.transwarp.midas.remoteImpl;

import com.rapid_i.repository.wsimport.AccessRights;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.Operator;
import com.rapidminer.repository.*;
import com.rapidminer.repository.internal.remote.RemoteFolder;
import com.rapidminer.repository.internal.remote.RemoteRepository;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.PasswordInputCanceledException;
import com.rapidminer.tools.ProgressListener;
import io.transwarp.midas.client.MidasClient;
import io.transwarp.midas.client.MidasClientFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

public class RemoteFolderImpl extends RemoteEntryImpl implements RemoteFolder{


    private List<DataEntry> data = new ArrayList<>();
    private List<Folder> folders = new ArrayList<>();

    private final ReentrantLock refreshLock = new ReentrantLock();

    RemoteFolderImpl(String name, RemoteFolder containingFolder, RemoteRepository repository) {
        super(name, containingFolder, repository);
    }

    @Override
    public List<DataEntry> getDataEntries() throws RepositoryException {
        return data;
    }

    @Override
    public List<Folder> getSubfolders() throws RepositoryException {
        return folders;
    }

    public void setDataEntries(List<DataEntry> data) {
        this.data = data;
    }

    public void setSubfolders(List<Folder> folders) {
        this.folders = folders;
    }

    @Override
    public void refresh() {
        refreshLock.lock();
        try {
            data = null;
            folders = null;
            RemoteRepositoryImpl repo = ((RemoteRepositoryImpl)getRepository());
            repo.refresh();
            repo.fireRefreshed(this);
        } finally {
            refreshLock.unlock();
        }
    }

    @Override
    public boolean containsEntry(String name) throws RepositoryException {
        for (Folder folder : folders) {
            if (folder.getName().equals(name)) {
                return true;
            }
        }
        for (DataEntry entry : data) {
            if (entry.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Folder createFolder(String name) throws RepositoryException {
        if (!RepositoryLocation.isNameValid(name)) {
            throw new RepositoryException(I18N.getMessage(I18N.getErrorBundle(), "repository.illegal_entry_name", name,
                    getLocation()));
        }

        for (Folder folder : folders) {
            // folder with the same name (no matter if they have different capitalization) must not
            // be created
            if (folder.getName().toLowerCase(Locale.ENGLISH).equals(name.toLowerCase(Locale.ENGLISH))) {
                throw new RepositoryException(I18N.getMessage(I18N.getErrorBundle(),
                        "repository.repository_folder_already_exists", name));
            }
        }
        for (DataEntry entry : data) {
            if (entry.getName().equals(name)) {
                throw new RepositoryException(I18N.getMessage(I18N.getErrorBundle(),
                        "repository.repository_entry_with_same_name_already_exists", name));
            }
        }

        MidasClient client = MidasClientFactory.getClientInstance();
        RemoteFolder folder = new RemoteFolderImpl(name, this, getRepository());
        client.mkdir(folder.getPath());

        folders.add(folder);
        ((RemoteRepositoryImpl)getRepository()).fireEntryAdded(folder, this);
        return folder;
    }

    @Override
    public IOObjectEntry createIOObjectEntry(String name, IOObject ioobject, Operator callingOperator, ProgressListener progressListener) throws RepositoryException {
        return null;
    }

    @Override
    public ProcessEntry createProcessEntry(String name, String processXML) throws RepositoryException {
        MidasClient client = MidasClientFactory.getClientInstance();
        RemoteProcessImpl processEntry = new RemoteProcessImpl(name, this, getRepository());
        try {
            client.addSharing(processXML.getBytes("UTF-8"), processEntry.getPath());
        } catch (UnsupportedEncodingException e) {
            throw new RepositoryException(e);
        }
        data.add(processEntry);
        ((RemoteRepositoryImpl)getRepository()).fireEntryAdded(processEntry, this);
        return processEntry;
    }

    @Override
    public BlobEntry createBlobEntry(String name) throws RepositoryException {
        return null;
    }

    @Override
    public boolean canRefreshChild(String childName) throws RepositoryException {
        return false;
    }

    @Override
    public String getType() {
        return Folder.TYPE_NAME;
    }

    @Override
    public String getOwner() {
        return null;
    }

    @Override
    public String getDescription() {
        return "remote folder";
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean willBlock() {
        return false;
    }

    @Override
    public List<AccessRights> getAccessRights() throws RepositoryException, PasswordInputCanceledException {
        return null;
    }

    @Override
    public void setAccessRights(List<AccessRights> accessRights) throws RepositoryException, PasswordInputCanceledException {

    }
}
