package com.rapidminer.deployment.update.client.listmodels;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.RapidMiner;
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.deployment.update.client.listmodels.AbstractPackageListModel;
import com.rapidminer.gui.tools.VersionNumber;
import com.rapidminer.tools.plugin.ManagedExtension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UpdatesPackageListModel extends AbstractPackageListModel {
    private static final long serialVersionUID = 1L;

    public UpdatesPackageListModel(PackageDescriptorCache cache) {
        super(cache, "gui.dialog.update.tab.no_updates");
    }

    public List<String> handleFetchPackageNames() {
        ArrayList packageNames = new ArrayList();
        packageNames.add("rapidminer-studio-6");
        Iterator var2 = ManagedExtension.getAll().iterator();

        while(var2.hasNext()) {
            ManagedExtension me = (ManagedExtension)var2.next();
            packageNames.add(me.getPackageId());
        }

        return packageNames;
    }

    public void modifyPackageList() {
        Iterator i = this.packageNames.iterator();

        while(i.hasNext()) {
            String packageName = (String)i.next();
            PackageDescriptor desc = this.cache.getPackageInfo(packageName);
            ManagedExtension ext = ManagedExtension.get(desc.getPackageId());
            if("rapidminer-studio-6".equals(packageName)) {
                if(RapidMiner.getVersion().toString().compareTo(desc.getVersion()) >= 0) {
                    i.remove();
                }
            } else {
                String installedVersionString = ext.getLatestInstalledVersion();
                if(installedVersionString != null) {
                    VersionNumber installed = new VersionNumber(installedVersionString);
                    boolean upToDate = installed.isAtLeast(new VersionNumber(desc.getVersion()));
                    if(upToDate) {
                        i.remove();
                    }
                }
            }
        }

    }
}
