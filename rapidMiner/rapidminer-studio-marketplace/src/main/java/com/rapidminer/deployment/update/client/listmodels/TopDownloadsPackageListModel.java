package com.rapidminer.deployment.update.client.listmodels;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.UpdateService;
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.deployment.update.client.listmodels.AbstractPackageListModel;
import java.util.LinkedList;
import java.util.List;

public class TopDownloadsPackageListModel extends AbstractPackageListModel {
    private static final long serialVersionUID = 1L;

    public TopDownloadsPackageListModel(PackageDescriptorCache cache) {
        super(cache, "gui.dialog.update.tab.no_packages");
    }

    public List<String> handleFetchPackageNames() {
        UpdateService updateService = this.cache.getUpdateService();
        return (List)(updateService != null?updateService.getTopDownloads():new LinkedList());
    }
}
