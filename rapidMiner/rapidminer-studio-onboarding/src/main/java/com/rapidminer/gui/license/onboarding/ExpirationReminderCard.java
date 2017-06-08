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
import javax.swing.JButton;
import javax.swing.JPanel;

public class ExpirationReminderCard extends AbstractCard {
    public static final String CARD_ID = "expiration_reminder";
    private static final String GET_MORE_EDITIONS_URL = I18N.getGUILabel("onboarding.more_editions.url", new Object[0]);
    private JButton btPurchase;
    private LinkLocalButton btContinue;
    private ResourceLabel lblExpiration1;
    private ResourceLabel lblExpiration2;
    private ReminderType type;

    public ExpirationReminderCard(OnboardingDialog onboardingDialog, ReminderType type) {
        super("expiration_reminder", onboardingDialog);
        this.type = ReminderType.REMINDER;
        this.type = type;
    }

    public JPanel getHeader() {
        return this.type == ReminderType.EXPIRED?OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.license_expired", new Object[0])):OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.license_reminder", new Object[0]));
    }

    public JPanel getContent() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = 10;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 5, 0);
        this.lblExpiration1 = new ResourceLabel("", new Object[0]);
        this.lblExpiration1.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lblExpiration1, gbc);
        gbc.insets = new Insets(0, 5, 50, 5);
        ++gbc.gridy;
        this.lblExpiration2 = new ResourceLabel("", new Object[0]);
        this.lblExpiration2.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lblExpiration2, gbc);
        this.btPurchase = new JButton(new ResourceAction("onboarding.purchase", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                try {
                    RMUrlHandler.browse(new URI(ExpirationReminderCard.GET_MORE_EDITIONS_URL));
                } catch (URISyntaxException | IOException var3) {
                    ;
                }

                ExpirationReminderCard.this.getContainer().dispose();
            }
        });
        this.btPurchase.putClientProperty("com.rapidminer.ui.button.type", "cfa");
        ++gbc.gridy;
        gbc.insets = new Insets(5, 5, 10, 5);
        panel.add(this.btPurchase, gbc);
        JPanel continueTrialPanel = new JPanel();
        continueTrialPanel.setOpaque(false);
        continueTrialPanel.setLayout(new BorderLayout());
        this.btContinue = new LinkLocalButton(new ResourceAction("onboarding.continue", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                ExpirationReminderCard.this.getContainer().dispose();
            }
        });
        this.btContinue.setFocusable(false);
        continueTrialPanel.add(this.btContinue, "Center");
        ++gbc.gridy;
        gbc.insets = new Insets(0, 5, 30, 5);
        panel.add(continueTrialPanel, gbc);
        return panel;
    }

    public void showCard() {
        String oldProductEdition = (String)this.getContainer().getSharedObject("com.rapidminer.onboarding.license.old.edition");
        String upcomingProductEdition = (String)this.getContainer().getSharedObject("com.rapidminer.onboarding.license.upcoming.edition");
        if(this.type == ReminderType.EXPIRED) {
            this.lblExpiration1.setText(I18N.getGUILabel("onboarding.license_expired_info1", new Object[]{oldProductEdition}));
            this.lblExpiration2.setText(I18N.getGUILabel("onboarding.license_expired_info2", new Object[]{upcomingProductEdition}));
        } else {
            String dateString = DateFormat.getDateInstance(1).format(this.getContainer().getSharedObject("com.rapidminer.onboarding.license.expiration"));
            this.lblExpiration1.setText(I18N.getGUILabel("onboarding.license_reminder_info1", new Object[]{oldProductEdition, dateString}));
            this.lblExpiration2.setText(I18N.getGUILabel("onboarding.license_reminder_info2", new Object[]{upcomingProductEdition}));
        }

        this.btPurchase.requestFocusInWindow();
        this.getContainer().getRootPane().setDefaultButton(this.btPurchase);
    }

    public Runnable getCloseAction() {
        return this.standardCloseAction;
    }
}
