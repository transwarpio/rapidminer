package com.rapidminer.deployment.update.client.listmodels;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.AccountService;
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.deployment.update.client.UpdateServerAccount;
import com.rapidminer.deployment.update.client.listmodels.AbstractPackageListModel;
import com.rapidminer.gui.tools.SwingTools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LicencedPackageListModel extends AbstractPackageListModel {
    private static final long serialVersionUID = 1L;

    public LicencedPackageListModel(PackageDescriptorCache cache) {
        super(cache, "gui.dialog.update.tab.no_licenses");
    }

    public List<String> handleFetchPackageNames() {
        UpdateServerAccount account = MarketplaceUpdateManager.getUpdateServerAccount();
        if(!account.isLoggedIn()) {
            return new ArrayList();
        } else {
            AccountService accountService = null;

            try {
                accountService = MarketplaceUpdateManager.getAccountService();
            } catch (Exception var4) {
                SwingTools.showSimpleErrorMessage("failed_update_server", var4, new Object[]{MarketplaceUpdateManager.getBaseUrl()});
            }

            if (accountService != null) {
                return accountService.getLicensedProducts();
            }
            return Collections.emptyList();
        }
    }
}
