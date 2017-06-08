package com.rapidminer.gui.license.onboarding;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.license.onboarding.AbstractCard;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.license.onboarding.OnboardingManager;
import com.rapidminer.gui.license.onboarding.OnboardingManager.WelcomeType;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.components.LinkLocalButton;
import com.rapidminer.gui.tools.components.LinkRemoteButton;
import com.rapidminer.tools.I18N;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class WelcomeCard extends AbstractCard {
    public static final String CARD_WELCOME_ID = "welcome";
    public static final String KEY_WELCOME_BACK = "onboarding.back_welcome";
    private static final String GET_COMMUNITY_INFO_URL = I18N.getGUILabel("onboarding.community_info.url", new Object[0]);
    private JButton btSignup;
    private JButton btLogin;
    private LinkLocalButton btNoThanks;
    private LinkRemoteButton btInfo;
    private final WelcomeType type;

    public WelcomeCard(OnboardingDialog onboardingDialog, WelcomeType type) {
        super("welcome", onboardingDialog);
        this.type = type;
    }

    public JPanel getHeader() {
        return this.type == WelcomeType.WELCOME_REMINDER?OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.welcome_reminder", new Object[0])):(this.type == WelcomeType.COMMUNITY_FEATURE?OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.community_feature", new Object[0])):OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.welcome", new Object[0])));
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
        panel.add(OnboardingDialog.createLabel("onboarding.welcome_info1", "normal"), gbc);
        gbc.insets = new Insets(0, 5, 25, 5);
        ++gbc.gridy;
        panel.add(OnboardingDialog.createLabel("onboarding.welcome_info2", "normal"), gbc);
        gbc.insets = new Insets(5, 5, 0, 5);
        ++gbc.gridy;
        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new GridBagLayout());
        GridBagConstraints listGBC = new GridBagConstraints();
        listGBC.anchor = 17;
        listGBC.gridx = 0;
        listGBC.gridy = 0;
        listGBC.insets = new Insets(0, 5, 0, 5);
        listPanel.add(OnboardingDialog.createLabel("onboarding.welcome_info3", "normal"), listGBC);
        ++listGBC.gridy;
        listPanel.add(OnboardingDialog.createLabel("onboarding.welcome_info4", "normal"), listGBC);
        ++listGBC.gridy;
        listPanel.add(OnboardingDialog.createLabel("onboarding.welcome_info5", "normal"), listGBC);
        ++listGBC.gridy;
        listPanel.add(OnboardingDialog.createLabel("onboarding.welcome_info6", "normal"), listGBC);
        ++listGBC.gridy;
        listPanel.add(OnboardingDialog.createLabel("onboarding.welcome_info7", "normal"), listGBC);
        ++listGBC.gridy;
        listGBC.insets = new Insets(0, 26, 0, 5);
        listPanel.add(OnboardingDialog.createLabel("onboarding.welcome_info8", "normal"), listGBC);
        panel.add(listPanel, gbc);
        JPanel noThanksPanel = new JPanel();
        noThanksPanel.setOpaque(false);
        noThanksPanel.setLayout(new BorderLayout());
        this.btInfo = new LinkRemoteButton(new ResourceAction("onboarding.login_info", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                OnboardingDialog.openURL(WelcomeCard.GET_COMMUNITY_INFO_URL);
            }
        });
        this.btInfo.setFocusable(false);
        JPanel innerInfoPanel = new JPanel();
        innerInfoPanel.setOpaque(false);
        innerInfoPanel.add(this.btInfo);
        noThanksPanel.add(innerInfoPanel, "Center");
        ++gbc.gridy;
        gbc.insets = new Insets(0, 5, 20, 5);
        panel.add(noThanksPanel, gbc);
        noThanksPanel = new JPanel();
        noThanksPanel.setLayout(new BoxLayout(noThanksPanel, 0));
        noThanksPanel.setOpaque(false);
        this.btSignup = new JButton(new ResourceAction("onboarding.go_to_signup", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                WelcomeCard.this.showSignupCard();
            }
        });
        this.btSignup.putClientProperty("com.rapidminer.ui.button.type", "cfa");
        noThanksPanel.add(this.btSignup);
        noThanksPanel.add(Box.createHorizontalStrut(10));
        noThanksPanel.add(OnboardingDialog.createLabel("onboarding.welcome_info9", "normal"));
        noThanksPanel.add(Box.createHorizontalStrut(10));
        this.btLogin = new JButton(new ResourceAction("onboarding.login", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                WelcomeCard.this.showInitialConnectToServiceCard();
            }
        });
        this.btLogin.setFocusable(false);
        this.btLogin.putClientProperty("com.rapidminer.ui.button.type", "cfa");
        noThanksPanel.add(this.btLogin);
        ++gbc.gridy;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(noThanksPanel, gbc);
        noThanksPanel = new JPanel();
        noThanksPanel.setOpaque(false);
        noThanksPanel.setLayout(new BorderLayout());
        this.btNoThanks = new LinkLocalButton(new ResourceAction("onboarding.minimum", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                OnboardingManager.INSTANCE.resetJoinCommunityReminder();
                WelcomeCard.this.getContainer().dispose();
            }
        });
        this.btNoThanks.setFocusable(false);
        noThanksPanel.add(this.btNoThanks, "Center");
        ++gbc.gridy;
        gbc.insets = new Insets(0, 5, 30, 5);
        panel.add(noThanksPanel, gbc);
        return panel;
    }

    public void showCard() {
        this.btSignup.requestFocusInWindow();
        this.getContainer().getRootPane().setDefaultButton(this.btSignup);
    }

    public Runnable getCloseAction() {
        OnboardingManager.INSTANCE.resetJoinCommunityReminder();
        return this.getContainer().getWelcomeType() == WelcomeType.COMMUNITY_FEATURE?this.standardCloseAction:this.standardAskAndCloseAction;
    }

    private void showInitialConnectToServiceCard() {
        this.getContainer().putSharedObject("com.rapidminer.onboarding.back_button", "onboarding.back_welcome");
        this.getContainer().showInitialConnectToServiceCard();
    }

    private void showSignupCard() {
        this.getContainer().putSharedObject("com.rapidminer.onboarding.error", (Object)null);
        this.getContainer().showSignupCard("onboarding.back_welcome");
    }
}
