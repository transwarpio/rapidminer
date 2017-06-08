package io.transwarp.midas.remoteImpl;

import com.rapid_i.repository.wsimport.AccessRights;
import com.rapidminer.operator.Operator;
import com.rapidminer.repository.*;
import com.rapidminer.repository.internal.remote.RemoteDataEntry;
import com.rapidminer.repository.internal.remote.RemoteFolder;
import com.rapidminer.repository.internal.remote.RemoteProcessEntry;
import com.rapidminer.repository.internal.remote.RemoteRepository;
import com.rapidminer.tools.PasswordInputCanceledException;
import io.transwarp.midas.client.MidasClient;
import io.transwarp.midas.client.MidasClientFactory;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

public class RemoteProcessImpl extends  RemoteEntryImpl implements RemoteProcessEntry, RemoteDataEntry {

    RemoteProcessImpl(String name, RemoteFolder containingFolder, RemoteRepository repository) {
        super(name, containingFolder, repository);
    }

    @Override
    public int getRevision() {
        return 0;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public long getDate() {
        return 0;
    }

    @Override
    public String retrieveXML() throws RepositoryException {
        MidasClient client = MidasClientFactory.getClientInstance();
        return new String(client.getSharing(getPath()));
    }

    @Override
    public void storeXML(String xml) throws RepositoryException {

    }

    @Override
    public void storeMidasXMLAndJson(Operator operator) throws RepositoryException {

    }

    @Override
    public String getType() {
        return ProcessEntry.TYPE_NAME;
    }

    @Override
    public String getOwner() {
        return "";
    }

    @Override
    public String getDescription() {
        return "remote process";
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean rename(String newName) throws RepositoryException {
        return false;
    }

    @Override
    public boolean willBlock() {
        return false;
    }

    @Override
    public Collection<Action> getCustomActions() {
        return null;
    }

    @Override
    public List<AccessRights> getAccessRights() throws RepositoryException, PasswordInputCanceledException {
        return null;
    }

    @Override
    public void setAccessRights(List<AccessRights> accessRights) throws RepositoryException, PasswordInputCanceledException {

    }
}
