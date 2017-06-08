package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.RepositoryProcessLocation;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.actions.OpenAction;
import com.rapidminer.gui.autosave.AutoSave;
import com.rapidminer.gui.startup.CardHeaderPanel;
import com.rapidminer.gui.startup.GettingStartedDialog;
import com.rapidminer.gui.startup.OpenProcessEntryList;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.repository.Entry;
import com.rapidminer.repository.IOObjectEntry;
import com.rapidminer.repository.MalformedRepositoryLocationException;
import com.rapidminer.repository.ProcessEntry;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.repository.RepositoryLocation;
import com.rapidminer.repository.gui.RepositoryLocationChooser;
import com.rapidminer.tools.I18N;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListModel;

class OpenProcessCard extends JPanel {
    private static final String CARD_FILECHOOSER = "filechooser";
    private static final String CARD_OVERVIEW = "overview";
    private static final Color WARNING_BACKGROUND_COLOR = new Color(252, 248, 160);
    private static final Color WARNING_TEXT_COLOR = new Color(90, 79, 29);
    private static final Color WARNING_BORDER_COLOR = new Color(250, 235, 204);
    private static final long serialVersionUID = 1L;
    private boolean autosavedProcessPresent;
    private final JDialog owner;
    private RepositoryLocationChooser chooser;
    private OpenProcessEntryList entryList;
    private boolean isOverviewShown = true;

    public OpenProcessCard(JDialog owner) {
        this.owner = owner;
        this.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
        this.setLayout(new CardLayout());
        this.add(this.createOverviewCard(), "overview");
        this.add(this.createFilechooserCard(), "filechooser");
    }

    public boolean shouldBeShown() {
        return this.autosavedProcessPresent;
    }

    private JPanel createFilechooserCard() {
        JPanel filechooserPanel = new JPanel(new BorderLayout());
        filechooserPanel.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
        filechooserPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        filechooserPanel.add(new CardHeaderPanel("getting_started.header.location"), "North");
        this.chooser = new RepositoryLocationChooser(this.owner, (RepositoryLocation)null, (String)null);
        this.chooser.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
        this.chooser.setBorder(BorderFactory.createEmptyBorder(5, 20, 0, 20));
        Component[] buttonPanel = this.chooser.getComponents();
        int var3 = buttonPanel.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Component component = buttonPanel[var4];
            component.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
        }

