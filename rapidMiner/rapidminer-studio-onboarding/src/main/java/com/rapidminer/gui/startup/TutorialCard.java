package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.Process;
import com.rapidminer.gui.MainFrame;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.startup.CardHeaderPanel;
import com.rapidminer.gui.startup.GettingStartedDialog;
import com.rapidminer.gui.startup.TutorialEntryList;
import com.rapidminer.gui.tools.DockingTools;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.Ionicon;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.components.LinkLocalButton;
import com.rapidminer.gui.tools.components.LinkRemoteButton;
import com.rapidminer.repository.MalformedRepositoryLocationException;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.RMUrlHandler;
import com.rapidminer.tools.XMLException;
import com.rapidminer.tutorial.Tutorial;
import com.rapidminer.tutorial.TutorialGroup;
import com.rapidminer.tutorial.TutorialManager;
import com.rapidminer.tutorial.TutorialRegistry;
import com.rapidminer.tutorial.gui.TutorialBrowser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;

class TutorialCard extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String TUTORIAL_PENDING_TEXT;
    private static final String TUTORIAL_COMPLETED_TEXT;
    private static final String TUTORIAL_HEADLINE_TEMPLATE = "<html><span style=\"font-family: \'Open Sans Bold\';font-size: 15; color: #424242;\">%s</html>";
    private static final String TUTORIAL_CONTENT_TEMPLATE = "<html><div style=\"font-family: \'Open Sans\'; font-size: 13; color: #424242; width: 300;\">%s</div></html>";
    private static final String PROGRESS_THREAD_ID_LOAD_TUTORIAL = "load_tutorial";
    private static final String PROGRESS_THREAD_ID_LOAD_TUTORIALS = "load_tutorials";
    private static final String TUTORIALS_HEADLINE;
    private static final String EXTERNAL_RESOURCES_HEADLINE;
    private static final String TEXT_LOADING;
    private static final String DOCUMENTATION_URL;
    private static final String SUPPORT_URL;
    private static final String TRAINING_URL;
    private static final String VIDEO_URL;
    private Window owner;
    private JPanel tutorialDetailsContent;
    private JPanel loadingIndicator;
    private transient JList<TutorialGroup> tutorialGroupList;

    TutorialCard(Window owner) {
        this.owner = owner;
        BoxLayout layout = new BoxLayout(this, 1);
        this.setLayout(layout);
        this.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
        this.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        this.add(new CardHeaderPanel("getting_started.header.tutorials"));
        this.add(this.createExternalResourcePanel());
        this.add(Box.createVerticalStrut(30));
        this.loadingIndicator = this.createLoadingIndicator();
        this.loadingIndicator.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        this.add(this.loadingIndicator);
        this.loadTutorials();
    }

    private JPanel createLoadingIndicator() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = 1;
        gbc.weightx = 1.0D;
        gbc.weighty = 1.0D;
        JLabel loadingLabel = new JLabel(String.format("<html><span style=\"font-family: \'Open Sans Bold\';font-size: 15; color: #424242;\">%s</html>", new Object[]{TEXT_LOADING}), 0);
        panel.add(loadingLabel, gbc);
        return panel;
    }

    private void loadTutorials() {
        ProgressThread pThread = new ProgressThread("load_tutorials") {
            public void run() {
                final ArrayList tutorialGroups = new ArrayList(TutorialRegistry.INSTANCE.getAllTutorialGroups());
                Runnable updateTutorials = new Runnable() {
                    public void run() {
                        if(TutorialCard.this.loadingIndicator != null) {
                            TutorialCard.this.remove(TutorialCard.this.loadingIndicator);
                            TutorialCard.this.loadingIndicator = null;
                        }

                        TutorialCard.this.add(TutorialCard.this.createTutorialSelector(tutorialGroups));
                        if(TutorialCard.this.isVisible()) {
                            TutorialCard.this.tutorialGroupList.requestFocusInWindow();
                        }

                    }
                };
                SwingUtilities.invokeLater(updateTutorials);
            }
        };
        pThread.setIndeterminate(true);
        pThread.addDependency(new String[]{"load_tutorials"});
        pThread.start();
    }

    private JPanel createTutorialSelector(List<TutorialGroup> tutorialGroups) {
        this.tutorialGroupList = new TutorialEntryList(tutorialGroups);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = 17;
        JLabel detailsPanel = new JLabel(String.format("<html><span style=\"font-family: \'Open Sans Bold\';font-size: 15; color: #424242;\">%s</html>", new Object[]{TUTORIALS_HEADLINE}));
        panel.add(detailsPanel, gbc);
        ++gbc.gridy;
        gbc.weighty = 1.0D;
        gbc.weightx = 0.0D;
        gbc.fill = 3;
        gbc.insets = new Insets(10, 0, 0, 0);
        JScrollPane detailsPanel1 = new JScrollPane(this.tutorialGroupList, 20, 30);
        detailsPanel1.getVerticalScrollBar().setFocusable(false);
        detailsPanel1.getVerticalScrollBar().setBackground(GettingStartedDialog.BACKGROUND_COLOR);
        detailsPanel1.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Colors.TAB_BORDER));
        detailsPanel1.setMinimumSize(new Dimension(300, detailsPanel1.getMinimumSize().height));
        detailsPanel1.setPreferredSize(new Dimension(300, detailsPanel1.getPreferredSize().height));
        panel.add(detailsPanel1, gbc);
        ++gbc.gridx;
        gbc.weightx = 1.0D;
        gbc.fill = 1;
        final JPanel detailsPanel2 = new JPanel(new BorderLayout());
        detailsPanel2.setOpaque(false);
        this.tutorialDetailsContent = this.createDetailsView((TutorialGroup)null);
        detailsPanel2.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Colors.TAB_BORDER));
        detailsPanel2.add(this.tutorialDetailsContent, "Center");
        panel.add(detailsPanel2, gbc);
        if(this.tutorialGroupList != null) {
            this.tutorialGroupList.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if(!e.getValueIsAdjusting()) {
                        ProgressThread thread = new ProgressThread("load_tutorial") {
                            public void run() {
                                final List selectionValues = TutorialCard.this.tutorialGroupList.getSelectedValuesList();
                                if(!selectionValues.isEmpty()) {
                                    Runnable updateTutorialDetails = new Runnable() {
                                        public void run() {
                                            TutorialCard.this.tutorialDetailsContent = TutorialCard.this.createDetailsView((TutorialGroup)selectionValues.get(0));
                                        }
                                    };

                                    try {
                                        SwingUtilities.invokeAndWait(updateTutorialDetails);
                                    } catch (InterruptedException | InvocationTargetException var4) {
                                        SwingUtilities.invokeLater(updateTutorialDetails);
                                    }

                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            detailsPanel2.removeAll();
                                            ExtendedJScrollPane scrollPane = new ExtendedJScrollPane(TutorialCard.this.tutorialDetailsContent);
                                            scrollPane.setVerticalScrollBarPolicy(20);
                                            scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Colors.TAB_BORDER));
                                            scrollPane.getVerticalScrollBar().setFocusable(false);
                                            detailsPanel2.setBorder((Border)null);
                                            detailsPanel2.add(scrollPane, "Center");
                                            detailsPanel2.revalidate();
                                            detailsPanel2.repaint();
                                        }
                                    });
                                }

                            }
                        };
                        thread.setIndeterminate(true);
                        thread.addDependency(new String[]{"load_tutorial"});
                        thread.start();
                    }

                }
            });
        }

        if(this.tutorialGroupList != null && this.tutorialGroupList.getModel().getSize() > 0) {
            this.tutorialGroupList.setSelectedIndex(0);
        }

        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        return panel;
    }

    private JPanel createDetailsView(TutorialGroup tutorialGroup) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
        if(tutorialGroup == null) {
            return panel;
        } else {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = 2;
            gbc.weightx = 1.0D;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(10, 10, 20, 10);
            JLabel label = new JLabel(String.format("<html><div style=\"font-family: \'Open Sans\'; font-size: 13; color: #424242; width: 300;\">%s</div></html>", new Object[]{tutorialGroup.getDescription()}));
            panel.add(label, gbc);
            ++gbc.gridy;
            gbc.insets = new Insets(0, 10, 10, 10);
            label = new JLabel();
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GettingStartedDialog.VERY_LIGHT_GRAY));
            panel.add(label, gbc);
            gbc.gridwidth = 1;
            int counter = 1;
            Iterator var6 = tutorialGroup.getTutorials().iterator();

            while(var6.hasNext()) {
                final Tutorial tutorial = (Tutorial)var6.next();
                ++gbc.gridy;
                gbc.weightx = 0.0D;
                gbc.gridx = 0;
                gbc.anchor = 17;
                gbc.fill = 0;
                gbc.insets = new Insets(0, 12, 0, 0);
                if(TutorialManager.INSTANCE.hasCompletedTutorial(tutorial.getIdentifier())) {
                    panel.add(new JLabel(TUTORIAL_COMPLETED_TEXT), gbc);
                } else {
                    panel.add(new JLabel(TUTORIAL_PENDING_TEXT), gbc);
                }

                AbstractAction action = new AbstractAction() {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        TutorialCard.this.openTutorial(tutorial);
                    }
                };
                action.putValue("SwingLargeIconKey", (Object)null);
                action.putValue("SmallIcon", (Object)null);
                action.putValue("ActionCommandKey", "tutorial-" + tutorial.getIdentifier());
                action.putValue("Name", counter++ + ". " + tutorial.getTitle());
                action.putValue("ShortDescription", tutorial.getDescription());
                action.putValue("isLinkBold", Boolean.TRUE);
                ++gbc.gridx;
                gbc.insets = new Insets(0, 5, 0, 0);
                final LinkLocalButton tutorialLinkButton = new LinkLocalButton(action);
                HTMLEditorKit htmlKit = (HTMLEditorKit)tutorialLinkButton.getEditorKit();
                htmlKit.getStyleSheet().addRule("a {font-family:\'Open Sans\';font-size:13;}");
                final JLabel tutorialDescriptionLabel = new JLabel(String.format("<html><div style=\"font-family: \'Open Sans\'; font-size: 13; color: #424242; width: 300;\">%s</div></html>", new Object[]{tutorial.getDescription()}));
                tutorialLinkButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                tutorialLinkButton.addFocusListener(new FocusListener() {
                    public void focusLost(FocusEvent e) {
                        tutorialLinkButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                    }

                    public void focusGained(FocusEvent e) {
                        tutorialLinkButton.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
                        if(TutorialCard.this.tutorialDetailsContent != null) {
                            Rectangle desiredView = tutorialLinkButton.getBounds((Rectangle)null).union(tutorialDescriptionLabel.getBounds((Rectangle)null));
                            TutorialCard.this.tutorialDetailsContent.scrollRectToVisible(desiredView);
                            TutorialCard.this.tutorialDetailsContent.revalidate();
                            TutorialCard.this.tutorialDetailsContent.repaint();
                        }

                    }
                });
                tutorialLinkButton.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        if(e.getKeyCode() == 10) {
                            TutorialCard.this.openTutorial(tutorial);
                        }

                    }
                });
                panel.add(tutorialLinkButton, gbc);
                ++gbc.gridy;
                gbc.insets = new Insets(2, 5, 15, 0);
                panel.add(tutorialDescriptionLabel, gbc);
            }

            ++gbc.gridy;
            gbc.weighty = 1.0D;
            gbc.fill = 1;
            panel.add(new JLabel(), gbc);
            return panel;
        }
    }

    private JComponent createExternalResourcePanel() {
        JPanel resourcePanel = new JPanel(new FlowLayout(0, 0, 0));
        resourcePanel.setOpaque(false);
        JLabel externalResourceLabel = new JLabel(String.format("<html><span style=\"font-family: \'Open Sans Bold\';font-size: 15; color: #424242;\">%s</html>", new Object[]{EXTERNAL_RESOURCES_HEADLINE}));
        resourcePanel.add(externalResourceLabel);
        resourcePanel.add(Box.createHorizontalStrut(30));
        LinkRemoteButton documentationButton = new LinkRemoteButton(new ResourceAction(false, "getting_started.tutorial.documentation", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                try {
                    RMUrlHandler.browse(new URI(TutorialCard.DOCUMENTATION_URL));
                } catch (Exception var3) {
                    SwingTools.showSimpleErrorMessage("cannot_open_browser_url", var3, new Object[]{TutorialCard.DOCUMENTATION_URL});
                }

            }
        });
        documentationButton.setFocusable(false);
        resourcePanel.add(documentationButton);
        LinkRemoteButton videoButton = new LinkRemoteButton(new ResourceAction(false, "getting_started.tutorial.video", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                try {
                    RMUrlHandler.browse(new URI(TutorialCard.VIDEO_URL));
                } catch (Exception var3) {
                    SwingTools.showSimpleErrorMessage("cannot_open_browser_url", var3, new Object[]{TutorialCard.VIDEO_URL});
                }

            }
        });
        videoButton.setFocusable(false);
        resourcePanel.add(videoButton);
        LinkRemoteButton supportButton = new LinkRemoteButton(new ResourceAction(false, "getting_started.tutorial.support", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                try {
                    RMUrlHandler.browse(new URI(TutorialCard.SUPPORT_URL));
                } catch (Exception var3) {
                    SwingTools.showSimpleErrorMessage("cannot_open_browser_url", var3, new Object[]{TutorialCard.SUPPORT_URL});
                }

            }
        });
        supportButton.setFocusable(false);
        resourcePanel.add(supportButton);
        LinkRemoteButton trainingButton = new LinkRemoteButton(new ResourceAction(false, "getting_started.tutorial.training", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                try {
                    RMUrlHandler.browse(new URI(TutorialCard.TRAINING_URL));
                } catch (Exception var3) {
                    SwingTools.showSimpleErrorMessage("cannot_open_browser_url", var3, new Object[]{TutorialCard.TRAINING_URL});
                }

            }
        });
        trainingButton.setFocusable(false);
        resourcePanel.add(trainingButton);
        resourcePanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        return resourcePanel;
    }

    private void openTutorial(final Tutorial tutorial) {
        this.owner.dispose();
        if(RapidMinerGUI.getMainFrame().close()) {
            (new ProgressThread("open_tutorial") {
                public void run() {
                    try {
                        GettingStartedDialog.logStats("tutorial_open", tutorial.getIdentifier());
                        MainFrame e = RapidMinerGUI.getMainFrame();
                        Process tutorialProcess = tutorial.makeProcess();
                        e.setOpenedProcess(tutorialProcess, false, (String)null);
                        e.getTutorialSelector().setSelectedTutorial(tutorial);
                        TutorialManager.INSTANCE.completedTutorial(tutorial.getIdentifier());
                        DockingTools.openDockable("tutorial_browser", (String)null, TutorialBrowser.POSITION);
                    } catch (MalformedRepositoryLocationException | IOException | XMLException | RuntimeException var3) {
                        SwingTools.showSimpleErrorMessage("cannot_open_tutorial", var3, new Object[]{tutorial.getTitle(), var3.getMessage()});
                    }

                }
            }).start();
        }
    }

    public boolean requestFocusInWindow() {
        return this.tutorialGroupList != null?this.tutorialGroupList.requestFocusInWindow():super.requestFocusInWindow();
    }

    static {
        TUTORIAL_PENDING_TEXT = "<html><div style=\"width: 10;\">" + Ionicon.ARROW_RIGHT_B.getHtml() + "</div></html>";
        TUTORIAL_COMPLETED_TEXT = "<html><div style=\"color: #00BB58; width: 10;\">" + Ionicon.CHECKMARK_ROUND.getHtml() + "</div></html>";
        TUTORIALS_HEADLINE = I18N.getGUILabel("getting_started.tutorials.tutorials_headline", new Object[0]);
        EXTERNAL_RESOURCES_HEADLINE = I18N.getGUILabel("getting_started.tutorials.ext_resources_headline", new Object[0]);
        TEXT_LOADING = I18N.getGUILabel("getting_started.tutorials.loading", new Object[0]);
        DOCUMENTATION_URL = I18N.getGUILabel("getting_started.tutorials.ext_resources.documentation_url", new Object[0]);
        SUPPORT_URL = I18N.getGUILabel("getting_started.tutorials.ext_resources.support_url", new Object[0]);
        TRAINING_URL = I18N.getGUILabel("getting_started.tutorials.ext_resources.training_url", new Object[0]);
        VIDEO_URL = I18N.getGUILabel("getting_started.tutorials.ext_resources.video_url", new Object[0]);
    }
}
