package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.ResourceLabel;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.dialogs.ButtonDialog;
import com.rapidminer.tools.plugin.ManagedExtension;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class ExtensionDialog extends ButtonDialog {
    public static final Action MANAGE_EXTENSIONS = new ResourceAction("manage_extensions", new Object[0]) {
        private static final long serialVersionUID = 1L;

        {
            this.setCondition(9, 0);
        }

        public void actionPerformed(ActionEvent e) {
            (new ExtensionDialog()).setVisible(true);
        }
    };
    private static final long serialVersionUID = 1L;
    private boolean changed = false;

    public ExtensionDialog() {
        super(ApplicationFrame.getApplicationFrame(), "manage_extensions", ModalityType.MODELESS, new Object[0]);
        Collection allExtensions = ManagedExtension.getAll();
        if(allExtensions.isEmpty()) {
            ResourceLabel main = new ResourceLabel("no_extensions_installed", new Object[0]);
            main.setPreferredSize(new Dimension(300, 100));
            this.layoutDefault(main, new AbstractButton[]{this.makeCloseButton()});
        } else {
            final JPanel main1 = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = 23;
            c.fill = 1;
            c.weightx = 1.0D;
            c.weighty = 0.0D;
            Iterator mainScrollPane = allExtensions.iterator();

            while(mainScrollPane.hasNext()) {
                final ManagedExtension ext = (ManagedExtension)mainScrollPane.next();
                c.gridwidth = 1;
                c.weightx = 0.7D;
                c.insets = new Insets(5, 0, 0, 5);
                final JCheckBox activate = new JCheckBox(ext.getName());
                main1.add(activate, c);
                c.weightx = 0.3D;
                c.gridwidth = -1;
                final JComboBox versionCombo = new JComboBox(ext.getInstalledVersions().toArray());
                main1.add(versionCombo, c);
                final JButton deleteButton = new JButton();
                ResourceAction uninstallAction = new ResourceAction(true, "uninstall_extension", new Object[0]) {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        if(SwingTools.showConfirmDialog("really_uninstall_extension", 0, new Object[]{ext.getName() + " v." + ext.getSelectedVersion()}) == 0) {
                            String selectedVersion = ext.getSelectedVersion();
                            if(ext.uninstallActiveVersion()) {
                                activate.setSelected(false);
                                versionCombo.removeItem(selectedVersion);
                                versionCombo.setSelectedIndex(-1);
                                if(ManagedExtension.get(ext.getPackageId()) == null) {
                                    main1.remove(activate);
                                    main1.remove(versionCombo);
                                    main1.remove(deleteButton);
                                }
                            } else {
                                SwingTools.showVerySimpleErrorMessage("error_uninstalling_extension", new Object[0]);
                            }

                            ExtensionDialog.this.changed = true;
                        }

                    }
                };
                deleteButton.setAction(uninstallAction);
                c.gridwidth = 0;
                c.weightx = 0.0D;
                c.insets = new Insets(5, 0, 0, 0);
                main1.add(deleteButton, c);
                activate.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ExtensionDialog.this.changed = true;
                        versionCombo.setEnabled(activate.isSelected());
                        ext.setActive(activate.isSelected());
                    }
                });
                versionCombo.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ExtensionDialog.this.changed = true;
                        ext.setSelectedVersion((String)versionCombo.getSelectedItem());
                    }
                });
                activate.setSelected(ext.isActive());
                versionCombo.setEnabled(ext.isActive());
                versionCombo.setSelectedItem(ext.getSelectedVersion());
            }

            c.gridwidth = 0;
            c.weighty = 1.0D;
            main1.add(new JPanel(), c);
            ExtendedJScrollPane mainScrollPane1 = new ExtendedJScrollPane(main1);
            if(mainScrollPane1.getPreferredSize().getHeight() < 50.0D) {
                mainScrollPane1.setPreferredSize(new Dimension((int)mainScrollPane1.getPreferredSize().getWidth(), 50));
            }

            mainScrollPane1.setBorder((Border)null);
            this.layoutDefault(mainScrollPane1, new AbstractButton[]{this.makeCloseButton()});
        }

        this.changed = false;
    }

    protected void close() {
        if(this.changed) {
            ManagedExtension.saveConfiguration();
            if(SwingTools.showConfirmDialog("manage_extensions.restart", 0, new Object[0]) == 0) {
                RapidMinerGUI.getMainFrame().exit(true);
            }
        }

        super.close();
    }
}
