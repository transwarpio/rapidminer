package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.RapidMiner;
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.deployment.update.client.UpdateListCellRenderer;
import com.rapidminer.deployment.update.client.UpdatePackagesModel;
import com.rapidminer.deployment.update.client.UpdateServerAccount;
import com.rapidminer.deployment.update.client.listmodels.AbstractPackageListModel;
import com.rapidminer.deployment.update.client.listmodels.BookmarksPackageListModel;
import com.rapidminer.deployment.update.client.listmodels.LicencedPackageListModel;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.tools.ExtendedHTMLJEditorPane;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.VersionNumber;
import com.rapidminer.gui.tools.components.LinkLocalButton;
import com.rapidminer.gui.tools.components.LinkRemoteButton;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.RMUrlHandler;
import com.rapidminer.tools.plugin.ManagedExtension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

public class UpdatePanelTab extends JPanel {
    private JPanel extensionButtonPane;
    private static final long serialVersionUID = 1L;
    private static final int LIST_WIDTH = 330;
    protected UpdatePackagesModel updateModel;
    protected AbstractPackageListModel listModel;
    protected UpdateServerAccount usAccount;
    private ExtendedHTMLJEditorPane displayPane;
    private final UpdatePanelTab.SelectForInstallationButton installButton;
    private final LinkRemoteButton updateRMForOSXButton;
    private LinkLocalButton loginForInstallHint;
    private LinkRemoteButton extensionHomepageLink;
    private PackageDescriptor lastSelected = null;
    private JList packageList;

