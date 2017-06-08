package com.rapidminer.gui.license.onboarding;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.license.onboarding.OnboardingManager.WelcomeType;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.license.License;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.nexus.NexusConnectionManager;
import org.apache.commons.lang.WordUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ActivationSuccessfulCard extends AbstractCard {
    public static final String CARD_ID = "activation_successful";
    private static final String NA = "-";
    private static final String LICENSE_INSTALLED = I18N.getGUILabel("onboarding.license_installed.label", new Object[0]);
    private static final Color CUSTOM_GREEN = new Color(27, 173, 75);
    private JLabel lbRegisteredToResult;
    private JLabel lbExpiresResult;
    private JLabel lbStatus;
    private JLabel lbStatusIcon;
    private JLabel lbAdditionalInfo;
    private JButton btFinish;

    public ActivationSuccessfulCard(OnboardingDialog owner) {
        super("activation_successful", owner);
    }

    public JPanel getHeader() {
        return OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.activation_successful", new Object[0]));
    }

    public JPanel getContent() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, 1));
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        this.lbStatusIcon = new JLabel(OnboardingDialog.getIcon("onboarding.license_installed"));
        this.lbStatusIcon.setAlignmentX(0.5F);
        panel.add(this.lbStatusIcon);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.lbStatus = OnboardingDialog.createLabel("onboarding.license_installed", "bold", true);
        this.lbStatus.putClientProperty("com.rapidminer.ui.label.foreground", CUSTOM_GREEN);
        this.lbStatus.setIcon((Icon)null);
        panel.add(this.lbStatus);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(this.createLicenseDetailPanel());
        panel.add(OnboardingDialog.createLabel("onboarding.change_license1", "normal", true));
        panel.add(OnboardingDialog.createLabel("onboarding.change_license2", "bold", true));
        panel.add(Box.createVerticalGlue());
        this.btFinish = new JButton(new ResourceAction("onboarding.finish", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                if(ActivationSuccessfulCard.this.getContainer().getWelcomeType() == WelcomeType.FIRST_WELCOME) {
                    ActivationSuccessfulCard.this.getContainer().dispose();
                } else {
                    ActivationSuccessfulCard.this.getContainer().dispose();
                    Runnable doAfterSuccess = ActivationSuccessfulCard.this.getContainer().getAfterSuccessAction();
                    if(doAfterSuccess != null) {
                        doAfterSuccess.run();
                    }
                }

            }
        });
        this.btFinish.putClientProperty("com.rapidminer.ui.button.type", "normal");
        panel.add(OnboardingDialog.createButtonPanel(new JButton[]{this.btFinish}));
        return panel;
    }

    public void showCard() {
        this.fillContent(this.getLicenseOwner(), this.getLicenseEdition(), this.getLicenseExpirationDate(), this.getAdditionalLicenseInformation());
        this.btFinish.requestFocusInWindow();
        this.getContainer().getRootPane().setDefaultButton(this.btFinish);
    }

    public Runnable getCloseAction() {
        return this.getContainer().getWelcomeType() == WelcomeType.FIRST_WELCOME?this.standardCloseAction:new Runnable() {
            public void run() {
                ActivationSuccessfulCard.this.getContainer().dispose();
                Runnable doAfterSuccess = ActivationSuccessfulCard.this.getContainer().getAfterSuccessAction();
                if(doAfterSuccess != null) {
                    doAfterSuccess.run();
                }

            }
        };
    }

    public static String getRemaingTrialDaysMessage(License activeLicense) {
        int remainingDays = NexusConnectionManager.getRemainingDays(activeLicense);
        String additionalInfoMessage;
        if(remainingDays > 0) {
            additionalInfoMessage = I18N.getGUILabel("onboarding.trial_information", new Object[]{Integer.valueOf(remainingDays)});
        } else {
            additionalInfoMessage = I18N.getGUILabel("onboarding.trial_information_expires_today", new Object[0]);
        }

        return additionalInfoMessage;
    }

    private JPanel createLicenseDetailPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = 17;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0D;
        gbc.fill = 2;
        panel.add(Box.createHorizontalGlue(), gbc);
        ++gbc.gridx;
        gbc.weightx = 0.0D;
        panel.add(OnboardingDialog.createLabel("onboarding.registered_to", "bold"), gbc);
        ++gbc.gridx;
        this.lbRegisteredToResult = new JLabel("-");
        this.lbRegisteredToResult.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lbRegisteredToResult, gbc);
        ++gbc.gridx;
        gbc.weightx = 1.0D;
        panel.add(Box.createHorizontalGlue(), gbc);
        gbc.gridx = 1;
        ++gbc.gridy;
        gbc.weightx = 0.0D;
        panel.add(OnboardingDialog.createLabel("onboarding.expires", "bold"), gbc);
        ++gbc.gridx;
        this.lbExpiresResult = new JLabel("-");
        this.lbExpiresResult.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lbExpiresResult, gbc);
        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.gridwidth = 4;
        gbc.weightx = 0.0D;
        gbc.fill = 0;
        gbc.anchor = 10;
        this.lbAdditionalInfo = new JLabel(" ");
        this.lbAdditionalInfo.putClientProperty("com.rapidminer.ui.label.type", "normal");
        panel.add(this.lbAdditionalInfo, gbc);
        gbc.gridx = 1;
        ++gbc.gridy;
        gbc.gridwidth = 2;
        gbc.fill = 2;
        gbc.insets = new Insets(15, -125, 5, -125);
        JSeparator separator = new JSeparator();
        separator.setForeground(OnboardingDialog.LIGHTER_GRAY);
        separator.setBackground(OnboardingDialog.LIGHTER_GRAY);
        panel.add(separator, gbc);
        panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));
        return panel;
    }

    private String getLicenseEdition() {
        return (String)this.getContainer().getSharedObject("com.rapidminer.onboarding.license.edition");
    }

    private String getLicenseExpirationDate() {
        return (String)this.getContainer().getSharedObject("com.rapidminer.onboarding.license.expiration");
    }

    private String getAdditionalLicenseInformation() {
        return (String)this.getContainer().getSharedObject("com.rapidminer.onboarding.license.additional_info");
    }

    private String getLicenseOwner() {
        return (String)this.getContainer().getSharedObject("com.rapidminer.onboarding.license.owner");
    }

    private void fillContent(String owner, String edition, String expires, String additionalInformation) {
        if(owner != null) {
            this.lbRegisteredToResult.setText(owner);
        } else {
            this.lbRegisteredToResult.setText("-");
        }

        if(edition != null) {
            this.lbStatus.setText(WordUtils.capitalize(edition) + " " + LICENSE_INSTALLED);
        } else {
            this.lbStatus.setText("- " + LICENSE_INSTALLED);
        }

        if(expires != null) {
            this.lbExpiresResult.setText(expires);
        } else {
            this.lbExpiresResult.setText("-");
        }

        if(additionalInformation != null && additionalInformation.trim().isEmpty()) {
            this.lbAdditionalInfo.setText((String)null);
        } else {
            this.lbAdditionalInfo.setText(additionalInformation);
        }

    }
}
