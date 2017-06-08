package com.rapidminer.deployment.update.client.listmodels;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.UpdateService;
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.deployment.update.client.listmodels.AbstractPackageListModel;
import com.rapidminer.tools.I18N;
import java.util.Collections;
import java.util.List;

public class SearchPackageListModel extends AbstractPackageListModel {
    private static final long serialVersionUID = 1L;
    private String searchString = "";
    private boolean searched = false;
    private boolean shouldUpdate = false;

    public SearchPackageListModel(PackageDescriptorCache cache) {
        super(cache, "gui.dialog.update.tab.no_search_results");
    }

    public void search(String searchString) {
        if(searchString != null) {
            this.searched = true;
            this.searchString = searchString;
            this.shouldUpdate = true;
            this.update();
            this.shouldUpdate = false;
        }

    }

    public List<String> handleFetchPackageNames() {
        UpdateService updateService = this.cache.getUpdateService();
        if(this.searchString != null && !this.searchString.equals("") && this.searchString.length() > 0) {
            List searchResults = updateService.searchFor(this.searchString);
            return searchResults;
        } else {
            return Collections.emptyList();
        }
    }

    public Object getElementAt(int index) {
        return this.fetching?I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.update.tab.loading", new Object[]{Integer.valueOf(this.completed)}):(this.packageNames.size() == 0?(this.searched?I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.update.tab.no_search_results", new Object[0]):""):this.cache.getPackageInfo((String)this.packageNames.get(index)));
    }

    protected boolean shouldUpdate() {
        return this.shouldUpdate;
    }
}
