package com.rapidminer.gui.license.onboarding;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.gui.license.onboarding.AbstractCard;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.security.UserCredential;
import com.rapidminer.gui.security.Wallet;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.components.LinkLocalButton;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.nexus.NexusAuthenticationToken;
import com.rapidminer.tools.nexus.NexusCommunicationException;
import com.rapidminer.tools.nexus.NexusConnectionManager;
import com.rapidminer.tools.nexus.RapidMinerAccount;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.prompt.PromptSupport;
import org.jdesktop.swingx.prompt.PromptSupport.FocusBehavior;

public class SignUpCard extends AbstractCard {
    public static final String CARD_ID = "signup";
    public static final String KEY_SIGN_UP_BACK = "onboarding.back_signup";
    private static final Icon RM_ICON = OnboardingDialog.getIcon("onboarding.welcome_header");
    private static final Icon RM_ICON_LOADING = OnboardingDialog.getIcon("onboarding.welcome_header_loading");
    private static final String ERROR_EMPTY_USER = I18N.getGUILabel("onboarding.empty_user", new Object[0]);
    private static final String ERROR_EMPTY_PASSWORD = I18N.getGUILabel("onboarding.empty_password", new Object[0]);
    private static final String ERROR_PASSWORD_CONFIRMATION = I18N.getGUILabel("onboarding.password_confirmation", new Object[0]);
    private static final String ERROR_PASSWORD_SECURITY = I18N.getGUILabel("onboarding.password_security", new Object[0]);
    private static final Dimension TEXT_FIELD_DIMENSION = new Dimension(275, 30);
    private final ProgressThread registerUser = new ProgressThread("register_user") {
        public void run() {
            SignUpCard.this.connecting(true);
            final String userName = SignUpCard.this.tfEmail.getText().trim();
            final char[] password = SignUpCard.this.tfPassword.getPassword();

            try {
                NexusConnectionManager.INSTANCE.createUser(new UserCredential("", userName, password));
                SignUpCard.this.showEmailVerificationCard(userName, password);
                SignUpCard.this.connecting(false);
            } catch (final NexusCommunicationException var4) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        boolean notActivatedYet = false;
                        SignUpCard.this.connecting(false);
                        if(var4.getApplicationStatusCode().equals("PRO-10")) {
                            try {
                                SignUpCard.this.connecting(true);
                                NexusAuthenticationToken e = SignUpCard.this.getToken(userName, password);
                                if(!e.isEmailVerified()) {
                                    notActivatedYet = true;
                                    SignUpCard.this.connecting(true);
                                    NexusConnectionManager.INSTANCE.sendVerificationEmail(e);
                                    SignUpCard.this.connecting(false);
                                    SignUpCard.this.showEmailVerificationCard(userName, password);
                                }
                            } catch (final NexusCommunicationException var3) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        SignUpCard.this.connecting(false);
                                        LogService.getRoot().log(Level.WARNING, "Could not fetch user: " + var3.getMessage());
                                    }
                                });
                            } catch (final IOException var4x) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        SignUpCard.this.connecting(false);
                                        LogService.getRoot().log(Level.WARNING, "Could not fetch user: " + var4x.getMessage());
                                    }
                                });
                            } catch (IllegalAccessException var5) {
                                ;
                            }

                            if(!notActivatedYet) {
                                SignUpCard.this.showError(var4.getMessage());
                            }

                            SignUpCard.this.connecting(false);
                        } else {
                            SignUpCard.this.showError(var4.getMessage());
                            LogService.getRoot().log(Level.WARNING, "Could not fetch user: " + var4.getMessage());
                        }

                    }
                });
            } catch (final IOException var5) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        SignUpCard.this.connecting(false);
                        SignUpCard.this.showError(I18N.getGUILabel("nexus.connection_error", new Object[0]));
                        LogService.getRoot().log(Level.WARNING, "Could not fetch user: " + var5.getMessage());
                    }
                });
            }

        }
    };
    private JTextField tfEmail;
    private JPasswordField tfPassword;
    private JPasswordField tfPasswordConfirmation;
    private JCheckBox chbRemember;
    private JLabel lbError;
    private JLabel lbRMLogoIcon;
    private JButton btSignup;
    private JButton btBack;
    private LinkLocalButton btLogin;

    public SignUpCard(OnboardingDialog onboardingDialog) {
        super("signup", onboardingDialog);
    }

    public JPanel getHeader() {
        return OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.signup", new Object[0]));
    }

    public JPanel getContent() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, 1));
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        this.lbRMLogoIcon = new JLabel(RM_ICON);
        this.lbRMLogoIcon.setAlignmentX(0.5F);
        panel.add(this.lbRMLogoIcon);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(OnboardingDialog.createLabel("onboarding.signup_info1", "normal"));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        this.tfEmail = new JTextField();
        this.tfEmail.setPreferredSize(TEXT_FIELD_DIMENSION);
        this.tfEmail.setMaximumSize(TEXT_FIELD_DIMENSION);
        panel.add(this.tfEmail);
        PromptSupport.setForeground(Color.GRAY, this.tfEmail);
        PromptSupport.setPrompt(I18N.getGUILabel("onboarding.signup.rm_email", new Object[0]), this.tfEmail);
        PromptSupport.setFontStyle(Integer.valueOf(2), this.tfEmail);
        PromptSupport.setFocusBehavior(FocusBehavior.SHOW_PROMPT, this.tfEmail);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.tfPassword = new JPasswordField();
        this.tfPassword.setPreferredSize(TEXT_FIELD_DIMENSION);
        this.tfPassword.setMaximumSize(TEXT_FIELD_DIMENSION);
        panel.add(this.tfPassword);
        PromptSupport.setForeground(Color.GRAY, this.tfPassword);
        PromptSupport.setPrompt(I18N.getGUILabel("onboarding.signup.rm_pw", new Object[0]), this.tfPassword);
        PromptSupport.setFontStyle(Integer.valueOf(2), this.tfPassword);
        PromptSupport.setFocusBehavior(FocusBehavior.SHOW_PROMPT, this.tfPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.tfPasswordConfirmation = new JPasswordField();
        this.tfPasswordConfirmation.setPreferredSize(TEXT_FIELD_DIMENSION);
        this.tfPasswordConfirmation.setMaximumSize(TEXT_FIELD_DIMENSION);
        panel.add(this.tfPasswordConfirmation);
        PromptSupport.setForeground(Color.GRAY, this.tfPasswordConfirmation);
        PromptSupport.setPrompt(I18N.getGUILabel("onboarding.signup.rm_pw_confirmation", new Object[0]), this.tfPasswordConfirmation);
        PromptSupport.setFontStyle(Integer.valueOf(2), this.tfPasswordConfirmation);
        PromptSupport.setFocusBehavior(FocusBehavior.SHOW_PROMPT, this.tfPasswordConfirmation);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.chbRemember = new JCheckBox(I18N.getGUILabel("onboarding.remember_pw", new Object[0]));
        this.chbRemember.putClientProperty("com.rapidminer.ui.label.type", "normal");
        this.chbRemember.setOpaque(false);
        this.chbRemember.setSelected(true);
        this.chbRemember.setMargin(new Insets(0, 0, 0, 0));
        JPanel loginPanel = new JPanel(new FlowLayout(0, 0, 0));
        loginPanel.setOpaque(false);
        loginPanel.setMinimumSize(new Dimension(275, 25));
        loginPanel.setMaximumSize(new Dimension(275, 25));
        loginPanel.add(this.chbRemember);
        panel.add(loginPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        this.lbError = new JLabel(" ");
        this.lbError.setAlignmentX(0.5F);
        this.lbError.putClientProperty("com.rapidminer.ui.label.type", "bold");
        this.lbError.putClientProperty("com.rapidminer.ui.label.foreground", Color.RED);
        panel.add(this.lbError);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        this.btSignup = new JButton(new ResourceAction("onboarding.signup", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                String error = SignUpCard.this.validateInput();
                if(error == null) {
                    if(SignUpCard.this.chbRemember.isSelected()) {
                        UserCredential credentials = new UserCredential("https://my.rapidminer.com", SignUpCard.this.tfEmail.getText(), SignUpCard.this.tfPassword.getPassword());
                        Wallet.getInstance().registerCredentials("RapidMiner.com account", credentials);
                        Wallet.getInstance().saveCache();
                    }

                    SignUpCard.this.showError((String)null);
                    SignUpCard.this.registerUser.start();
                } else {
                    SignUpCard.this.showError(error);
                }

            }
        });
        this.btSignup.putClientProperty("com.rapidminer.ui.button.type", "cfa");
        this.btSignup.setAlignmentX(0.5F);
        panel.add(this.btSignup);
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints loginGBC = new GridBagConstraints();
        loginPanel.setOpaque(false);
        loginGBC.gridx = 0;
        loginGBC.gridy = 0;
        loginGBC.fill = 2;
        JPanel gapPanel = new JPanel();
        gapPanel.setOpaque(false);
        loginPanel.add(gapPanel, loginGBC);
        ++loginGBC.gridx;
        loginGBC.fill = 0;
        loginPanel.add(OnboardingDialog.createLabel("onboarding.signup_info2", "normal"));
        ++loginGBC.gridx;
        loginPanel.add(Box.createHorizontalStrut(5));
        JPanel linkButtonPanel = new JPanel(new GridBagLayout());
        linkButtonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        this.btLogin = new LinkLocalButton(new ResourceAction("onboarding.login", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                SignUpCard.this.getContainer().showInitialConnectToServiceCard("onboarding.back_signup");
            }
        });
        this.btLogin.setFocusable(false);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = 17;
        gbc.insets = new Insets(0, 0, 0, 0);
        linkButtonPanel.add(this.btLogin, gbc);
        ++loginGBC.gridx;
        loginPanel.add(linkButtonPanel);
        ++loginGBC.gridx;
        loginGBC.fill = 2;
        loginPanel.add(gapPanel, loginGBC);
        panel.add(loginPanel);
        panel.add(Box.createVerticalGlue());
        this.btBack = new JButton(new ResourceAction("onboarding.back", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                String key = SignUpCard.this.getContainer().getSharedObject("com.rapidminer.onboarding.back_button").toString();
                SignUpCard.this.showError((String)null);
                if(key.equals("onboarding.back_welcome")) {
                    SignUpCard.this.getContainer().showWelcomeCard();
                } else if(key.equals("onboarding.login.back")) {
                    SignUpCard.this.getContainer().showConnectToServiceCard();
                }

            }
        });
        this.btBack.setFocusable(false);
        this.btBack.putClientProperty("com.rapidminer.ui.button.type", "normal");
        panel.add(OnboardingDialog.createButtonPanel(false, new JButton[]{this.btBack}));
        return panel;
    }

    public void showCard() {
        this.tfEmail.requestFocusInWindow();
        this.getContainer().getRootPane().setDefaultButton(this.btSignup);
    }

    public Runnable getCloseAction() {
        return this.standardAskAndCloseAction;
    }

    private void connecting(boolean value) {
        if(value) {
            this.lbRMLogoIcon.setIcon(RM_ICON_LOADING);
        } else {
            this.lbRMLogoIcon.setIcon(RM_ICON);
        }

        this.tfEmail.setEnabled(!value);
        this.tfPassword.setEnabled(!value);
        this.tfPasswordConfirmation.setEnabled(!value);
        this.chbRemember.setEnabled(!value);
        this.btSignup.setEnabled(!value);
        this.btBack.setEnabled(!value);
    }

    private String validateInput() {
        String user = this.tfEmail.getText();
        String pw = new String(this.tfPassword.getPassword());
        String pwConfirmation = new String(this.tfPasswordConfirmation.getPassword());
        return user != null && !user.trim().isEmpty()?(pw != null && !pw.trim().isEmpty()?(!pw.equals(pwConfirmation)?ERROR_PASSWORD_CONFIRMATION:(pw.length() < 6?ERROR_PASSWORD_SECURITY:null)):ERROR_EMPTY_PASSWORD):ERROR_EMPTY_USER;
    }

    private void showError(String errorMsg) {
        if(errorMsg == null) {
            this.lbError.setText(" ");
        } else {
            this.lbError.setText(errorMsg);
        }

    }

    private void showEmailVerificationCard(String userName, char[] password) {
        this.showError((String)null);
        this.getContainer().putSharedObject("com.rapidminer.onboarding.error", (Object)null);
        this.getContainer().showEmailVerificationCard(userName, password, "onboarding.back_signup");
    }

    private NexusAuthenticationToken getToken(String userName, char[] password) throws IllegalAccessException, NexusCommunicationException, IOException {
        return RapidMinerAccount.getRMAccountAuthToken(userName, password);
    }
}
