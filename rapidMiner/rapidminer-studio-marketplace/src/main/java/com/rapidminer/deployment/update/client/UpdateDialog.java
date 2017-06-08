package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.client.wsimport.UpdateService;
import com.rapidminer.deployment.update.client.ConfirmLicensesDialog;
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.deployment.update.client.UpdatePackagesModel;
import com.rapidminer.deployment.update.client.UpdatePanel;
import com.rapidminer.deployment.update.client.UpdateServerAccount;
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.license.onboarding.OnboardingManager.WelcomeType;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ProgressThreadStoppedException;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.components.LinkButton;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.NetTools;
import com.rapidminer.tools.ProgressListener;
import com.rapidminer.tools.RMUrlHandler;
import com.rapidminer.tools.plugin.Dependency;
import com.rapidminer.tools.plugin.ManagedExtension;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.ws.WebServiceException;

public class UpdateDialog extends ButtonDialog {
    private static final long serialVersionUID = 1L;
    public static final Action UPDATE_ACTION;
    private WindowListener windowListener;
    private final UpdatePanel ulp;
    private static UpdatePackagesModel updateModel;
    private UpdateDialog.USAcountInfoButton accountInfoButton;
    private UpdateDialog.InstallButton installButton;
    private final PackageDescriptorCache packageDescriptorCache;
    private boolean isConfirmed;
    private LinkedList<PackageDescriptor> installablePackageList;
    private JButton closeButton;

    public UpdateDialog(String[] preselectedExtensions) {
        this(false, preselectedExtensions);
    }

    public UpdateDialog(boolean onlyUpdateTab, String[] preselectedExtensions) {
        super(ApplicationFrame.getApplicationFrame(), "update", ModalityType.APPLICATION_MODAL, new Object[0]);
        this.windowListener = new WindowListener() {
            public void windowActivated(WindowEvent e) {
                UpdateServerAccount account = MarketplaceUpdateManager.getUpdateServerAccount();
                account.updatePurchasedPackages(UpdateDialog.updateModel);
            }

            public void windowOpened(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
            }
        };
        this.accountInfoButton = new UpdateDialog.USAcountInfoButton();
        this.packageDescriptorCache = new PackageDescriptorCache();
        this.isConfirmed = false;
        UpdateServerAccount usAccount = MarketplaceUpdateManager.getUpdateServerAccount();
        usAccount.addObserver(this.accountInfoButton);
        updateModel = new UpdatePackagesModel(this.packageDescriptorCache, usAccount);
        this.ulp = new UpdatePanel(this, this.packageDescriptorCache, preselectedExtensions, usAccount, updateModel, onlyUpdateTab);
        this.closeButton = this.makeCloseButton();
        this.layoutDefault(this.ulp, 9, new AbstractButton[]{this.makeOkButton(), this.closeButton});
        this.addWindowListener(this.windowListener);
    }

