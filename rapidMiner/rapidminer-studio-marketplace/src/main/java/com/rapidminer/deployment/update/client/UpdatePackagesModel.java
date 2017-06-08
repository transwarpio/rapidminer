package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.RapidMiner;
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.deployment.update.client.UpdateServerAccount;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.plugin.Dependency;
import com.rapidminer.tools.plugin.ManagedExtension;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.Map.Entry;

public class UpdatePackagesModel extends Observable {
    private final Map<PackageDescriptor, Boolean> selectionMap = new HashMap();
    private UpdateServerAccount usAccount;
    private Set<String> purchasedPackages = new HashSet();
    private PackageDescriptorCache cache;

    public UpdatePackagesModel(PackageDescriptorCache cache, UpdateServerAccount usAccount) {
        this.cache = cache;
        this.usAccount = usAccount;
    }

    public void updatePurchasedPackages() {
        if(this.usAccount.isLoggedIn()) {
            try {
                this.purchasedPackages = new HashSet(MarketplaceUpdateManager.getAccountService().getLicensedProducts());
                this.setChanged();
                this.notifyObservers();
            } catch (Exception var2) {
                SwingTools.showSimpleErrorMessage("error_accessing_marketplace_account", var2, new Object[]{var2.getMessage()});
                this.purchasedPackages = new HashSet();
            }
        } else {
            this.purchasedPackages = new HashSet();
        }

    }

    public void clearPurchasedPackages() {
        if(!this.usAccount.isLoggedIn()) {
            this.purchasedPackages = new HashSet();
            Iterator var1 = this.selectionMap.entrySet().iterator();

            while(var1.hasNext()) {
                Entry selectionEntry = (Entry)var1.next();
                if(((PackageDescriptor)selectionEntry.getKey()).isRestricted() && ((Boolean)selectionEntry.getValue()).booleanValue()) {
                    this.toggleSelectionForInstallation((PackageDescriptor)selectionEntry.getKey());
                }
            }
        }

    }

    public void setSelectedForInstallation(PackageDescriptor desc, boolean selected) {
        this.selectionMap.put(desc, Boolean.valueOf(true));
    }

    public void forceNotifyObservers() {
        this.setChanged();
        this.notifyObservers();
    }

    public void toggleSelectionForInstallation(PackageDescriptor desc) {
        if(desc != null) {
            boolean select = !this.isSelectedForInstallation(desc);
            if(this.isUpToDate(desc)) {
                select = false;
            }

            if(!"RAPIDMINER_PLUGIN".equals(desc.getPackageTypeName()) && "STAND_ALONE".equals(desc.getPackageTypeName())) {
                String longVersion = RapidMiner.getLongVersion();
                String myVersion = ManagedExtension.normalizeVersion(longVersion);
                String remoteVersion = ManagedExtension.normalizeVersion(desc.getVersion());
                if(myVersion != null && remoteVersion.compareTo(myVersion) <= 0) {
                    select = false;
                }
            }

            if("RIC".equals(desc.getLicenseName()) && !this.isPurchased(desc)) {
                select = false;
                SwingTools.showMessageDialog("purchase_package", new Object[]{desc.getName()});
            }

            this.selectionMap.put(desc, Boolean.valueOf(select));
            this.setChanged();
            this.notifyObservers(desc);
        }

    }

    public void clearFromSelectionMap(List<PackageDescriptor> toClear) {
        Iterator var2 = toClear.iterator();

        while(var2.hasNext()) {
            PackageDescriptor desc = (PackageDescriptor)var2.next();
            this.selectionMap.remove(desc);
            this.setChanged();
            this.notifyObservers(desc);
        }

    }

    public boolean isSelectedForInstallation(PackageDescriptor desc) {
        Boolean selected = (Boolean)this.selectionMap.get(desc);
        return selected != null && selected.booleanValue();
    }

    public List<PackageDescriptor> getInstallationList() {
        LinkedList downloadList = new LinkedList();
        Iterator var2 = this.selectionMap.entrySet().iterator();

        while(var2.hasNext()) {
            Entry entry = (Entry)var2.next();
            if(((Boolean)entry.getValue()).booleanValue()) {
                downloadList.add(entry.getKey());
            }
        }

        return downloadList;
    }

    public boolean isUpToDate(PackageDescriptor desc) {
        ManagedExtension ext = ManagedExtension.get(desc.getPackageId());
        if(ext != null) {
            String remoteVersion = ManagedExtension.normalizeVersion(desc.getVersion());
            String myVersion = ManagedExtension.normalizeVersion(ext.getLatestInstalledVersion());
            return myVersion != null && remoteVersion.compareTo(myVersion) <= 0;
        } else {
            return false;
        }
    }

    public boolean isPurchased(PackageDescriptor desc) {
        return this.purchasedPackages.contains(desc.getPackageId());
    }

    public String getExtensionURL(PackageDescriptor descriptor) {
        return MarketplaceUpdateManager.getBaseUrl() + "/faces/product_details.xhtml?productId=" + descriptor.getPackageId();
    }

    public String toString(PackageDescriptor descriptor, String changes) {
        StringBuilder b = new StringBuilder("<html><body>");
        b.append("<h1>");
        b.append(descriptor.getName());
        if(descriptor.isRestricted()) {
            b.append("&nbsp;<img src=\"icon:///").append("16/currency_euro.png").append("\"/>");
        }

        b.append("</h1><hr/>");
        Date date = new Date(descriptor.getCreationTime().toGregorianCalendar().getTimeInMillis());
        String keyStyle = " style=\"padding-right:5px;color:gray;width:80px;\"";
        b.append("<table style=\"margin:5px 0 10px 5px\"><thead><tr><td " + keyStyle + ">Version</td><td>").append(descriptor.getVersion());
        b.append("</td></tr><tr><td " + keyStyle + ">Release date</td><td>").append(Tools.formatDate(date));
        b.append("</td></tr><tr><td " + keyStyle + ">File size</td><td>").append(Tools.formatBytes((long)descriptor.getSize())).append("</td></tr>");
        b.append("</td></tr><tr><td " + keyStyle + ">License</td><td>").append(descriptor.getLicenseName()).append("</td></tr>");
        if(descriptor.getDependencies() != null && !descriptor.getDependencies().isEmpty()) {
            b.append("<tr><td " + keyStyle + ">Dependencies</td><td>");
            boolean first = true;
            Iterator var7 = Dependency.parse(descriptor.getDependencies()).iterator();

            while(var7.hasNext()) {
                Dependency dependency = (Dependency)var7.next();
                PackageDescriptor packageInfo = this.cache.getPackageInfo(dependency.getPluginExtensionId());
                if(packageInfo != null) {
                    if(!first) {
                        b.append(", ");
                    } else {
                        first = false;
                    }

                    b.append(packageInfo.getName());
                }
            }

            b.append("</td></tr>");
        }

        b.append("</table><div>").append(descriptor.getLongDescription()).append("</div>");
        if(changes != null && !changes.trim().equals("")) {
            b.append("<h2>Changes</h2>");
            b.append(changes);
        }

        b.append("</body></html>");
        return b.toString();
    }
}
