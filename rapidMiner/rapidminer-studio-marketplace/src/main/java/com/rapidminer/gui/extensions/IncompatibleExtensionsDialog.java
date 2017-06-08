package com.rapidminer.gui.extensions;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.extensions.IncompatibleExtension.ExtensionEvent;
import com.rapidminer.gui.extensions.IncompatibleExtension.Fix;
import com.rapidminer.gui.tools.*;
import com.rapidminer.gui.tools.components.FixedWidthLabel;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.plugin.ManagedExtension;
import com.rapidminer.tools.plugin.Plugin;
import com.rapidminer.tools.update.internal.UpdateManagerRegistry;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

public class IncompatibleExtensionsDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private final JButton ignoreButton;
    private final JButton applyButton;
    private final List<IncompatibleExtension> toRemove;
    private final List<IncompatibleExtension> toUpdate;

    public IncompatibleExtensionsDialog() {
        super(ApplicationFrame.getApplicationFrame());
        Collection plugins = Plugin.getIncompatiblePlugins();
        final ArrayList extensions = new ArrayList(plugins.size());
        Iterator checkWorker = plugins.iterator();

        while(checkWorker.hasNext()) {
            Plugin removalWorker = (Plugin)checkWorker.next();
            extensions.add(new IncompatibleExtension(removalWorker));
        }

        this.toRemove = new ArrayList(extensions.size());
        this.toUpdate = new ArrayList(extensions.size());
        final ProgressThread checkWorker1 = new ProgressThread("incompatible_extensions.check") {
            public void run() {
                boolean communicationFailure = false;
                Iterator var2 = extensions.iterator();

                while(var2.hasNext()) {
                    IncompatibleExtension extension = (IncompatibleExtension)var2.next();
                    this.checkCancelled();

                    try {
                        extension.updateFixes();
                    } catch (URISyntaxException | IOException var5) {
                        LogService.getRoot().log(Level.WARNING, "Failed to communicate with marketplace", var5);
                        communicationFailure = true;
                        break;
                    }
                }

                if(communicationFailure) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            SwingTools.showVerySimpleErrorMessage("incompatible_extensions.failed_to_query_market_place", new Object[0]);
                        }
                    });
                } else {
                    this.checkCancelled();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            IncompatibleExtensionsDialog.this.applyButton.setEnabled(true);
                        }
                    });
                }

            }

            public boolean isCancelable() {
                return true;
            }
        };
        final ProgressThread removalWorker1 = new ProgressThread("incompatible_extensions.remove", true) {
            public void run() {
                Iterator var1 = IncompatibleExtensionsDialog.this.toRemove.iterator();

                while(var1.hasNext()) {
                    IncompatibleExtension extension = (IncompatibleExtension)var1.next();
                    ManagedExtension.get(extension.getId()).uninstallActiveVersion();
                }

            }
        };
        removalWorker1.setIndeterminate(true);
        final ProgressThread updateWorker = new ProgressThread("incompatible_extensions.update") {
            public void run() {
                ArrayList packages = new ArrayList(IncompatibleExtensionsDialog.this.toUpdate.size());
                Iterator e = IncompatibleExtensionsDialog.this.toUpdate.iterator();

                while(e.hasNext()) {
                    IncompatibleExtension extension = (IncompatibleExtension)e.next();
                    packages.add(extension.getId());
                }

                try {
                    UpdateManagerRegistry.INSTANCE.get().installSelectedPackages(packages);
                } catch (IOException | URISyntaxException var4) {
                    LogService.getRoot().log(Level.SEVERE, "Failed to open marketplace.", var4);
                    SwingTools.showVerySimpleErrorMessage("incompatible_extensions.failed_to_open_market_place", new Object[0]);
                }

            }
        };
        updateWorker.setIndeterminate(true);
        ResourceAction ignore = new ResourceAction("incompatible_extensions.ignore", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                IncompatibleExtensionsDialog.this.ignoreButton.setEnabled(false);
                IncompatibleExtensionsDialog.this.applyButton.setEnabled(false);
                checkWorker1.cancel();
                IncompatibleExtensionsDialog.this.dispose();
            }
        };
        ResourceAction apply = new ResourceAction("incompatible_extensions.apply", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                IncompatibleExtensionsDialog.this.toRemove.clear();
                IncompatibleExtensionsDialog.this.toUpdate.clear();
                Iterator var2 = extensions.iterator();

                while(var2.hasNext()) {
                    IncompatibleExtension extension = (IncompatibleExtension)var2.next();
                    switch(extension.getSelectedFix().ordinal()) {
                        case 1:
                            IncompatibleExtensionsDialog.this.toRemove.add(extension);
                            break;
                        case 2:
                            IncompatibleExtensionsDialog.this.toUpdate.add(extension);
                        case 3:
                    }
                }

                if(!IncompatibleExtensionsDialog.this.toRemove.isEmpty()) {
                    updateWorker.addDependency(new String[]{removalWorker1.getID()});
                    removalWorker1.start();
                }

                if(!IncompatibleExtensionsDialog.this.toUpdate.isEmpty()) {
                    updateWorker.start();
                }

                IncompatibleExtensionsDialog.this.dispose();
            }
        };
        this.setDefaultCloseOperation(2);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                checkWorker1.cancel();
            }
        });
        String title = I18N.getGUIMessage("gui.dialog.incompatible_extensions.title", new Object[0]);
        String message = I18N.getGUIMessage("gui.dialog.incompatible_extensions.message", new Object[0]);
        String icon = I18N.getGUIMessage("gui.dialog.incompatible_extensions.icon", new Object[0]);
        JPanel header = new JPanel(new FlowLayout(0, 12, 8));
        header.add(new JLabel(SwingTools.createIcon("48/" + icon)));
        header.add(new FixedWidthLabel(400, message));
        IncompatibleExtensionsDialog.ExtensionList content = new IncompatibleExtensionsDialog.ExtensionList(extensions);
        JPanel buttons = new JPanel(new FlowLayout(2));
        this.applyButton = new JButton(apply);
        this.applyButton.setEnabled(false);
        this.ignoreButton = new JButton(ignore);
        buttons.add(this.applyButton);
        buttons.add(this.ignoreButton);
        this.setTitle(title);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setResizable(false);
        this.add(header, "North");
        this.add(content, "Center");
        this.add(buttons, "South");
        Dimension size = header.getPreferredSize();
        size.width += 8;
        size.height = 300;
        this.setPreferredSize(size);
        this.pack();
        this.setLocationRelativeTo(RapidMinerGUI.getMainFrame());
        checkWorker1.start();
    }

    private static class ExtensionList extends ExtendedJScrollPane {
        private static final long serialVersionUID = 1L;
        private static final Color PRIMARY_BACKGROUND;
        private static final Color SECONDARY_BACKGROUND;
        private static final Border BORDER;
        private final List<IncompatibleExtension> extensions;

        public ExtensionList(List<IncompatibleExtension> extensions) {
            this.extensions = extensions;
            this.setBorder(BORDER);
            this.getViewport().setView(this.createList());
        }

        private JPanel createList() {
            JPanel list = new JPanel();
            list.setLayout(new GridBagLayout());
            list.setBackground(PRIMARY_BACKGROUND);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.fill = 2;
            constraints.weightx = 1.0D;
            constraints.weighty = 0.0D;
            boolean even = true;

            for(Iterator var4 = this.extensions.iterator(); var4.hasNext(); even = !even) {
                IncompatibleExtension extension = (IncompatibleExtension)var4.next();
                IncompatibleExtensionsDialog.ExtensionPanel panel = new IncompatibleExtensionsDialog.ExtensionPanel(extension);
                panel.setBackground(even?PRIMARY_BACKGROUND:SECONDARY_BACKGROUND);
                list.add(panel, constraints);
                ++constraints.gridy;
            }

            constraints.fill = 1;
            constraints.weighty = 1.0D;
            list.add(Box.createVerticalGlue(), constraints);
            return list;
        }

        static {
            PRIMARY_BACKGROUND = Color.WHITE;
            SECONDARY_BACKGROUND = new Color(225, 225, 225);
            BORDER = BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY);
        }
    }

    private static class ExtensionPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final GridBagConstraints ICON_CONSTRAINTS = new GridBagConstraints();
        private static final GridBagConstraints LABEL_CONSTRAINTS;
        private static final GridBagConstraints CONTROL_CONSTRAINTS;
        private static final Dimension CONTROL_SIZE = new Dimension(120, 24);
        private static final Icon UNKNOWN_ICON = SwingTools.createIcon("48/" + I18N.getGUIMessage("gui.dialog.incompatible_extensions.unknown_icon", new Object[0]));
        private JComponent currentControl;
        private final IncompatibleExtension extension;

        ExtensionPanel(IncompatibleExtension extension) {
            this.extension = extension;
            this.setLayout(new GridBagLayout());
            Object icon = extension.getPlugin().getExtensionIcon();
            if(icon == null) {
                icon = UNKNOWN_ICON;
            }

            this.add(new JLabel((Icon)icon), ICON_CONSTRAINTS);
            String name = extension.getPlugin().getName();
            String version = extension.getPlugin().getVersion();
            this.add(new ResourceLabel("incompatible_extension", new Object[]{name, version}), LABEL_CONSTRAINTS);
            this.showProgressIndicator();
            extension.addObserver(new Observer() {
                public void update(Observable o, Object arg) {
                    if((ExtensionEvent)arg == ExtensionEvent.CHECK_COMPLETED) {
                        final List fixes = ((IncompatibleExtension)o).getAvailableFixes();
                        String[] fixLabels = new String[fixes.size()];
                        int i = 0;

                        Fix fix;
                        for(Iterator var6 = fixes.iterator(); var6.hasNext(); fixLabels[i++] = fix.toString()) {
                            fix = (Fix)var6.next();
                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                ExtensionPanel.this.showFixes((Fix[])fixes.toArray(new Fix[fixes.size()]));
                            }
                        });
                    }

                }
            });
        }

        public void showProgressIndicator() {
            if(this.currentControl != null) {
                this.remove(this.currentControl);
            }

            JProgressBar progressBar = new JProgressBar();
            progressBar.setPreferredSize(CONTROL_SIZE);
            progressBar.setIndeterminate(true);
            this.add(progressBar, CONTROL_CONSTRAINTS);
            this.currentControl = progressBar;
            this.revalidate();
        }

        public void showFixes(Fix[] fixes) {
            if(this.currentControl != null) {
                this.remove(this.currentControl);
            }

            JComboBox comboBox = new JComboBox(fixes);
            comboBox.setPreferredSize(CONTROL_SIZE);
            this.add(comboBox, CONTROL_CONSTRAINTS);
            comboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if(e.getID() == 701 && e.getStateChange() == 1) {
                        ExtensionPanel.this.extension.setFix((Fix)e.getItem());
                    }

                }
            });
            this.currentControl = comboBox;
            this.revalidate();
            this.repaint();
        }

        static {
            ICON_CONSTRAINTS.insets = new Insets(4, 12, 4, 12);
            ICON_CONSTRAINTS.gridx = 0;
            ICON_CONSTRAINTS.weightx = 0.0D;
            LABEL_CONSTRAINTS = new GridBagConstraints();
            LABEL_CONSTRAINTS.insets = ICON_CONSTRAINTS.insets;
            LABEL_CONSTRAINTS.anchor = 10;
            LABEL_CONSTRAINTS.fill = 2;
            LABEL_CONSTRAINTS.gridx = 1;
            LABEL_CONSTRAINTS.weightx = 1.0D;
            CONTROL_CONSTRAINTS = new GridBagConstraints();
            CONTROL_CONSTRAINTS.insets = ICON_CONSTRAINTS.insets;
            CONTROL_CONSTRAINTS.anchor = 10;
            CONTROL_CONSTRAINTS.gridx = 2;
            CONTROL_CONSTRAINTS.weightx = 0.0D;
        }
    }
}
