package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.deployment.update.client.UpdateDialog;
import com.rapidminer.deployment.update.client.UpdatePackagesModel;
import com.rapidminer.deployment.update.client.UpdatePanelSearchTab;
import com.rapidminer.deployment.update.client.UpdatePanelTab;
import com.rapidminer.deployment.update.client.UpdatePanelUpdatesTab;
import com.rapidminer.deployment.update.client.UpdateServerAccount;
import com.rapidminer.deployment.update.client.listmodels.AbstractPackageListModel;
import com.rapidminer.deployment.update.client.listmodels.BookmarksPackageListModel;
import com.rapidminer.deployment.update.client.listmodels.LicencedPackageListModel;
import com.rapidminer.deployment.update.client.listmodels.TopDownloadsPackageListModel;
import com.rapidminer.deployment.update.client.listmodels.TopRatedPackageListModel;
import com.rapidminer.gui.tools.ResourceTabbedPane;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tools.NetTools;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class UpdatePanel extends JPanel {
    private final PackageDescriptorCache packageDescriptorCache;
    private final UpdateDialog updateDialog;
    private static final long serialVersionUID = 1L;
    private ResourceTabbedPane updatesTabbedPane = new ResourceTabbedPane("update");
    private UpdateServerAccount usAccount = null;
    private UpdatePackagesModel updateModel;
    private boolean onlyUpdateTab = false;

    public UpdatePanel(UpdateDialog dialog, PackageDescriptorCache packageDescriptorCache, String[] preselectedExtensions, UpdateServerAccount usAccount, UpdatePackagesModel updateModel, boolean onlyUpdateTab) {
        this.packageDescriptorCache = packageDescriptorCache;
        this.usAccount = usAccount;
        this.updateModel = updateModel;
        this.onlyUpdateTab = onlyUpdateTab;
        String[] var7 = preselectedExtensions;
        int var8 = preselectedExtensions.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            String pE = var7[var9];
            PackageDescriptor desc = packageDescriptorCache.getPackageInfo(pE);
            if(desc != null) {
                updateModel.setSelectedForInstallation(desc, true);
            } else {
                SwingTools.showVerySimpleErrorMessage("cannot_install_protocol_extension", new Object[0]);
            }
        }

        this.updateDialog = dialog;
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(800, 320));
        this.setMinimumSize(new Dimension(800, 320));
        if(onlyUpdateTab) {
            this.addUpdateTab();
        } else {
            this.addAllTabs();
        }

        this.add(this.updatesTabbedPane, "Center");
        updateModel.forceNotifyObservers();
        usAccount.forceNotifyObservers();
    }

    private void addAllTabs() {
        this.updatesTabbedPane.addTabI18N("search", this.createSearchListPanel(), new String[0]);
        this.updatesTabbedPane.addTabI18N("updates", this.createUpdatesListPanel(), new String[0]);
        this.updatesTabbedPane.addTabI18N("top_downloads", this.createUpdateListPanel(new TopDownloadsPackageListModel(this.packageDescriptorCache)), new String[0]);
        this.updatesTabbedPane.addTabI18N("top_rated", this.createUpdateListPanel(new TopRatedPackageListModel(this.packageDescriptorCache)), new String[0]);
        this.updatesTabbedPane.addTabI18N("purchased", this.createUpdateListPanel(new LicencedPackageListModel(this.packageDescriptorCache), true), new String[0]);
        this.updatesTabbedPane.addTabI18N("bookmarks", this.createUpdateListPanel(new BookmarksPackageListModel(this.packageDescriptorCache), true), new String[0]);
        this.updatesTabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                UpdatePanelTab currentTab = (UpdatePanelTab)UpdatePanel.this.updatesTabbedPane.getSelectedComponent();
                currentTab.selectNotify();
            }
        });
    }

    private void addUpdateTab() {
        this.updatesTabbedPane.addTabI18N("updates", this.createUpdatesListPanel(), new String[0]);
    }

    private JPanel createUpdateListPanel(AbstractPackageListModel model, boolean updateOnAccountAction) {
        if(updateOnAccountAction) {
            this.usAccount.addObserver(new UpdatePanel.ModelUpdateOberver(model));
        }

        return this.createUpdateListPanel(model);
    }

    private JPanel createUpdateListPanel(AbstractPackageListModel listModel) {
        return new UpdatePanelTab(this.updateModel, listModel, this.usAccount);
    }

    private JPanel createSearchListPanel() {
        return new UpdatePanelSearchTab(this.updateModel, this.packageDescriptorCache, this.usAccount);
    }

    private JPanel createUpdatesListPanel() {
        return new UpdatePanelUpdatesTab(this.updateModel, this.packageDescriptorCache, this.usAccount);
    }

    public void startUpdate() {
        List downloadList = this.updateModel.getInstallationList();
        this.updateDialog.startUpdate(downloadList);
    }

    public void selectUpdatesTab() {
        if(!this.onlyUpdateTab) {
            this.updatesTabbedPane.setSelectedIndex(1);
        }

    }

    static {
        NetTools.init();
    }

    private class ModelUpdateOberver implements Observer {
        private AbstractPackageListModel model;

        ModelUpdateOberver(AbstractPackageListModel model) {
            this.model = model;
        }

        public void update(Observable obs, Object arg) {
            if(obs instanceof UpdateServerAccount) {
                this.model.update(true);
            }

        }
    }
}
