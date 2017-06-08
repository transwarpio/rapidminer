package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.client.wsimport.UpdateService;
import com.rapidminer.deployment.update.client.MarketplaceUpdateManager;
import com.rapidminer.deployment.update.client.PackageListCellRenderer;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.ResourceLabel;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.RMUrlHandler;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.HyperlinkEvent.EventType;

public class ConfirmLicensesDialog extends ButtonDialog {
    private static final long serialVersionUID = 4276757146820898347L;
    private JButton okButton;
    private JEditorPane licensePane = new JEditorPane("text/html", "");
    private static final int LIST_WIDTH = 330;
    private JList selectedForInstallList;
    private JList dependentPackages;
    private ResourceLabel licenseLabel;
    private Map<String, String> licenseNameToLicenseTextMap;
    private static String LOADING_LICENSE_TEXT = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en\" xml:lang=\"en\"><head><table cellpadding=0 cellspacing=0><tr><td><img src=\"" + SwingTools.getIconPath("48/hourglass.png") + "\" /></td>" + "<td width=\"5\">" + "</td>" + "<td>" + I18N.getGUILabel("loading_license", new Object[0]) + "</td></tr>" + "</table>" + "</head>" + "</html>";
    private static String ERROR_LOADING_LICENSE_TEXCT = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en\" xml:lang=\"en\"><head><table cellpadding=0 cellspacing=0><tr><td><img src=\"" + SwingTools.getIconPath("48/error.png") + "\" /></td>" + "<td width=\"5\">" + "</td>" + "<td>" + I18N.getGUILabel("error_loading_license", new Object[0]) + "</td></tr>" + "</table>" + "</head>" + "</html>";
    private boolean licenseLoadingFailed = true;
    private JCheckBox acceptReject;

