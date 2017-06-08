package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.client.wsimport.UpdateService;
import com.rapidminer.deployment.update.client.ConfirmLicensesDialog;
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.deployment.update.client.PackageDescriptorCache;
import com.rapidminer.deployment.update.client.UpdateDialog;
import com.rapidminer.deployment.update.client.UpdateListCellRenderer;
import com.rapidminer.deployment.update.client.listmodels.AbstractPackageListModel;
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.io.process.XMLTools;
import com.rapidminer.tools.FileSystemService;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.XMLException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PendingPurchasesInstallationDialog extends ButtonDialog {
    private static final long serialVersionUID = 1L;
    private final PackageDescriptorCache packageDescriptorCache = new PackageDescriptorCache();
    private AbstractPackageListModel purchasedModel;
    JCheckBox neverAskAgain;
    private final List<String> packages;
    private boolean isConfirmed;
    private LinkedList<PackageDescriptor> installablePackageList;
    private JButton remindNeverButton;
    private JButton remindLaterButton;
    private JButton okButton;

    public PendingPurchasesInstallationDialog(List<String> packages) {
        super(ApplicationFrame.getApplicationFrame(), "purchased_not_installed", ModalityType.MODELESS, new Object[0]);
        this.purchasedModel = new PendingPurchasesInstallationDialog.PurchasedNotInstalledModel(this.packageDescriptorCache);
        this.neverAskAgain = new JCheckBox(I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.purchased_not_installed.not_check_on_startup", new Object[0]));
        this.packages = packages;
        this.remindNeverButton = this.remindNeverButton();
        this.remindLaterButton = this.remindLaterButton();
        this.okButton = this.makeOkButton("install_purchased");
        this.layoutDefault(this.makeContentPanel(), 1, new AbstractButton[]{this.okButton, this.remindNeverButton, this.remindLaterButton});
        this.setPreferredSize(new Dimension(404, 430));
        this.setMaximumSize(new Dimension(404, 430));
        this.setMinimumSize(new Dimension(404, 300));
        this.setSize(new Dimension(404, 430));
    }

    private JPanel makeContentPanel() {
        BorderLayout layout = new BorderLayout(12, 12);
        JPanel panel = new JPanel(layout);
        panel.setBorder(new EmptyBorder(0, 12, 8, 12));
        panel.add(this.createExtensionListScrollPane(this.purchasedModel), "Center");
        this.purchasedModel.update();
        JPanel southPanel = new JPanel(new BorderLayout(0, 7));
        JLabel question = new JLabel(I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.purchased_not_installed.should_install", new Object[0]));
        southPanel.add(question, "Center");
        southPanel.add(this.neverAskAgain, "South");
        panel.add(southPanel, "South");
        return panel;
    }

    private JScrollPane createExtensionListScrollPane(AbstractPackageListModel model) {
        JList updateList = new JList(model);
        updateList.setCellRenderer(new UpdateListCellRenderer(true));
        ExtendedJScrollPane extensionListScrollPane = new ExtendedJScrollPane(updateList);
        extensionListScrollPane.setBorder(BorderFactory.createEtchedBorder(1));
        return extensionListScrollPane;
    }

    private JButton remindLaterButton() {
        ResourceAction Action = new ResourceAction("ask_later", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                PendingPurchasesInstallationDialog.this.wasConfirmed = false;
                PendingPurchasesInstallationDialog.this.checkNeverAskAgain();
                PendingPurchasesInstallationDialog.this.close();
            }
        };
        this.getRootPane().getInputMap(2).put(KeyStroke.getKeyStroke(27, 0, false), "CLOSE");
        this.getRootPane().getActionMap().put("CLOSE", Action);
        JButton button = new JButton(Action);
        this.getRootPane().setDefaultButton(button);
        return button;
    }

    private JButton remindNeverButton() {
        ResourceAction Action = new ResourceAction("ask_never", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                PendingPurchasesInstallationDialog.this.wasConfirmed = false;
                PendingPurchasesInstallationDialog.this.checkNeverAskAgain();
                PendingPurchasesInstallationDialog.this.neverRemindAgain();
                PendingPurchasesInstallationDialog.this.close();
            }
        };
        JButton button = new JButton(Action);
        this.getRootPane().setDefaultButton(button);
        return button;
    }

    protected void ok() {
        this.checkNeverAskAgain();
        this.startUpdate(this.getPackageDescriptorList());
        this.dispose();
    }

    public List<PackageDescriptor> getPackageDescriptorList() {
        ArrayList packageList = new ArrayList();

        for(int a = 0; a < this.purchasedModel.getSize(); ++a) {
            Object listItem = this.purchasedModel.getElementAt(a);
            if(listItem instanceof PackageDescriptor) {
                packageList.add((PackageDescriptor)listItem);
            }
        }

        return packageList;
    }

    public void startUpdate(final List<PackageDescriptor> downloadList) {
        try {
            MarketplaceUpdateManager.getService();
        } catch (Exception var3) {
            SwingTools.showSimpleErrorMessage("failed_update_server", var3, new Object[]{MarketplaceUpdateManager.getBaseUrl()});
            return;
        }

        (new ProgressThread("resolving_dependencies", true) {
            public void run() {
                try {
                    this.getProgressListener().setTotal(100);
                    PendingPurchasesInstallationDialog.this.remindLaterButton.setEnabled(false);
                    PendingPurchasesInstallationDialog.this.remindNeverButton.setEnabled(false);
                    final HashMap e = UpdateDialog.resolveDependency(downloadList, PendingPurchasesInstallationDialog.this.packageDescriptorCache);
                    this.getProgressListener().setCompleted(30);
                    PendingPurchasesInstallationDialog.this.installablePackageList = UpdateDialog.getPackagesforInstallation(e);
                    final HashMap licenseNameToLicenseTextMap = UpdateDialog.collectLicenses(PendingPurchasesInstallationDialog.this.installablePackageList, this.getProgressListener(), 100, 30, 100);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            PendingPurchasesInstallationDialog.this.isConfirmed = ConfirmLicensesDialog.confirm(PendingPurchasesInstallationDialog.this, e, licenseNameToLicenseTextMap);
                            (new ProgressThread("installing_updates", true) {
                                public void run() {
                                    try {
                                        if(PendingPurchasesInstallationDialog.this.isConfirmed) {
                                            this.getProgressListener().setTotal(100);
                                            this.getProgressListener().setCompleted(20);
                                            UpdateService ex = MarketplaceUpdateManager.getService();
                                            MarketplaceUpdateManager um = new MarketplaceUpdateManager(ex);
                                            List installedPackages = um.performUpdates(PendingPurchasesInstallationDialog.this.installablePackageList, this.getProgressListener());
                                            if(installedPackages.size() > 0) {
                                                int confirmation = SwingTools.showConfirmDialog(installedPackages.size() == 1?"update.complete_restart":"update.complete_restart1", 0, new Object[]{Integer.valueOf(installedPackages.size())});
                                                if(confirmation == 0) {
                                                    RapidMinerGUI.getMainFrame().exit(true);
                                                } else if(confirmation == 1 && installedPackages.size() == PendingPurchasesInstallationDialog.this.installablePackageList.size()) {
                                                    PendingPurchasesInstallationDialog.this.dispose();
                                                }
                                            }
                                        }
                                    } catch (Exception var8) {
                                        SwingTools.showSimpleErrorMessage("error_installing_update", var8, new Object[]{var8.getMessage()});
                                    } finally {
                                        this.getProgressListener().complete();
                                    }

                                }
                            }).start();
                        }
                    });
                    PendingPurchasesInstallationDialog.this.remindLaterButton.setEnabled(true);
                    PendingPurchasesInstallationDialog.this.remindNeverButton.setEnabled(true);
                    this.getProgressListener().complete();
                } catch (Exception var3) {
                    SwingTools.showSimpleErrorMessage("error_resolving_dependencies", var3, new Object[]{var3.getMessage()});
                }

            }
        }).start();
    }

    private void checkNeverAskAgain() {
        if(this.neverAskAgain.isSelected()) {
            ParameterService.setParameterValue("rapidminer.update.purchased.not_installed.check", "false");
            ParameterService.saveParameters();
        }

    }

    private void neverRemindAgain() {
        LogService.getRoot().log(Level.CONFIG, "com.rapid_i.deployment.update.client.PurchasedNotInstalledDialog.saving_ignored_extensions_file");

        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException var7) {
            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapid_i.deployment.update.client.PurchasedNotInstalledDialog.creating_xml_document_error", new Object[]{var7}), var7);
            return;
        }

        Element root = doc.createElement("ignored_extensions.xml");
        doc.appendChild(root);
        Iterator file = this.purchasedModel.fetchPackageNames().iterator();

        while(file.hasNext()) {
            String e = (String)file.next();
            Element entryElem = doc.createElement("extension_name");
            entryElem.setTextContent(e);
            root.appendChild(entryElem);
        }

        File file1 = FileSystemService.getUserConfigFile("ignored_extensions.xml");

        try {
            XMLTools.stream(doc, file1, (Charset)null);
        } catch (XMLException var6) {
            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapid_i.deployment.update.client.PurchasedNotInstalledDialog.saving_ignored_extensions_file_error", new Object[]{var6}), var6);
        }

    }

    private class PurchasedNotInstalledModel extends AbstractPackageListModel {
        private static final long serialVersionUID = 1L;

        public PurchasedNotInstalledModel(PackageDescriptorCache cache) {
            super(cache, "gui.dialog.update.tab.no_packages");
        }

        public List<String> handleFetchPackageNames() {
            return PendingPurchasesInstallationDialog.this.packages;
        }
    }
}
