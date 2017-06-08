package com.rapidminer.deployment.update.client.listmodels;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.ProgressListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

public abstract class AbstractPackageListModel extends AbstractListModel {
    private static final long serialVersionUID = 1L;
    protected PackageDescriptorCache cache;
    protected boolean updatedOnce = false;
    private boolean forceUpdate = false;
    protected boolean fetching = false;
    protected int completed = 0;
    protected List<String> packageNames = new CopyOnWriteArrayList();
    private String noPackagesMessageKey = "gui.dialog.update.tab.no_packages";

    public AbstractPackageListModel(PackageDescriptorCache cache, String noPackagesMessageKey) {
        this.cache = cache;
        this.noPackagesMessageKey = noPackagesMessageKey;
    }

    public void update(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
        this.update();
    }

    public synchronized void update() {
        if(this.shouldUpdate() || this.forceUpdate) {
            this.fetching = true;
            (new ProgressThread("fetching_updates", false) {
                public void run() {
                    try {
                        this.getProgressListener().setTotal(100);
                        AbstractPackageListModel.this.setCompleted(this.getProgressListener(), 5);
                        AbstractPackageListModel.this.packageNames = AbstractPackageListModel.this.fetchPackageNames();
                        AbstractPackageListModel.this.setCompleted(this.getProgressListener(), 25);
                        int e = 0;
                        Iterator it = AbstractPackageListModel.this.packageNames.iterator();
                        int size = AbstractPackageListModel.this.packageNames.size();

                        while(true) {
                            PackageDescriptor desc;
                            do {
                                if(!it.hasNext()) {
                                    AbstractPackageListModel.this.modifyPackageList();
                                    AbstractPackageListModel.this.updatedOnce = true;
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            AbstractPackageListModel.this.fetching = false;
                                            AbstractPackageListModel.this.fireContentsChanged(this, 0, AbstractPackageListModel.this.packageNames.size() > 0?AbstractPackageListModel.this.packageNames.size():1);
                                        }
                                    });
                                    return;
                                }

                                String packageName = (String)it.next();
                                desc = AbstractPackageListModel.this.cache.getPackageInfo(packageName);
                                AbstractPackageListModel.this.cache.getPackageChanges(packageName);
                                ++e;
                                AbstractPackageListModel.this.setCompleted(this.getProgressListener(), 30 + 70 * e / size);
                            } while(desc != null && (!"STAND_ALONE".equals(desc.getPackageTypeName()) || "rapidminer-studio-6".equals(desc.getPackageId())));

                            it.remove();
                        }
                    } catch (Exception var9) {
                        SwingTools.showVerySimpleErrorMessage("failed_update_server", new Object[]{var9, MarketplaceUpdateManager.getBaseUrl()});
                    } finally {
                        AbstractPackageListModel.this.fetching = false;
                        this.getProgressListener().complete();
                    }

                }
            }).start();
            this.forceUpdate = false;
        }

    }

    protected boolean shouldUpdate() {
        return !this.updatedOnce;
    }

    private void setCompleted(ProgressListener listener, int progress) {
        listener.setCompleted(progress);
        this.completed = progress;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AbstractPackageListModel.this.fireContentsChanged(this, 0, AbstractPackageListModel.this.packageNames.size() > 0?AbstractPackageListModel.this.packageNames.size():1);
            }
        });
    }

    public abstract List<String> handleFetchPackageNames();

    public List<String> fetchPackageNames() {
        return new ArrayList(this.handleFetchPackageNames());
    }

    public void modifyPackageList() {
    }

    public int getSize() {
        return this.fetching?1:(this.packageNames.size() > 0?this.packageNames.size():1);
    }

    public Object getElementAt(int index) {
        return this.fetching?I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.update.tab.loading", new Object[]{Integer.valueOf(this.completed)}):(this.packageNames.size() == 0?I18N.getMessage(I18N.getGUIBundle(), this.noPackagesMessageKey, new Object[0]):this.cache.getPackageInfo((String)this.packageNames.get(index)));
    }

    public List<String> getAllPackageNames() {
        return this.packageNames;
    }

    public PackageDescriptorCache getCache() {
        return this.cache;
    }

    public String getChanges(String packageId) {
        return this.cache.getPackageChanges(packageId);
    }

    public void updateView(PackageDescriptor descr) {
        if(descr != null) {
            int index = this.packageNames.indexOf(descr.getPackageId());
            this.fireContentsChanged(this, index, index);
        }

    }

    public void updateView() {
        this.fireContentsChanged(this, 0, this.packageNames.size() > 0?this.packageNames.size():1);
    }

    public void add(PackageDescriptor desc) {
        this.packageNames.add(desc.getPackageId());
        this.fireIntervalAdded(this, this.packageNames.size() - 1, this.packageNames.size() - 1);
    }
}
