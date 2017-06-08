package com.rapidminer.gui.startup;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.look.Colors;
import com.rapidminer.gui.startup.CardHeaderPanel;
import com.rapidminer.gui.startup.GettingStartedDialog;
import com.rapidminer.gui.tools.Ionicon;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.ResourceLabel;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.components.LinkLocalButton;
import com.rapidminer.studio.internal.StartupDialogProvider.ToolbarButton;
import com.rapidminer.tools.I18N;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.print.DocFlavor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class GettingStartedCard extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final List<String> CARDS = new ArrayList();
    private static final String NAVIGATION_CHEVRON;
    private static final String NEXT_STEP_GLYPH = "<html><span style=\"font-size: 18;\">%s&nbsp;</span></html>";
    private static final String NEXT_GLYPH = "<html>%s&nbsp;</span></html>";
    private static final int GAP = 14;
    private static final Insets EMPTY_INSETS;
    private static final Color DESCRIPTION_BG;
    private JPanel steps;

    public GettingStartedCard(GettingStartedDialog parent) {
        this.setLayout(new BorderLayout());
        this.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
        this.steps = new JPanel(new CardLayout());

        for(int i = 0; i < CARDS.size(); ++i) {
            boolean isLast = i == CARDS.size() - 1;
            String cardKey = (String)CARDS.get(i);
            this.steps.add(new GettingStartedCard.StepPanel(parent, cardKey, isLast), cardKey);
        }

        this.add(this.steps, "Center");
    }

    private void showNextStep(String key) {
        int i = CARDS.indexOf(key) + 1;
        ((CardLayout)this.steps.getLayout()).show(this.steps, (String)CARDS.get(i < CARDS.size()?i:0));
    }

    static {
        CARDS.add("import_data");
        CARDS.add("build_process");
        CARDS.add("run_process");
        CARDS.add("inspect_results");
        CARDS.add("improve_process");
        NAVIGATION_CHEVRON = String.format("<html><span style=\"color: #999999;\">&nbsp;<%s&nbsp;</span></html>", new Object[]{Ionicon.CHEVRON_RIGHT.getHtml()});
        EMPTY_INSETS = new Insets(0, 0, 0, 0);
        DESCRIPTION_BG = new Color(243, 112, 24, 15);
    }

    private class StepPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        public StepPanel(final GettingStartedDialog parent, final String key, boolean last) {
            this.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
            JPanel outerPanel = new JPanel(new GridBagLayout());
            outerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 14, 20));
            outerPanel.setOpaque(false);
            this.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.anchor = 18;
            constraints.fill = 2;
            constraints.weightx = 1.0D;
            constraints.weighty = 0.0D;
            FlowLayout innerPanel = new FlowLayout(0);
            innerPanel.setHgap(0);
            innerPanel.setVgap(0);
            JPanel gbc = new JPanel(innerPanel);
            gbc.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
            Border next = BorderFactory.createEmptyBorder(0, 7, 0, 7);


            for(int glyph = 0; glyph < GettingStartedCard.CARDS.size(); ++glyph) {
                boolean i = glyph == GettingStartedCard.CARDS.size() - 1;
                final String i18nKey = GettingStartedCard.CARDS.get(glyph);
                String button = i18nKey.equals(key)?"getting_starter.step_active":"getting_starter.step";
                LinkLocalButton glyph1 = new LinkLocalButton(new ResourceAction(button, new Object[]{glyph + 1 + ". " + I18N.getGUIMessage("gui.getting_started.step." + i18nKey + ".title", new Object[0])}) {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {

                        ((CardLayout)GettingStartedCard.this.steps.getLayout()).show(GettingStartedCard.this.steps, i18nKey);
                    }
                });
                glyph1.setMargin(GettingStartedCard.EMPTY_INSETS);
                gbc.add(glyph1, constraints);
                if(!i) {
                    JLabel button1 = new JLabel(GettingStartedCard.NAVIGATION_CHEVRON);
                    button1.setBorder(next);
                    SwingTools.disableClearType(button1);
                    gbc.add(button1);
                }
            }

            constraints.insets = new Insets(0, 10, 10, 0);
            outerPanel.add(gbc, constraints);
            JPanel var16 = new JPanel(new GridBagLayout());
            constraints.insets = new Insets(0, 0, 0, 0);
            constraints.fill = 0;
            ImageIcon var17 = SwingTools.createImage(I18N.getGUIMessage("gui.getting_started.step." + key + ".animation", new Object[0]));
            JLabel var19 = new JLabel(var17);
            var19.setBorder(BorderFactory.createLineBorder(Colors.PANEL_BORDER));
            ++constraints.gridy;
            var16.add(var19, constraints);
            ++constraints.gridx;
            constraints.weightx = 1.0D;
            constraints.fill = 2;
            this.add(new JLabel(), constraints);
            --constraints.gridx;
            constraints.weightx = 1.0D;
            constraints.insets = new Insets(14, 14, 14, 0);
            constraints.fill = 2;
            ++constraints.gridy;
            var16.add(new JLabel(I18N.getGUIMessage("gui.getting_started.step." + key + ".description", new Object[0])), constraints);
            constraints.insets = new Insets(14, 14, 0, 0);
            if(last) {
                ++constraints.gridy;
                outerPanel.add(new ResourceLabel("getting_started.next_steps", new Object[0]), constraints);
                gbc = new JPanel(new GridBagLayout());
                gbc.setBackground(GettingStartedDialog.BACKGROUND_COLOR);
                GridBagConstraints var21 = new GridBagConstraints();
                var21.fill = 2;
                var21.anchor = 17;
                var21.gridx = 0;
                var21.gridy = 0;
                var21.weightx = 0.0D;
                Ionicon[] var23 = new Ionicon[]{Ionicon.ANDROID_ADD_CIRCLE, Ionicon.UNIVERSITY, Ionicon.ANDROID_REFRESH};
                ResourceAction[] var25 = new ResourceAction[]{new ResourceAction("getting_starter.build", new Object[0]) {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        parent.showCard(ToolbarButton.NEW_PROCESS);
                    }
                }, new ResourceAction("getting_starter.keep_learning", new Object[0]) {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        parent.showCard(ToolbarButton.TUTORIAL);
                    }
                }, new ResourceAction("getting_starter.repeat", new Object[0]) {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        GettingStartedCard.this.showNextStep(key);
                    }
                }};

                for(int var27 = 0; var27 < var23.length; ++var27) {
                    boolean var28 = var27 == var23.length - 1;
                    JLabel var30 = new JLabel(String.format("<html><span style=\"font-size: 18;\">%s&nbsp;</span></html>", new Object[]{var23[var27].getHtml()})) {
                        private static final long serialVersionUID = 1L;

                        public void paintComponent(Graphics g) {
                            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            super.paintComponent(g);
                        }
                    };
                    SwingTools.disableClearType(var30);
                    var30.setForeground(Colors.LINKBUTTON_LOCAL);
                    ++var21.gridx;
                    var21.weightx = 0.0D;
                    gbc.add(var30, var21);
                    LinkLocalButton var31 = new LinkLocalButton(var25[var27]);
                    var31.setMargin(GettingStartedCard.EMPTY_INSETS);
                    ++var21.gridx;
                    gbc.add(var31, var21);
                    if(!var28) {
                        var21.weightx = 1.0D;
                        gbc.add(Box.createVerticalGlue(), var21);
                    }
                }

                ++constraints.gridy;
                constraints.insets = new Insets(0, 14, 0, 0);
                outerPanel.add(gbc, constraints);
            } else {
                FlowLayout var18 = new FlowLayout(0);
                var18.setHgap(0);
                var18.setVgap(0);
                JPanel var22 = new JPanel(var18);
                var22.setOpaque(false);
                JLabel var24 = new JLabel(String.format("<html>%s&nbsp;</span></html>", new Object[]{Ionicon.CHEVRON_RIGHT.getHtml()}));
                var24.setForeground(Colors.LINKBUTTON_LOCAL);
                SwingTools.disableClearType(var24);
                var22.add(var24);
                int var26 = GettingStartedCard.CARDS.indexOf(key) + 1;
                String i18nKey = GettingStartedCard.CARDS.get(var26);
                LinkLocalButton var29 = new LinkLocalButton(new ResourceAction("getting_starter.next", new Object[]{I18N.getGUIMessage("gui.getting_started.step." + i18nKey + ".title", new Object[0])}) {
                    private static final long serialVersionUID = 1L;

                    public void actionPerformed(ActionEvent e) {
                        GettingStartedCard.this.showNextStep(key);
                    }
                });
                var29.setMargin(GettingStartedCard.EMPTY_INSETS);
                var22.add(var29);
                ++constraints.gridy;
                constraints.insets = new Insets(0, 14, 14, 0);
                var16.add(var22, constraints);
            }

            GridBagConstraints var20 = new GridBagConstraints();
            var20.gridx = 0;
            var20.gridy = 0;
            var20.weightx = 1.0D;
            var20.fill = 2;
            this.add(new CardHeaderPanel("getting_started.header.getstarted"), var20);
            ++var20.gridy;
            var20.weightx = 0.0D;
            var20.fill = 0;
            outerPanel.add(var16, var20);
            var20.gridy = 100;
            var20.weightx = 1.0D;
            var20.weighty = 1.0D;
            var20.fill = 1;
            outerPanel.add(new JLabel(), var20);
            var16.setOpaque(true);
            var16.setBackground(GettingStartedCard.DESCRIPTION_BG);
            this.add(outerPanel, var20);
        }
    }
}
