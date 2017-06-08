package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.deployment.update.client.UpdatePackagesModel;
import com.rapidminer.deployment.update.client.UpdatePanelTab;
import com.rapidminer.deployment.update.client.UpdateServerAccount;
import com.rapidminer.deployment.update.client.listmodels.AbstractPackageListModel;
import com.rapidminer.deployment.update.client.listmodels.UpdatesPackageListModel;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tools.I18N;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class UpdatePanelUpdatesTab extends UpdatePanelTab {
    private static final long serialVersionUID = 1L;
    private JButton updateAllButton;
    public final Action selectAllAction;

    public UpdatePanelUpdatesTab(UpdatePackagesModel updateModel, PackageDescriptorCache packageDescriptorCache, UpdateServerAccount usAccount) {
        this(updateModel, (AbstractPackageListModel)(new UpdatesPackageListModel(packageDescriptorCache)), usAccount);
    }

    private UpdatePanelUpdatesTab(UpdatePackagesModel updateModel, AbstractPackageListModel model, UpdateServerAccount usAccount) {
        super(updateModel, model, usAccount);
        this.selectAllAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                boolean containsRestricted = false;
                Iterator var3 = UpdatePanelUpdatesTab.this.listModel.getAllPackageNames().iterator();

                while(var3.hasNext()) {
                    String packageName = (String)var3.next();
                    PackageDescriptor pd = UpdatePanelUpdatesTab.this.listModel.getCache().getPackageInfo(packageName);
                    if(!UpdatePanelUpdatesTab.this.updateModel.isSelectedForInstallation(pd)) {
                        containsRestricted |= pd.isRestricted();
                    }
                }

                if(containsRestricted && !UpdatePanelUpdatesTab.this.usAccount.isLoggedIn()) {
                    UpdatePanelUpdatesTab.this.usAccount.login(UpdatePanelUpdatesTab.this.updateModel, false, new Runnable() {
                        public void run() {
                            markAllEligible();
                        }
                    }, new Runnable() {
                        public void run() {
                            markAllEligible();
                        }
                    });
                } else {
                    this.markAllEligible();
                }

            }

            private void markAllEligible() {
                Iterator var1 = UpdatePanelUpdatesTab.this.listModel.getAllPackageNames().iterator();

                while(var1.hasNext()) {
                    String packageName = (String)var1.next();
                    PackageDescriptor pd = UpdatePanelUpdatesTab.this.listModel.getCache().getPackageInfo(packageName);
                    boolean osxStudioUpdate = UpdatePanelUpdatesTab.this.isOSXStudioUpdate(pd);
                    if(!UpdatePanelUpdatesTab.this.updateModel.isSelectedForInstallation(pd) && !osxStudioUpdate) {
                        UpdatePanelUpdatesTab.this.markForInstallation(pd, false, false);
                    }
                }

                UpdatePanelUpdatesTab.this.getModel().updateView();
                UpdatePanelUpdatesTab.this.checkInstallAllEnabled();
            }
        };
    }

    private void checkInstallAllEnabled() {
        boolean allSelected = true;
        LinkedList allPackageNames = new LinkedList(this.listModel.getAllPackageNames());

        PackageDescriptor pd;
        for(Iterator var3 = allPackageNames.iterator(); var3.hasNext(); allSelected &= this.updateModel.isSelectedForInstallation(pd) || this.isOSXStudioUpdate(pd)) {
            String packageName = (String)var3.next();
            pd = this.listModel.getCache().getPackageInfo(packageName);
        }

        this.updateAllButton.setEnabled(!allSelected);
    }

    private boolean isOSXStudioUpdate(PackageDescriptor pd) {
        return pd != null && MarketplaceUpdateManager.useOSXUpdateMechansim() && pd.getPackageId().equals("rapidminer-studio-6");
    }

    protected JComponent makeBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(0));
        panel.setMinimumSize(new Dimension(100, 35));
        panel.setPreferredSize(new Dimension(100, 35));
        this.updateAllButton = new JButton(I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.update.tab.updates.update_all_button", new Object[0]));
        this.updateAllButton.setIcon(SwingTools.createIcon("16/checks.png"));
        this.updateAllButton.setEnabled(false);
        this.listModel.addListDataListener(new ListDataListener() {
            public void intervalAdded(ListDataEvent e) {
            }

            public void intervalRemoved(ListDataEvent e) {
            }

            public void contentsChanged(ListDataEvent e) {
                UpdatePanelUpdatesTab.this.checkInstallAllEnabled();
            }
        });
        this.updateAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UpdatePanelUpdatesTab.this.selectAllAction.actionPerformed((ActionEvent)null);
            }
        });
        panel.add(this.updateAllButton);
        return panel;
    }
}