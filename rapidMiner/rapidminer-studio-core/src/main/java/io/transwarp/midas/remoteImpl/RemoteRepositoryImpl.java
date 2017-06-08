package io.transwarp.midas.remoteImpl;

import com.rapidminer.RapidMiner;
import com.rapidminer.repository.*;
import com.rapidminer.repository.gui.RepositoryConfigurationPanel;
import com.rapidminer.repository.internal.remote.*;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.PasswordInputCanceledException;
import io.transwarp.midas.client.MidasClient;
import io.transwarp.midas.client.MidasClientFactory;
import io.transwarp.midas.thrift.message.FileEntry;
import io.transwarp.midas.thrift.message.FolderEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RemoteRepositoryImpl extends RemoteFolderImpl implements RemoteRepository{


    private List<RepositoryListener> listeners = new ArrayList<>();

    public RemoteRepositoryImpl() {
        super("Share", null , null);
    }

    @Override
    public List<Folder> getSubfolders() throws RepositoryException {
        if (super.getSubfolders().isEmpty()) {
            load();
        }
        return super.getSubfolders();
    }

    @Override
    public void addRepositoryListener(RepositoryListener l) {
        listeners.add(l);
    }

    @Override
    public void removeRepositoryListener(RepositoryListener l) {
        listeners.remove(l);
    }

    @Override
    public RepositoryLocation getLocation() {
        try {
            return new RepositoryLocation(getName(), new String[0]);
        } catch (MalformedRepositoryLocationException e) {
            throw new RuntimeException(e);
        }
    }

    protected void fireEntryRenamed(final Entry entry) {
        for (RepositoryListener l : listeners) {
            l.entryChanged(entry);
        }

    }

    protected void fireEntryAdded(final Entry newEntry, final Folder parent) {
        for (RepositoryListener l : listeners) {
            l.entryAdded(newEntry, parent);
        }
    }

    public void fireRefreshed(final Folder folder) {
        for (RepositoryListener l : listeners) {
            l.folderRefreshed(folder);
        }
    }

    protected void fireEntryRemoved(final Entry removedEntry, final Folder parent, final int index) {
        for (RepositoryListener l : listeners) {
            l.entryRemoved(removedEntry, parent, index);
        }
    }

    @Override
    public boolean rename(String newName) throws RepositoryException {
        checkRename(getContainingFolder(), newName);
        setName(newName);
        ((RemoteRepositoryImpl)getRepository()).fireEntryRenamed(this);
        return true;
    }

    @Override
    public Entry locate(String entry) throws RepositoryException {
        return RepositoryManager.getInstance(null).locate(this, entry, false);
    }

    @Override
    public RemoteRepository getRepository() {
        return this;
    }

    @Override
    public String getState() {
        return null;
    }

    @Override
    public String getIconName() {
        return "share.png";
    }

    @Override
    public Element createXML(Document doc) {
        return null;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public void postInstall() {

    }

    @Override
    public void preRemove() {

    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public RepositoryConfigurationPanel makeConfigurationPanel() {
        return null;
    }

    @Override
    public URL getBaseUrl() {
        return null;
    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public void setUsername(String username) {

    }

    @Override
    public void setPassword(char[] password) {

    }

    @Override
    public void setPasswortInputCanceled(boolean pwCanceled) {

    }

    @Override
    public boolean isPasswordInputCanceled() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isReachable() throws RepositoryException {
        return false;
    }

    @Override
    public RemoteInfoService getInfoService() {
        return null;
    }

    @Override
    public RemoteScheduler getScheduler() throws RepositoryException, PasswordInputCanceledException {
        return null;
    }

    @Override
    public RemoteContentManager getContentManager() throws RepositoryException, PasswordInputCanceledException {
        return null;
    }

    @Override
    public void resetContentManager() throws RepositoryException, PasswordInputCanceledException {

    }

    @Override
    public HttpURLConnection getHTTPConnection(String pathInfo, boolean preAuthHeader) throws IOException, RepositoryException {
        String host = ParameterService.getParameterValue(RapidMiner.MIDAS_HOST);
        String port = ParameterService.getParameterValue(RapidMiner.MIDAS_PORT);
        return (HttpURLConnection) new URL("http://" + host + ":" + port)
                .openConnection();
    }

    @Override
    public HttpURLConnection getHTTPConnection(String pathInfo, String query, boolean preAuthHeader) throws IOException, RepositoryException {
        String host = ParameterService.getParameterValue(RapidMiner.MIDAS_HOST);
        String port = ParameterService.getParameterValue(RapidMiner.MIDAS_PORT);
        return (HttpURLConnection) new URL("http://" + host + ":" + port)
                .openConnection();
    }

    @Override
    public List<String> getTypeIds() {
        return null;
    }

    @Override
    public void setTypeIds(List<String> typeIds) throws RepositoryException {

    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
    }

    @Override
    public void removeConnectionListener(ConnectionListener listener) {

    }

    private RemoteFolder genFolder(FolderEntry f, RemoteFolder parent) {
        RemoteFolderImpl folder = new RemoteFolderImpl(f.name, parent, this);
        List<Folder> subFolders = new ArrayList<>();
        for (FolderEntry e: f.getFolders()) {
            subFolders.add(genFolder(e, folder));
        }
        folder.setSubfolders(subFolders);
        List<DataEntry> files = new ArrayList<>();
        for (FileEntry e: f.getFiles()) {
            files.add(genFile(e, folder));
        }
        folder.setDataEntries(files);
        return folder;
    }

    private RemoteDataEntry genFile(FileEntry f, RemoteFolder parent) {
        return new RemoteProcessImpl(f.name, parent, this);
    }

    private void load() {
        MidasClient client = MidasClientFactory.getClientInstance();
        io.transwarp.midas.thrift.message.Repository r = client.getRepository();
        List<Folder> subFolders = new ArrayList<>();
        for (FolderEntry e: r.getFolders()) {
            subFolders.add(genFolder(e, this));
        }
        this.setSubfolders(subFolders);
    }

    @Override
    public void refresh() {
        load();
        fireRefreshed(this);
    }
}