    protected JButton makeOkButton(final String i18nKey) {
        ResourceAction okAction = new ResourceAction(i18nKey, new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                UpdateDialog.this.wasConfirmed = true;
                UpdateDialog.this.ok();
            }
        };
        this.installButton = new UpdateDialog.InstallButton(okAction);
        this.getRootPane().setDefaultButton(this.installButton);
        this.installButton.setEnabled(false);
        updateModel.addObserver(this.installButton);
        return this.installButton;
    }

    protected JPanel makeButtonPanel(AbstractButton... buttons) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel buttonPanelRight = new JPanel(new FlowLayout(2, 6, 6));
        AbstractButton[] buttonPanelLeft = buttons;
        int var5 = buttons.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            AbstractButton button = buttonPanelLeft[var6];
            if(button != null) {
                buttonPanelRight.add(button);
            }
        }

        buttonPanel.add(buttonPanelRight, "Center");
        JPanel var8 = new JPanel(new FlowLayout(0, 6, 12));
        var8.add(this.accountInfoButton, Boolean.valueOf(false));
        buttonPanel.add(var8, "West");
        return buttonPanel;
    }

    public void installSelectedPackages() {
        this.ulp.startUpdate();
    }

    public static void showUpdateDialog(final boolean selectUpdateTab, final String... preselectedExtensions) {
        boolean fullDialogAllowed = ProductConstraintManager.INSTANCE.isCommunityFeatureAllowed();
        final boolean showOnlyUpdateTab;
        if(!fullDialogAllowed && selectUpdateTab) {
            showOnlyUpdateTab = true;
        } else {
            showOnlyUpdateTab = false;
        }

        if(!fullDialogAllowed && !showOnlyUpdateTab) {
            showOnboardingDialog(new Runnable() {
                public void run() {
                    UpdateDialog.showUpdateDialog(selectUpdateTab, preselectedExtensions);
                }
            });
        } else {
            (new ProgressThread("open_marketplace_dialog", true) {
                public void run() {
                    this.getProgressListener().setTotal(100);
                    this.getProgressListener().setCompleted(33);

                    try {
                        MarketplaceUpdateManager.resetService();
                        MarketplaceUpdateManager.getService();
                    } catch (WebServiceException var16) {
                        SwingTools.showVerySimpleErrorMessage("failed_update_server_simple", new Object[0]);
                        LogService.getRoot().log(Level.WARNING, "com.rapid_i.deployment.update.client.UpdateDialog.could_not_connect", var16);
                        return;
                    } catch (Exception var17) {
                        SwingTools.showSimpleErrorMessage("failed_update_server", var17, new Object[]{MarketplaceUpdateManager.getBaseUrl()});
                        LogService.getRoot().log(Level.WARNING, "com.rapid_i.deployment.update.client.UpdateDialog.could_not_connect", var17);
                        return;
                    }

                    this.getProgressListener().setCompleted(66);
                    LinkedList preselected = null;
                    if(preselectedExtensions != null) {
                        preselected = new LinkedList();
                        PackageDescriptorCache preSelExts = new PackageDescriptorCache();
                        String[] var3 = preselectedExtensions;
                        int var4 = var3.length;

                        for(int var5 = 0; var5 < var4; ++var5) {
                            String preSelExtId = var3[var5];
                            PackageDescriptor desc = preSelExts.getPackageInfo(preSelExtId);
                            if(desc != null) {
                                ManagedExtension ext = ManagedExtension.get(desc.getPackageId());
                                if(ext != null) {
                                    String installed = ext.getLatestInstalledVersion();
                                    if(installed != null) {
                                        boolean upToDate = installed.compareTo(desc.getVersion()) >= 0;
                                        if(upToDate) {
                                            if(preselectedExtensions.length != 1) {
                                                continue;
                                            }

                                            HashSet installSet = new HashSet();
                                            String[] dependencies = preselectedExtensions;
                                            int var13 = dependencies.length;

                                            for(int var14 = 0; var14 < var13; ++var14) {
                                                String install = dependencies[var14];
                                                installSet.add(install);
                                            }

                                            HashSet var19 = UpdateDialog.collectDependency(desc, installSet, preSelExts);
                                            if(var19.isEmpty()) {
                                                SwingTools.showMessageDialog("marketplace.extension_up_to_date", new Object[]{desc.getName()});
                                                this.getProgressListener().complete();
                                                return;
                                            }
                                        }
                                    }
                                }
                            }

                            preselected.add(preSelExtId);
                        }
                    }

                    final String[] var18;
                    if(preselected != null) {
                        var18 = (String[])preselected.toArray(new String[preselected.size()]);
                    } else {
                        var18 = preselectedExtensions;
                    }

                    this.getProgressListener().setCompleted(100);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            final UpdateDialog updateDialog = new UpdateDialog(showOnlyUpdateTab, var18);
                            if(selectUpdateTab && !showOnlyUpdateTab) {
                                updateDialog.showUpdateTab();
                            }

                            if(var18 != null && var18.length > 0) {
                                updateDialog.addWindowListener(new WindowAdapter() {
                                    public void windowOpened(WindowEvent e) {
                                        updateDialog.ok();
                                    }
                                });
                            }

                            updateDialog.setVisible(true);
                        }
                    });
                    this.getProgressListener().complete();
                }
            }).start();
        }

    }

    static void showOnboardingDialog(final Runnable doAfterSuccess) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                OnboardingDialog dialog = new OnboardingDialog(WelcomeType.COMMUNITY_FEATURE);
                dialog.setAfterSuccessAction(doAfterSuccess);
                dialog.showWelcomeCard();
                dialog.setVisible(true);
            }
        });
    }

    private void showUpdateTab() {
        this.ulp.selectUpdatesTab();
    }

    public void startUpdate(final List<PackageDescriptor> downloadList) {
        this.installButton.setEnabled(false);
        (new ProgressThread("resolving_dependencies", true) {
            public void run() {
                try {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            UpdateDialog.this.closeButton.setEnabled(false);
                        }
                    });
                    this.getProgressListener().setTotal(100);
                    final HashMap e = UpdateDialog.resolveDependency(downloadList, UpdateDialog.this.packageDescriptorCache);
                    UpdateDialog.this.installablePackageList = UpdateDialog.getPackagesforInstallation(e);
                    this.getProgressListener().setCompleted(30);
                    if(!UpdateDialog.this.installablePackageList.isEmpty()) {
                        final HashMap licenseNameToLicenseTextMap = UpdateDialog.collectLicenses(UpdateDialog.this.installablePackageList, this.getProgressListener(), 100, 30, 100);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                UpdateDialog.this.isConfirmed = ConfirmLicensesDialog.confirm(UpdateDialog.this, e, licenseNameToLicenseTextMap);
                                (new ProgressThread("installing_updates", true) {
                                    public void run() {
                                        try {
                                            if(UpdateDialog.this.isConfirmed) {
                                                this.getProgressListener().setTotal(100);
                                                this.getProgressListener().setCompleted(20);
                                                UpdateService ex = MarketplaceUpdateManager.getService();
                                                MarketplaceUpdateManager um = new MarketplaceUpdateManager(ex);
                                                final List installedPackages = um.performUpdates(UpdateDialog.this.installablePackageList, this.getProgressListener());
                                                UpdateDialog.updateModel.clearFromSelectionMap(installedPackages);
                                                UpdateDialog.this.ulp.validate();
                                                UpdateDialog.this.ulp.repaint();
                                                (new Thread(new Runnable() {
                                                    public void run() {
                                                        if(installedPackages.size() > 0) {
                                                            int confirmation = SwingTools.showConfirmDialog(installedPackages.size() == 1?"update.complete_restart":"update.complete_restart1", 0, new Object[]{Integer.valueOf(installedPackages.size())});
                                                            if(confirmation == 0) {
                                                                RapidMinerGUI.getMainFrame().exit(true);
                                                            } else if(confirmation == 1 && installedPackages.size() == UpdateDialog.this.installablePackageList.size()) {
                                                                UpdateDialog.this.dispose();
                                                            }
                                                        }

                                                    }
                                                })).start();
                                            }
                                        } catch (ProgressThreadStoppedException var8) {
                                            ;
                                        } catch (Exception var9) {
                                            SwingTools.showSimpleErrorMessage("error_installing_update", var9, new Object[]{var9.getMessage()});
                                        } finally {
                                            this.getProgressListener().complete();
                                            UpdateDialog.this.installButton.setEnabled(true);
                                        }

                                    }
                                }).start();
                            }
                        });
                    }

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            UpdateDialog.this.closeButton.setEnabled(true);
                        }
                    });
                } catch (Exception var6) {
                    SwingTools.showSimpleErrorMessage("error_resolving_dependencies", var6, new Object[]{var6.getMessage()});
                } finally {
                    this.getProgressListener().complete();
                    if(UpdateDialog.this.installablePackageList != null && !UpdateDialog.this.installablePackageList.isEmpty()) {
                        UpdateDialog.this.installButton.setEnabled(true);
                    }

                }

            }
        }).start();
    }

    protected static HashMap<String, String> collectLicenses(LinkedList<PackageDescriptor> installablePackageList, ProgressListener progressListener, int total, int start, int end) throws MalformedURLException, URISyntaxException {
        int range = end - start;
        double perIterationPercent = (double)range * 1.0D / (double)installablePackageList.size();
        int iterCnt = 1;
        HashMap licenseNameToLicenseTextMap = null;
        licenseNameToLicenseTextMap = new HashMap();
        UpdateService service = MarketplaceUpdateManager.getService();

        for(Iterator var11 = installablePackageList.iterator(); var11.hasNext(); ++iterCnt) {
            PackageDescriptor packageDescriptor = (PackageDescriptor)var11.next();
            String licenseName = packageDescriptor.getLicenseName();
            String licenseText = service.getLicenseTextHtml(licenseName);
            licenseNameToLicenseTextMap.put(licenseName, licenseText);
            progressListener.setCompleted((int)((double)start + (double)iterCnt * perIterationPercent));
        }

        return licenseNameToLicenseTextMap;
    }

    protected void ok() {
        this.ulp.startUpdate();
    }

    public static LinkedList<PackageDescriptor> getPackagesforInstallation(HashMap<PackageDescriptor, HashSet<PackageDescriptor>> dependency) {
        HashSet installabledPackages = new HashSet();
        Iterator installablePackageList = dependency.keySet().iterator();

        while(installablePackageList.hasNext()) {
            PackageDescriptor packageDescriptor = (PackageDescriptor)installablePackageList.next();
            installabledPackages.add(packageDescriptor);
            installabledPackages.addAll((Collection)dependency.get(packageDescriptor));
        }

        LinkedList installablePackageList1 = new LinkedList();
        installablePackageList1.addAll(installabledPackages);
        return installablePackageList1;
    }

    private static HashSet<Dependency> collectDependency(PackageDescriptor desc, HashSet<String> pluginsSelectedForDownload, PackageDescriptorCache packageDescriptorCache) {
        HashSet dependencySet = new HashSet();
        List dependencies = Dependency.parse(desc.getDependencies());
        Iterator var5 = dependencies.iterator();

        while(var5.hasNext()) {
            Dependency dependency = (Dependency)var5.next();
            String packageId = dependency.getPluginExtensionId();
            PackageDescriptor packageInfo = packageDescriptorCache.getPackageInfo(packageId);
            if(packageInfo != null) {
                boolean upToDate = false;
                ManagedExtension ext = ManagedExtension.get(packageId);
                if(ext != null) {
                    String installed = ext.getLatestInstalledVersion();
                    if(installed != null) {
                        upToDate = installed.compareTo(packageInfo.getVersion()) >= 0;
                    }
                }

                if(!dependencySet.contains(dependency) && !pluginsSelectedForDownload.contains(packageId) && !upToDate) {
                    dependencySet.add(dependency);
                    dependencySet.addAll(collectDependency(packageInfo, pluginsSelectedForDownload, packageDescriptorCache));
                }
            }
        }

        return dependencySet;
    }

    public static HashMap<PackageDescriptor, HashSet<PackageDescriptor>> resolveDependency(List<PackageDescriptor> downloadList, PackageDescriptorCache packageDescriptorCache) {
        HashMap dependentPackageMap = new HashMap();
        HashSet pluginsSelectedForDownload = new HashSet();
        Iterator var4 = downloadList.iterator();

        PackageDescriptor desc;
        while(var4.hasNext()) {
            desc = (PackageDescriptor)var4.next();
            pluginsSelectedForDownload.add(desc.getPackageId());
            dependentPackageMap.put(desc, new HashSet());
        }

        var4 = downloadList.iterator();

        while(var4.hasNext()) {
            desc = (PackageDescriptor)var4.next();
            HashSet dependencySet = collectDependency(desc, pluginsSelectedForDownload, packageDescriptorCache);
            Iterator var7 = dependencySet.iterator();

            while(var7.hasNext()) {
                Dependency dependency = (Dependency)var7.next();
                ((HashSet)dependentPackageMap.get(desc)).add(packageDescriptorCache.getPackageInfo(dependency.getPluginExtensionId()));
            }
        }

        return dependentPackageMap;
    }

    static {
        NetTools.init();
        UPDATE_ACTION = new ResourceAction("update_manager", new Object[0]) {
            private static final long serialVersionUID = 1L;

            {
                this.setCondition(9, 0);
            }

            public void actionPerformed(ActionEvent arg0) {
                UpdateDialog.showUpdateDialog(false, new String[0]);
            }
        };
    }

    private class InstallButton extends JButton implements Observer {
        private static final long serialVersionUID = 1L;

        InstallButton(Action a) {
            super(a);
            this.updateButton();
        }

        public void update(Observable o, Object arg) {
            if(o instanceof UpdatePackagesModel) {
                this.updateButton();
            }

        }

        private void updateButton() {
            UpdatePackagesModel currentModel = UpdateDialog.updateModel;
            if(currentModel.getInstallationList() != null && currentModel.getInstallationList().size() > 0) {
                this.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.update.install.label", new Object[]{Integer.valueOf(currentModel.getInstallationList().size())}));
                this.setEnabled(true);
            } else {
                this.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.update.install.label", new Object[]{Integer.valueOf(0)}));
                this.setEnabled(false);
            }

        }
    }

    private static class USAcountInfoButton extends LinkButton implements Observer {
        private static final long serialVersionUID = 1L;

        public USAcountInfoButton() {
            super(new AbstractAction("") {
                private static final long serialVersionUID = 1L;

                public void actionPerformed(ActionEvent e) {
                    UpdateServerAccount account = MarketplaceUpdateManager.getUpdateServerAccount();
                    if("#register".equals(e.getActionCommand())) {
                        try {
                            RMUrlHandler.browse(new URI(MarketplaceUpdateManager.getBaseUrl() + "/faces/signup.xhtml"));
                        } catch (Exception var4) {
                            SwingTools.showSimpleErrorMessage("cannot_open_browser", var4, new Object[0]);
                        }
                    } else if(account.isLoggedIn()) {
                        account.logout(UpdateDialog.updateModel);
                    } else {
                        account.login(UpdateDialog.updateModel);
                    }

                }
            });
            Dimension size = new Dimension(300, 24);
            this.setSize(size);
            this.setMaximumSize(size);
            this.setPreferredSize(size);
        }

        public void update(Observable obs, Object arg) {
            if(obs instanceof UpdateServerAccount) {
                UpdateServerAccount account = (UpdateServerAccount)obs;
                if(account.isLoggedIn()) {
                    this.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.update.account_button.logged_in", new Object[]{account.getUserName()}));
                } else {
                    this.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.update.account_button.logged_out", new Object[0]));
                }
            }

        }
    }
}