    public ConfirmLicensesDialog(Dialog owner, HashMap<PackageDescriptor, HashSet<PackageDescriptor>> dependency, HashMap<String, String> licenseNameToLicenseTextMap) {
        super(owner, "confirm_licenses", ModalityType.APPLICATION_MODAL, new Object[]{"updates"});
        if(licenseNameToLicenseTextMap != null) {
            this.licenseNameToLicenseTextMap = licenseNameToLicenseTextMap;
            this.licenseLoadingFailed = false;
        } else {
            this.licenseNameToLicenseTextMap = new HashMap();
        }

        JPanel main = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 1;
        c.anchor = 23;
        c.weightx = 0.0D;
        c.weighty = 1.0D;
        c.gridwidth = -1;
        c.gridx = 0;
        c.weighty = 0.0D;
        c.insets = new Insets(0, 0, 2, 0);
        ResourceLabel label = new ResourceLabel("selected_packages", new Object[0]);
        label.setFont(label.getFont().deriveFont(1));
        main.add(label, c);
        Set selectedPackages = dependency.keySet();
        this.selectedForInstallList = new JList(selectedPackages.toArray());
        label.setLabelFor(this.selectedForInstallList);
        HashMap invertedDependency = new HashMap();
        Iterator depPackages = selectedPackages.iterator();

        while(depPackages.hasNext()) {
            PackageDescriptor selectedForInstallPane = (PackageDescriptor)depPackages.next();
            Iterator sourceCellRenderer = ((HashSet)dependency.get(selectedForInstallPane)).iterator();

            while(sourceCellRenderer.hasNext()) {
                PackageDescriptor dependentLabel = (PackageDescriptor)sourceCellRenderer.next();
                if(!invertedDependency.containsKey(dependentLabel)) {
                    invertedDependency.put(dependentLabel, new HashSet());
                    ((HashSet)invertedDependency.get(dependentLabel)).add(selectedForInstallPane);
                } else {
                    ((HashSet)invertedDependency.get(dependentLabel)).add(selectedForInstallPane);
                }
            }
        }

        Set depPackages1 = invertedDependency.keySet();
        this.dependentPackages = new JList(depPackages1.toArray());
        c.gridx = 0;
        c.gridy = -1;
        c.weighty = 1.0D;
        c.insets = new Insets(0, 0, 0, 0);
        ExtendedJScrollPane selectedForInstallPane1 = new ExtendedJScrollPane(this.selectedForInstallList);
        selectedForInstallPane1.setMinimumSize(new Dimension(330, 100));
        selectedForInstallPane1.setPreferredSize(new Dimension(330, 100));
        selectedForInstallPane1.setHorizontalScrollBarPolicy(31);
        selectedForInstallPane1.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        this.selectedForInstallList.addListSelectionListener(new ConfirmLicensesDialog.LicenseListSelectionListener(this.dependentPackages));
        PackageListCellRenderer sourceCellRenderer1 = new PackageListCellRenderer((HashMap)null);
        this.selectedForInstallList.setCellRenderer(sourceCellRenderer1);
        main.add(selectedForInstallPane1, c);
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.0D;
        c.insets = new Insets(10, 0, 0, 0);
        ResourceLabel dependentLabel1 = new ResourceLabel("dependent_packages", new Object[0]);
        dependentLabel1.setFont(dependentLabel1.getFont().deriveFont(1));
        dependentLabel1.setLabelFor(this.dependentPackages);
        main.add(dependentLabel1, c);
        c.gridx = 0;
        c.gridy = -1;
        c.weighty = 1.0D;
        c.insets = new Insets(0, 0, 0, 0);
        ExtendedJScrollPane dependentPackagesPane = new ExtendedJScrollPane(this.dependentPackages);
        dependentPackagesPane.setMinimumSize(new Dimension(330, 100));
        dependentPackagesPane.setPreferredSize(new Dimension(330, 100));
        dependentPackagesPane.setHorizontalScrollBarPolicy(31);
        dependentPackagesPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        this.dependentPackages.addListSelectionListener(new ConfirmLicensesDialog.LicenseListSelectionListener(this.selectedForInstallList));
        PackageListCellRenderer depCellRenderer = new PackageListCellRenderer(invertedDependency);
        this.dependentPackages.setCellRenderer(depCellRenderer);
        main.add(dependentPackagesPane, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.0D;
        c.weightx = 1.0D;
        c.insets = new Insets(0, 10, 1, 0);
        this.licenseLabel = new ResourceLabel("license_label", new Object[0]);
        main.add(this.licenseLabel, c);
        c.gridx = 1;
        c.gridy = -1;
        c.gridheight = 4;
        c.insets = new Insets(0, 10, 1, 0);
        this.licensePane.setEditable(false);
        this.licensePane.setBackground(Colors.PANEL_BACKGROUND);
        JScrollPane scrollPane = new JScrollPane(this.licensePane);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setHorizontalScrollBarPolicy(31);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
        main.add(scrollPane, c);
        this.licensePane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == EventType.ACTIVATED) {
                    RMUrlHandler.handleUrl(e.getDescription());
                }

            }
        });
        this.acceptReject = new JCheckBox(new ResourceAction("accept_license", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                if(ConfirmLicensesDialog.this.acceptReject.isSelected()) {
                    ConfirmLicensesDialog.this.enableButtons();
                } else {
                    ConfirmLicensesDialog.this.okButton.setEnabled(false);
                }

            }
        });
        this.okButton = this.makeOkButton("update.install");
        this.okButton.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.action.update.install.label", new Object[]{Integer.valueOf(selectedPackages.size() + depPackages1.size())}));
        this.okButton.setEnabled(false);
        this.layoutDefault(main, 9, new AbstractButton[]{this.acceptReject, this.okButton, this.makeCancelButton("skip_install")});
        this.enableButtons();
    }

    private void setInitialSelection() {
        this.selectedForInstallList.setSelectedIndex(0);
    }

    private void enableButtons() {
        this.okButton.setEnabled(this.acceptReject.isSelected() && !this.licenseLoadingFailed);
        this.acceptReject.setEnabled(!this.licenseLoadingFailed);
    }

    private void setLicensePaneContent(PackageDescriptor desc) {
        this.licenseLabel.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.label.license_label.label", new Object[]{desc.getName()}));
        final String licenseName = desc.getLicenseName();
        String licenseText = (String)this.licenseNameToLicenseTextMap.get(licenseName);
        if(licenseText != null) {
            this.setLicenseText(licenseText);
        } else {
            this.licensePane.setText(LOADING_LICENSE_TEXT);
            (new Thread("fetching-license") {
                public void run() {
                    UpdateService service = null;

                    try {
                        service = MarketplaceUpdateManager.getService();
                        String e = service.getLicenseTextHtml(licenseName);
                        ConfirmLicensesDialog.this.licenseNameToLicenseTextMap.put(licenseName, e);
                        ConfirmLicensesDialog.this.licenseLoadingFailed = false;
                        ConfirmLicensesDialog.this.setLicenseText(e);
                    } catch (Exception var3) {
                        ConfirmLicensesDialog.this.licenseLoadingFailed = true;
                        ConfirmLicensesDialog.this.setLicenseText(ConfirmLicensesDialog.ERROR_LOADING_LICENSE_TEXCT);
                    }

                    ConfirmLicensesDialog.this.enableButtons();
                }
            }).start();
        }

    }

    private void setLicenseText(final String licenseText) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ConfirmLicensesDialog.this.licensePane.setText(licenseText);
                ConfirmLicensesDialog.this.licensePane.setCaretPosition(0);
            }
        });
    }

    public static boolean confirm(Dialog owner, HashMap<PackageDescriptor, HashSet<PackageDescriptor>> dependency, HashMap<String, String> licenseNameToLicenseTextMap) {
        ConfirmLicensesDialog d = new ConfirmLicensesDialog(owner, dependency, licenseNameToLicenseTextMap);
        d.setInitialSelection();
        d.setVisible(true);
        return d.wasConfirmed();
    }

    private class LicenseListSelectionListener implements ListSelectionListener {
        private JList otherList;

        public LicenseListSelectionListener(JList otherList) {
            this.otherList = otherList;
        }

        public void valueChanged(ListSelectionEvent e) {
            if(!e.getValueIsAdjusting()) {
                JList source = (JList)e.getSource();
                if(!source.isSelectionEmpty()) {
                    if(!this.otherList.isSelectionEmpty()) {
                        this.otherList.clearSelection();
                    }

                    PackageDescriptor desc = null;
                    Object selectedValue = source.getSelectedValue();
                    desc = (PackageDescriptor)selectedValue;

                    try {
                        ConfirmLicensesDialog.this.setLicensePaneContent(desc);
                    } catch (Exception var6) {
                        SwingTools.showSimpleErrorMessage("error_installing_update", var6, new Object[]{var6.getMessage()});
                    }
                }
            }

        }
    }
}