        filechooserPanel.add(this.chooser, "Center");
        JPanel var6 = new JPanel(new FlowLayout(2));
        var6.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
        var6.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        var6.add(this.createSelectButton(this.chooser));
        var6.add(this.createBackButton());
        filechooserPanel.add(var6, "South");
        return filechooserPanel;
    }

    private JComponent createBackButton() {
        JButton backButton = new JButton(new ResourceAction("getting_started.back", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout)OpenProcessCard.this.getLayout();
                layout.show(OpenProcessCard.this, "overview");
                OpenProcessCard.this.isOverviewShown = true;
                if(OpenProcessCard.this.entryList.getModel().getSize() > 0) {
                    OpenProcessCard.this.entryList.setSelectedIndex(0);
                }

                OpenProcessCard.this.entryList.requestFocusInWindow();
            }
        });
        backButton.setFocusable(false);
        this.styleButton(backButton);
        return backButton;
    }

    private JComponent createSelectButton(final RepositoryLocationChooser chooser) {
        JButton selectButton = new JButton(new ResourceAction("getting_started.open", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                if(RapidMinerGUI.getMainFrame().close()) {
                    try {
                        GettingStartedDialog.logStats("open_process_card", "open_from_location");
                        String e1 = chooser.getRepositoryLocation();
                        if(e1 != null) {
                            try {
                                RepositoryLocation e11 = new RepositoryLocation(e1);
                                Entry entry = e11.locateEntry();
                                if(entry instanceof ProcessEntry) {
                                    OpenAction.open(new RepositoryProcessLocation(e11), true);
                                    OpenProcessCard.this.owner.dispose();
                                } else if(entry instanceof IOObjectEntry) {
                                    OpenAction.showAsResult((IOObjectEntry)entry);
                                    OpenProcessCard.this.owner.dispose();
                                } else {
                                    SwingTools.showVerySimpleErrorMessage("no_data_or_process", new Object[0]);
                                }
                            } catch (MalformedRepositoryLocationException | RepositoryException var5) {
                                SwingTools.showSimpleErrorMessage("while_loading", var5, new Object[]{e1, var5.getMessage()});
                            }
                        }
                    } catch (MalformedRepositoryLocationException var6) {
                        SwingTools.showSimpleErrorMessage("while_loading", var6, new Object[]{"", var6.getMessage()});
                    }

                }
            }
        });
        selectButton.setFocusable(false);
        this.styleButton(selectButton);
        return selectButton;
    }

    private JPanel createOverviewCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = 2;
        c.weightx = 1.0D;
        c.anchor = 21;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(new CardHeaderPanel("getting_started.header.recent"), c);
        ++c.gridy;
        c.insets = new Insets(5, 20, 10, 20);
        JPanel autosaveMarker = this.checkForAutosaved();
        if(autosaveMarker != null) {
            panel.add(autosaveMarker, c);
            ++c.gridy;
        }

        List recentFiles = RapidMinerGUI.getRecentFiles();
        if(recentFiles.isEmpty()) {
            JLabel fileButton = new JLabel(I18N.getGUILabel("getting_started.no_recent_files", new Object[0]));
            fileButton.setFont(GettingStartedDialog.OPEN_SANS_LIGHT_14);
            fileButton.setForeground(Color.LIGHT_GRAY);
            panel.add(fileButton, c);
            ++c.gridy;
        } else {
            this.entryList = new OpenProcessEntryList(recentFiles, this.owner);
            panel.add(this.entryList, c);
            ++c.gridy;
        }

        JButton var6 = new JButton(new ResourceAction("getting_started.open_another", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                CardLayout layout = (CardLayout)OpenProcessCard.this.getLayout();
                layout.show(OpenProcessCard.this, "filechooser");
                OpenProcessCard.this.isOverviewShown = false;
                OpenProcessCard.this.chooser.requestFocusInWindow();
            }
        });
        var6.setFocusable(false);
        this.styleButton(var6);
        c.fill = 0;
        c.insets = new Insets(20, 20, 10, 20);
        panel.add(var6, c);
        ++c.gridy;
        c.weighty = 1.0D;
        panel.add(new JLabel(), c);
        return panel;
    }

    private JPanel checkForAutosaved() {
        AutoSave autosave = RapidMinerGUI.getAutoSave();
        this.autosavedProcessPresent = autosave.isRecoveryProcessPresent();
        if(this.autosavedProcessPresent) {
            JPanel recoverPanel = new JPanel(new GridBagLayout());
            recoverPanel.setBackground(WARNING_BACKGROUND_COLOR);
            recoverPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(WARNING_BORDER_COLOR, 1, true), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            JLabel interrupted = new JLabel(I18N.getGUILabel("getting_started.info_interrupted", new Object[0]));
            interrupted.setForeground(WARNING_TEXT_COLOR);
            interrupted.setFont(GettingStartedDialog.OPEN_SANS_SEMIBOLD_14);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            recoverPanel.add(interrupted, c);
            JPanel buttonPanel = this.createRecoverButtonPanel(autosave);
            c.gridy = 1;
            c.insets = new Insets(10, 0, 0, 0);
            recoverPanel.add(buttonPanel, c);
            return recoverPanel;
        } else {
            return null;
        }
    }

    private JPanel createRecoverButtonPanel(final AutoSave autosave) {
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(WARNING_BACKGROUND_COLOR);
        final JLabel recoverLabel = new JLabel(I18N.getGUILabel("getting_started.label.recover", new Object[0]));
        recoverLabel.setIcon(SwingTools.createIcon("16/loading.gif"));
        recoverLabel.setFont(GettingStartedDialog.OPEN_SANS_SEMIBOLD_14);
        recoverLabel.setForeground(WARNING_TEXT_COLOR);
        recoverLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        String autosavedPath = autosave.getAutosavedPath();
        JButton recoverButton = new JButton(new ResourceAction("getting_started.recover", new Object[]{autosavedPath == null?"autosaved process":autosavedPath}) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                if(OpenProcessCard.this.entryList != null) {
                    OpenProcessCard.this.entryList.setFocusable(false);
                }

                buttonPanel.removeAll();
                buttonPanel.add(recoverLabel);
                buttonPanel.revalidate();
                (new ProgressThread("recover_process") {
                    public void run() {
                        autosave.recoverAutosavedProcess();
                        SwingTools.invokeLater(new Runnable() {
                            public void run() {
                                if(OpenProcessCard.this.entryList != null) {
                                    OpenProcessCard.this.entryList.setFocusable(true);
                                }

                                OpenProcessCard.this.owner.dispose();
                            }
                        });
                    }
                }).start();
            }
        });
        this.styleButton(recoverButton);
        buttonPanel.add(recoverButton);
        this.owner.getRootPane().setDefaultButton(recoverButton);
        return buttonPanel;
    }

    private void styleButton(JButton button) {
        button.setFont(GettingStartedDialog.OPEN_SANS_SEMIBOLD_14);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    public boolean requestFocusInWindow() {
        if(this.isOverviewShown && this.entryList != null) {
            ListModel model = this.entryList.getModel();
            if(model != null && model.getSize() > 0) {
                this.entryList.setSelectedIndex(0);
            }

            return this.entryList.requestFocusInWindow();
        } else {
            return this.chooser.requestFocusInWindow();
        }
    }
}
