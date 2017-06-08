package com.rapidminer.deployment.update.client.listmodels;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.UpdateService;
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.deployment.update.client.listmodels.AbstractPackageListModel;
import java.util.List;

public class TopRatedPackageListModel extends AbstractPackageListModel {
    private static final long serialVersionUID = 1L;

    public TopRatedPackageListModel(PackageDescriptorCache cache) {
        super(cache, "gui.dialog.update.tab.no_packages");
    }

    public List<String> handleFetchPackageNames() {
        UpdateService updateService = this.cache.getUpdateService();
        return updateService.getTopRated();
    }
}
