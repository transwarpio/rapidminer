package com.rapidminer.gui.license.onboarding;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.license.onboarding.AbstractCard;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.license.onboarding.OnboardingManager.ReminderType;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.ResourceLabel;
import com.rapidminer.gui.tools.components.LinkLocalButton;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.RMUrlHandler;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class TrialReminderCard extends AbstractCard {
    public static final String CARD_ID = "trial_reminder";
    private static final String GET_MORE_EDITIONS_URL = I18N.getGUILabel("onboarding.more_editions.url", new Object[0]);
    private static final String REQUEST_EXTENSION_URL = I18N.getGUILabel("onboarding.extend_trial.url", new Object[0]);
    private JButton btPurchase;
    private JButton btExtend;
    private LinkLocalButton btContinueTrial;
    private ResourceLabel lblExpiration;
    private ReminderType type;

    public TrialReminderCard(OnboardingDialog onboardingDialog, ReminderType type) {
        super("trial_reminder", onboardingDialog);
        this.type = ReminderType.REMINDER;
        this.type = type;
    }

    public JPanel getHeader() {
        return this.type == ReminderType.EXPIRED?OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.trial_expired", new Object[0])):OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.trial_reminder", new Object[0]));
    }

    public JPanel getContent() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = 10;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 0, 0);
        this.lblExpiration = new ResourceLabel("", new Object[0]);
        this.lblExpiration.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lblExpiration, gbc);
        gbc.insets = new Insets(0, 5, 30, 5);
        ++gbc.gridy;
        if(this.type == ReminderType.EXPIRED) {
            panel.add(OnboardingDialog.createLabel("onboarding.trial_expired_info2", "normal"), gbc);
        } else {
            panel.add(OnboardingDialog.createLabel("onboarding.trial_reminder_info2", "normal"), gbc);
        }

        gbc.insets = new Insets(0, 5, 15, 5);
        ++gbc.gridy;
        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new GridBagLayout());
        GridBagConstraints listGBC = new GridBagConstraints();
        listGBC.anchor = 17;
        listGBC.gridx = 0;
        listGBC.gridy = 0;
        listGBC.insets = new Insets(0, 5, 0, 5);
        listPanel.add(OnboardingDialog.createLabel("onboarding.trial_reminder_info3", "normal"), listGBC);
        ++listGBC.gridy;
        listPanel.add(OnboardingDialog.createLabel("onboarding.trial_reminder_info4", "normal"), listGBC);
        ++listGBC.gridy;
        listPanel.add(OnboardingDialog.createLabel("onboarding.trial_reminder_info5", "normal"), listGBC);
        ++listGBC.gridy;
        listPanel.add(OnboardingDialog.createLabel("onboarding.trial_reminder_info6", "normal"), listGBC);
        ++listGBC.gridy;
        listPanel.add(OnboardingDialog.createLabel("onboarding.trial_reminder_info7", "normal"), listGBC);
        panel.add(listPanel, gbc);
        JPanel continueTrialPanel = new JPanel();
        continueTrialPanel.setLayout(new BoxLayout(continueTrialPanel, 0));
        continueTrialPanel.setOpaque(false);
        this.btPurchase = new JButton(new ResourceAction("onboarding.purchase", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                try {
                    RMUrlHandler.browse(new URI(TrialReminderCard.GET_MORE_EDITIONS_URL));
                } catch (URISyntaxException | IOException var3) {
                    ;
                }

                TrialReminderCard.this.getContainer().dispose();
            }
        });
        this.btPurchase.putClientProperty("com.rapidminer.ui.button.type", "cfa");
        continueTrialPanel.add(this.btPurchase);
        continueTrialPanel.add(Box.createHorizontalStrut(10));
        continueTrialPanel.add(OnboardingDialog.createLabel("onboarding.trial_reminder_info8", "normal"));
        continueTrialPanel.add(Box.createHorizontalStrut(10));
        this.btExtend = new JButton(new ResourceAction("onboarding.extend_trial", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                try {
                    RMUrlHandler.browse(new URI(TrialReminderCard.REQUEST_EXTENSION_URL));
                } catch (URISyntaxException | IOException var3) {
                    ;
                }

                TrialReminderCard.this.getContainer().dispose();
            }
        });
        this.btExtend.putClientProperty("com.rapidminer.ui.button.type", "cfa");
        continueTrialPanel.add(this.btExtend);
        ++gbc.gridy;
        gbc.insets = new Insets(20, 5, 5, 5);
        panel.add(continueTrialPanel, gbc);
        continueTrialPanel = new JPanel();
        continueTrialPanel.setOpaque(false);
        continueTrialPanel.setLayout(new BorderLayout());
        final String i18N = this.type == ReminderType.EXPIRED?"onboarding.continue_community":"onboarding.continue_trial";
        this.btContinueTrial = new LinkLocalButton(new ResourceAction(i18N, new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                TrialReminderCard.this.getContainer().dispose();
            }
        });
        this.btContinueTrial.setFocusable(false);
        continueTrialPanel.add(this.btContinueTrial, "Center");
        ++gbc.gridy;
        gbc.insets = new Insets(0, 5, 30, 5);
        panel.add(continueTrialPanel, gbc);
        return panel;
    }

    public void showCard() {
        if(this.type == ReminderType.EXPIRED) {
            this.lblExpiration.setText(I18N.getGUILabel("onboarding.trial_expired_info1", new Object[0]));
        } else {
            String dateString = DateFormat.getDateInstance(1).format(this.getContainer().getSharedObject("com.rapidminer.onboarding.license.expiration"));
            this.lblExpiration.setText(I18N.getGUILabel("onboarding.trial_reminder_info1", new Object[]{dateString}));
        }

        this.btPurchase.requestFocusInWindow();
        this.getContainer().getRootPane().setDefaultButton(this.btPurchase);
    }

    public Runnable getCloseAction() {
        return this.standardCloseAction;
    }
}