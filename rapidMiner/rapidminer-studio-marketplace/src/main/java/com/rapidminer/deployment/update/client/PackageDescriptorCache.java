package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.RapidMiner;
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.client.wsimport.UpdateService;
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.plugin.Dependency;
import com.rapidminer.tools.plugin.ManagedExtension;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PackageDescriptorCache {
    private final Map<String, PackageDescriptor> descriptors = new HashMap();
    private final Map<String, String> packageChanges = new HashMap();
    private UpdateService updateService = null;

    public PackageDescriptorCache() {
    }

    public PackageDescriptor getPackageInfo(String packageId) {
        if("rapidminer-studio-6".equals(packageId)) {
            packageId = MarketplaceUpdateManager.getRMPackageId();
        }

        if(this.descriptors.containsKey(packageId)) {
            return (PackageDescriptor)this.descriptors.get(packageId);
        } else {
            this.fetchPackageInfo(packageId);
            return this.descriptors.containsKey(packageId)?(PackageDescriptor)this.descriptors.get(packageId):null;
        }
    }

    public String getPackageChanges(String packageId) {
        if(this.packageChanges.containsKey(packageId)) {
            return (String)this.packageChanges.get(packageId);
        } else {
            try {
                ManagedExtension e = ManagedExtension.get(packageId);
                String installedVersion = e != null?e.getLatestInstalledVersion():null;
                PackageDescriptor packageDescriptior = (PackageDescriptor)this.descriptors.get(packageId);
                String fromVersion = "";
                String toVersion = "";
                if(installedVersion != null) {
                    fromVersion = "?baseVersion=" + installedVersion;
                }

                if(packageDescriptior != null && packageDescriptior.getVersion() != null) {
                    if(installedVersion == null) {
                        toVersion = "?";
                    } else {
                        toVersion = "&";
                    }

                    toVersion = toVersion + "toVersion=" + packageDescriptior.getVersion();
                }

                URI changesURI = MarketplaceUpdateManager.getUpdateServerURI("/download/changes/" + packageId + fromVersion + toVersion);
                String changes = Tools.readTextFile(changesURI.toURL().openStream());
                this.packageChanges.put(packageId, changes);
                return changes;
            } catch (Exception var9) {
                this.packageChanges.put(packageId, null);
                return null;
            }
        }
    }

    private void fetchPackageInfo(String packageId) {
        this.initUpdateService();
        if(this.updateService != null) {
            try {
                String e = MarketplaceUpdateManager.TARGET_PLATFORM.toString();
                if(!"rapidminer-studio-6".equals(packageId)) {
                    e = "ANY";
                }

                PackageDescriptor descriptor = this.updateService.getPackageInfo(packageId, this.updateService.getLatestVersion(packageId, e, RapidMiner.getLongVersion()), e);
                this.descriptors.put(packageId, descriptor);
                if(descriptor != null && descriptor.getDependencies() != null) {
                    List dependencies = Dependency.parse(descriptor.getDependencies());
                    Iterator var5 = dependencies.iterator();

                    while(var5.hasNext()) {
                        Dependency dependency = (Dependency)var5.next();
                        this.getPackageInfo(dependency.getPluginExtensionId());
                    }
                }
            } catch (Exception var7) {
                SwingTools.showVerySimpleErrorMessage("failed_update_server", new Object[]{var7, MarketplaceUpdateManager.getBaseUrl()});
            }
        }

    }

    private void initUpdateService() {
        if(this.updateService == null) {
            try {
                this.updateService = MarketplaceUpdateManager.getService();
            } catch (Exception var2) {
                SwingTools.showVerySimpleErrorMessage("failed_update_server", new Object[]{var2, MarketplaceUpdateManager.getBaseUrl()});
            }
        }

    }

    public UpdateService getUpdateService() {
        this.initUpdateService();
        return this.updateService;
    }
}