    public UpdatePanelTab(final UpdatePackagesModel updateModel, AbstractPackageListModel model, final UpdateServerAccount usAccount) {
        super(new GridBagLayout());
        this.updateModel = updateModel;
        this.listModel = model;
        this.usAccount = usAccount;
        this.usAccount.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                UpdatePanelTab.this.updateDisplayPane();
            }
        });
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 1;
        c.gridheight = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0D;
        c.weighty = 1.0D;
        c.insets = new Insets(0, 0, 0, 0);
        this.installButton = new UpdatePanelTab.SelectForInstallationButton(new ResourceAction(true, "update.select", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                UpdatePanelTab.this.markForInstallation((PackageDescriptor)UpdatePanelTab.this.getPackageList().getSelectedValue(), true, true);
            }
        });
        this.installButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!UpdatePanelTab.this.installButton.getPurchaseFirst()) {
                    if(UpdatePanelTab.this.installButton.isSelected()) {
                        UpdatePanelTab.this.installButton.setIcon(SwingTools.createIcon("16/checkbox.png"));
                    } else {
                        UpdatePanelTab.this.installButton.setIcon(SwingTools.createIcon("16/checkbox_unchecked.png"));
                    }
                }

            }
        });
        this.installButton.setEnabled(false);
        updateModel.addObserver(this.installButton);
        this.displayPane = new ExtendedHTMLJEditorPane("text/html", "");
        this.displayPane.setBackground(Colors.PANEL_BACKGROUND);
        this.displayPane.installDefaultStylesheet();
        ((HTMLEditorKit)this.displayPane.getEditorKit()).getStyleSheet().addRule("a  {text-decoration:underline; color:blue;}");
        this.setDefaultDescription();
        this.displayPane.setEditable(false);
        this.displayPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(EventType.ACTIVATED.equals(e.getEventType())) {
                    try {
                        RMUrlHandler.browse(e.getURL().toURI());
                    } catch (Exception var3) {
                        SwingTools.showVerySimpleErrorMessage("cannot_open_browser", new Object[0]);
                    }
                }

            }
        });
        this.loginForInstallHint = new LinkLocalButton(new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                usAccount.login(updateModel);
            }
        });
        this.extensionHomepageLink = new LinkRemoteButton(new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                PackageDescriptor selectedDescriptor = (PackageDescriptor)UpdatePanelTab.this.getPackageList().getSelectedValue();
                if(selectedDescriptor != null) {
                    String url = updateModel.getExtensionURL(selectedDescriptor);

                    try {
                        RMUrlHandler.browse(new URI(url));
                    } catch (URISyntaxException | IOException var5) {
                        SwingTools.showVerySimpleErrorMessage("cannot_open_browser", new Object[0]);
                    }
                }

            }
        });
        this.packageList = this.createUpdateList();
        ExtendedJScrollPane updateListScrollPane = new ExtendedJScrollPane(this.packageList);
        updateListScrollPane.setMinimumSize(new Dimension(330, 100));
        updateListScrollPane.setPreferredSize(new Dimension(330, 100));
        updateListScrollPane.setBorder((Border)null);
        updateListScrollPane.setHorizontalScrollBarPolicy(31);
        Component topPanel = this.makeTopPanel();
        Component bottomPanel = this.makeBottomPanel();
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(updateListScrollPane, "Center");
        if(topPanel != null) {
            leftPanel.add(topPanel, "North");
            this.add(leftPanel, c);
        }

        if(bottomPanel != null) {
            leftPanel.add(bottomPanel, "South");
            this.add(leftPanel, c);
        }

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        c.insets = new Insets(0, 0, 0, 0);
        ExtendedJScrollPane jScrollPane = new ExtendedJScrollPane(this.displayPane);
        jScrollPane.setHorizontalScrollBarPolicy(31);
        jScrollPane.setBorder((Border)null);
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(jScrollPane, "Center");
        this.extensionButtonPane = new JPanel(new BorderLayout());
        this.extensionButtonPane.setMinimumSize(new Dimension(100, 35));
        this.extensionButtonPane.setPreferredSize(new Dimension(100, 35));
        JPanel extensionButtonPaneLeft = new JPanel(new FlowLayout(0));
        this.updateRMForOSXButton = new LinkRemoteButton(new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                MarketplaceUpdateManager.openOSXDownloadURL();
            }
        });
        this.updateRMForOSXButton.setText(I18N.getGUILabel("update.osx.label", new Object[0]));
        extensionButtonPaneLeft.add(this.updateRMForOSXButton);
        extensionButtonPaneLeft.add(this.installButton);
        extensionButtonPaneLeft.add(this.loginForInstallHint);
        this.extensionButtonPane.add(extensionButtonPaneLeft, "West");
        JPanel extensionButtonPaneRight = new JPanel(new FlowLayout(2));
        this.extensionHomepageLink.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.label.update.extension_homepage.label", new Object[0]));
        extensionButtonPaneRight.add(this.extensionHomepageLink);
        this.extensionButtonPane.add(extensionButtonPaneRight, "Center");
        Component[] var13 = this.extensionButtonPane.getComponents();
        int var14 = var13.length;

        for(int var15 = 0; var15 < var14; ++var15) {
            Component component = var13[var15];
            component.setVisible(false);
        }

        this.extensionButtonPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        descriptionPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        descriptionPanel.add(this.extensionButtonPane, "South");
        this.add(descriptionPanel, c);
    }

    private void showProductPage(PackageDescriptor desc) {
        try {
            String e1 = MarketplaceUpdateManager.getBaseUrl() + "/faces/product_details.xhtml?productId=" + desc.getPackageId();
            RMUrlHandler.browse(new URI(e1));
        } catch (Exception var3) {
            SwingTools.showVerySimpleErrorMessage("cannot_open_browser", new Object[0]);
        }

    }

    protected Component makeTopPanel() {
        return null;
    }

    protected Component makeBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(0));
        panel.setMinimumSize(new Dimension(100, 35));
        panel.setPreferredSize(new Dimension(100, 35));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        return panel;
    }

    private JList createUpdateList() {
        JList updateList = new JList(this.listModel);
        updateList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    UpdatePanelTab.this.updateDisplayPane();
                }

            }
        });
        updateList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    PackageDescriptor selectedDescriptor = (PackageDescriptor)UpdatePanelTab.this.getPackageList().getSelectedValue();
                    if("rapidminer-studio-6".equals(selectedDescriptor.getPackageId())) {
                        if(MarketplaceUpdateManager.useOSXUpdateMechansim()) {
                            MarketplaceUpdateManager.openOSXDownloadURL();
                        } else {
                            UpdatePanelTab.this.markForInstallation(selectedDescriptor, true, true);
                        }
                    } else {
                        UpdatePanelTab.this.markForInstallation(selectedDescriptor, true, true);
                    }
                }

            }
        });
        updateList.setCellRenderer(new UpdateListCellRenderer(this.updateModel));
        return updateList;
    }

    protected void markForInstallation(final PackageDescriptor selectedDescriptor, boolean loginForRestricted, final boolean showProductPage) {
        if(!this.updateModel.isUpToDate(selectedDescriptor)) {
            if(selectedDescriptor.isRestricted()) {
                if(this.usAccount.isLoggedIn()) {
                    if(this.updateModel.isPurchased(selectedDescriptor)) {
                        this.updateModel.toggleSelectionForInstallation(selectedDescriptor);
                        this.getModel().updateView(selectedDescriptor);
                    } else if(showProductPage) {
                        this.showProductPage(selectedDescriptor);
                    }
                } else if(loginForRestricted) {
                    this.usAccount.login(this.updateModel, false, new Runnable() {
                        public void run() {
                            if(UpdatePanelTab.this.usAccount.isLoggedIn()) {
                                if(UpdatePanelTab.this.updateModel.isPurchased(selectedDescriptor)) {
                                    UpdatePanelTab.this.updateModel.toggleSelectionForInstallation(selectedDescriptor);
                                    UpdatePanelTab.this.getModel().updateView(selectedDescriptor);
                                } else if(showProductPage) {
                                    UpdatePanelTab.this.showProductPage(selectedDescriptor);
                                }
                            }

                        }
                    }, (Runnable)null);
                }
            } else {
                this.updateModel.toggleSelectionForInstallation(selectedDescriptor);
                this.getModel().updateView(selectedDescriptor);
            }

        }
    }

    protected JList getPackageList() {
        return this.packageList;
    }

    public void selectNotify() {
        if(this.listModel instanceof BookmarksPackageListModel || this.listModel instanceof LicencedPackageListModel) {
            this.usAccount.login(this.updateModel);
        }

        this.listModel.update();
    }

    public AbstractPackageListModel getModel() {
        return this.listModel;
    }

    private void setDefaultDescription() {
        (new Thread("Load Default Description") {
            public void run() {
                try {
                    String e = I18N.getMessage(I18N.getGUIBundle(), "gui.label.marketplace.news.url", new Object[0]);
                    UpdatePanelTab.this.displayPane.setPage(e);
                } catch (Exception var2) {
                    UpdatePanelTab.this.displayPane.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.dialog.update_welcome_message.text", new Object[]{MarketplaceUpdateManager.getBaseUrl()}));
                }

            }
        }).start();
    }

    private void updateDisplayPane() {
        Object selectedValue = this.packageList.getSelectedValue();
        PackageDescriptor desc = null;
        if(selectedValue instanceof PackageDescriptor) {
            desc = (PackageDescriptor)selectedValue;
            this.lastSelected = desc;
        } else {
            this.packageList.clearSelection();
            if(this.lastSelected != null) {
                desc = this.lastSelected;
            }
        }

        if(desc != null) {
            Component[] css = this.extensionButtonPane.getComponents();
            int doc = css.length;

            for(int isInstalled = 0; isInstalled < doc; ++isInstalled) {
                Component isUpToDate = css[isInstalled];
                isUpToDate.setVisible(true);
            }

            this.installButton.setVisible(false);
            this.updateRMForOSXButton.setVisible(false);
            this.extensionButtonPane.setVisible(true);
            StyleSheet var11 = new StyleSheet();
            var11.addRule("a  {text-decoration:underline; color:blue;}");
            var11.addRule("h1 {font-size: 14px;}");
            var11.addRule("h2 {font-size: 11px;font-weight:bold;}");
            var11.addRule("div, p, hr { margin-bottom:8px }");
            var11.addRule("div.changes-section{padding-left:10px;font-size:9px;color:#444444;}");
            var11.addRule(".changes-header-version {margin-top:10px;margin-bottom:5px;color:#111111;}");
            var11.addRule("ul {padding-left:10px;}");
            var11.addRule("ul li {margin-left:0px;padding-left:0px;}");
            HTMLDocument var12 = new HTMLDocument(var11);
            this.displayPane.setDocument(var12);
            this.displayPane.setText(this.updateModel.toString(desc, this.listModel.getChanges(desc.getPackageId())));
            this.displayPane.setCaretPosition(0);
            this.installButton.setSelected(this.updateModel.isSelectedForInstallation(desc));
            boolean var13 = false;
            boolean var14 = false;
            boolean isRapidMiner = "STAND_ALONE".equals(desc.getPackageTypeName());
            if(isRapidMiner) {
                var14 = RapidMiner.getVersion().isAtLeast(new VersionNumber(desc.getVersion()));
                var13 = true;
            } else {
                ManagedExtension ext = ManagedExtension.get(desc.getPackageId());
                if(ext != null) {
                    var13 = true;
                    String installed = ext.getLatestInstalledVersion();
                    if(installed != null) {
                        boolean upToDate = installed.compareTo(desc.getVersion()) >= 0;
                        if(upToDate) {
                            var14 = true;
                        } else {
                            var14 = false;
                        }
                    }
                }
            }

            if(desc.isRestricted() && !var13) {
                if(!this.usAccount.isLoggedIn()) {
                    this.installButton.setVisible(false);
                    this.loginForInstallHint.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.label.update.need_to_log_in.label", new Object[0]));
                } else if(this.updateModel.isPurchased(desc)) {
                    this.installButton.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.install.select.label", new Object[0]));
                    this.installButton.getAction().putValue("MnemonicKey", Integer.valueOf(I18N.getMessage(I18N.getGUIBundle(), "gui.action.install.select.mne", new Object[0]).toUpperCase().charAt(0)));
                    this.extensionHomepageLink.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.label.update.extension_homepage.label", new Object[0]));
                    this.installButton.setPurchaseFirst(false);
                    this.installButton.setVisible(true);
                    this.installButton.setEnabled(true);
                    this.loginForInstallHint.setText("");
                    if(this.updateModel.isSelectedForInstallation(desc)) {
                        this.installButton.setIcon(SwingTools.createIcon("16/checkbox.png"));
                    } else {
                        this.installButton.setIcon(SwingTools.createIcon("16/checkbox_unchecked.png"));
                    }
                } else {
                    this.installButton.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.update.purchase.label", new Object[0]));
                    this.installButton.setIcon(SwingTools.createIcon("16/shopping_cart.png"));
                    this.installButton.getAction().putValue("MnemonicKey", Integer.valueOf(I18N.getMessage(I18N.getGUIBundle(), "gui.action.update.purchase.mne", new Object[0]).toUpperCase().charAt(0)));
                    this.extensionHomepageLink.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.label.update.extension_homepage.label", new Object[0]));
                    this.installButton.setVisible(true);
                    this.loginForInstallHint.setText("");
                    this.installButton.setPurchaseFirst(true);
                }
            } else if(var13) {
                this.extensionHomepageLink.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.label.update.extension_homepage.label", new Object[0]));
                if(!var14) {
                    this.loginForInstallHint.setText("");
                    if(isRapidMiner && MarketplaceUpdateManager.useOSXUpdateMechansim()) {
                        this.updateRMForOSXButton.setVisible(true);
                    } else {
                        this.installButton.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.update.select.label", new Object[0]));
                        this.installButton.getAction().putValue("MnemonicKey", Integer.valueOf(I18N.getMessage(I18N.getGUIBundle(), "gui.action.update.select.mne", new Object[0]).toUpperCase().charAt(0)));
                        this.installButton.setPurchaseFirst(false);
                        this.installButton.setEnabled(true);
                        this.installButton.setVisible(true);
                        if(this.updateModel.isSelectedForInstallation(desc)) {
                            this.installButton.setIcon(SwingTools.createIcon("16/checkbox.png"));
                        } else {
                            this.installButton.setIcon(SwingTools.createIcon("16/checkbox_unchecked.png"));
                        }
                    }
                } else {
                    this.installButton.setVisible(false);
                    this.loginForInstallHint.setText("");
                }
            } else {
                this.installButton.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.install.select.label", new Object[0]));
                this.installButton.getAction().putValue("MnemonicKey", Integer.valueOf(I18N.getMessage(I18N.getGUIBundle(), "gui.action.install.select.mne", new Object[0]).toUpperCase().charAt(0)));
                this.extensionHomepageLink.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.label.update.extension_homepage.label", new Object[0]));
                this.installButton.setPurchaseFirst(false);
                this.installButton.setVisible(true);
                this.installButton.setEnabled(true);
                this.loginForInstallHint.setText("");
                if(this.updateModel.isSelectedForInstallation(desc)) {
                    this.installButton.setIcon(SwingTools.createIcon("16/checkbox.png"));
                } else {
                    this.installButton.setIcon(SwingTools.createIcon("16/checkbox_unchecked.png"));
                }
            }

            if(isRapidMiner) {
                this.extensionHomepageLink.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.label.update.product_homepage.label", new Object[0]));
            }
        }

    }

    public void removeNotify() {
        super.removeNotify();
        this.usAccount.deleteObservers();
        this.updateModel.deleteObservers();
    }

    private class SelectForInstallationButton extends JToggleButton implements Observer {
        private boolean purchaseFirst = false;
        private static final long serialVersionUID = 1L;

        public SelectForInstallationButton(Action a) {
            super(a);
        }

        public void setPurchaseFirst(boolean purchaseFirst) {
            this.purchaseFirst = purchaseFirst;
        }

        public boolean getPurchaseFirst() {
            return this.purchaseFirst;
        }

        public void update(Observable o, Object arg) {
            if(o instanceof UpdatePackagesModel) {
                UpdatePackagesModel currentModel = (UpdatePackagesModel)o;
                if(arg != null && arg instanceof PackageDescriptor) {
                    PackageDescriptor desc = (PackageDescriptor)arg;
                    Object selectedObject = UpdatePanelTab.this.getPackageList().getSelectedValue();
                    if(selectedObject instanceof PackageDescriptor) {
                        PackageDescriptor selectedDescriptor = (PackageDescriptor)selectedObject;
                        if(desc.getPackageId().equals(selectedDescriptor.getPackageId())) {
                            this.setSelected(currentModel.isSelectedForInstallation(desc));
                            if(this.isSelected()) {
                                this.setIcon(SwingTools.createIcon("16/checkbox.png"));
                            } else {
                                this.setIcon(SwingTools.createIcon("16/checkbox_unchecked.png"));
                            }
                        }
                    }

                    UpdatePanelTab.this.listModel.updateView(desc);
                }
            }

        }
    }
}
