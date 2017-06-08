package com.rapidminer.gui.license.onboarding;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.gui.license.LicenseTools;
import com.rapidminer.gui.license.onboarding.AbstractCard;
import com.rapidminer.gui.license.onboarding.ActivationSuccessfulCard;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.security.UserCredential;
import com.rapidminer.gui.security.Wallet;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.components.LinkLocalButton;
import com.rapidminer.gui.tools.components.LinkRemoteButton;
import com.rapidminer.license.License;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.nexus.NexusAuthenticationToken;
import com.rapidminer.tools.nexus.NexusCommunicationException;
import com.rapidminer.tools.nexus.NexusConnectionManager;
import com.rapidminer.tools.nexus.RapidMinerAccount;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

public class ConnectToServiceCard extends AbstractCard {
    public static final String CARD_ID = "connectToService";
    public static final String KEY_LOGIN_BACK = "onboarding.login.back";
    private static final Icon RM_ICON = OnboardingDialog.getIcon("onboarding.welcome_header");
    private static final Icon RM_ICON_LOADING = OnboardingDialog.getIcon("onboarding.welcome_header_loading");
    private static final String RESET_CREDENTIALS_URL = I18N.getGUILabel("onboarding.reset_credentials.url", new Object[0]);
    private static final String ERROR_EMPTY_USER = I18N.getGUILabel("onboarding.empty_user", new Object[0]);
    private static final String ERROR_EMPTY_PASSWORD = I18N.getGUILabel("onboarding.empty_password", new Object[0]);
    private static final Dimension TEXT_FIELD_DIMENSION = new Dimension(275, 30);
    private final ProgressThread downloadAndInstallLicenses = new ProgressThread("download_licenses") {
        public void run() {
            final String userName = ConnectToServiceCard.this.tfEmail.getText();
            final char[] password = ConnectToServiceCard.this.tfPassword.getPassword();

            try {
                final License e = ProductConstraintManager.INSTANCE.getActiveLicense();
                NexusConnectionManager.INSTANCE.installAvailableLicenses(new UserCredential("", userName, password));
                final License newLicense = ProductConstraintManager.INSTANCE.getActiveLicense();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if(newLicense.getPrecedence() > e.getPrecedence()) {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            ConnectToServiceCard.this.getContainer().putSharedObject("com.rapidminer.onboarding.license.owner", newLicense.getLicenseUser().getName());
                            ConnectToServiceCard.this.getContainer().putSharedObject("com.rapidminer.onboarding.license.edition", LicenseTools.translateProductEdition(newLicense));
                            ConnectToServiceCard.this.getContainer().putSharedObject("com.rapidminer.onboarding.license.expiration", newLicense.getExpirationDate() != null?df.format(newLicense.getExpirationDate()):I18N.getGUILabel("license.no_end_date", new Object[0]));
                            if(newLicense.getProductEdition().equals("trial")) {
                                ConnectToServiceCard.this.getContainer().putSharedObject("com.rapidminer.onboarding.license.additional_info", ActivationSuccessfulCard.getRemaingTrialDaysMessage(newLicense));
                            }

                            ConnectToServiceCard.this.getContainer().showActivationSuccessfulCard();
                            ConnectToServiceCard.this.connecting(false);
                        } else {
                            ConnectToServiceCard.this.getContainer().dispose();
                        }

                    }
                });
            } catch (final NexusCommunicationException var5) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ConnectToServiceCard.this.connecting(false);
                        if(var5.getApplicationStatusCode().equals("AUTH-04")) {
                            try {
                                ConnectToServiceCard.this.connecting(true);
                                NexusAuthenticationToken e = ConnectToServiceCard.this.getToken(userName, password);
                                NexusConnectionManager.INSTANCE.sendVerificationEmail(e);
                                ConnectToServiceCard.this.connecting(false);
                                ConnectToServiceCard.this.showEmailVerificationCard(userName, password);
                            } catch (final NexusCommunicationException var2) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        ConnectToServiceCard.this.connecting(false);
                                        ConnectToServiceCard.this.showError(var2.getMessage());
                                        LogService.getRoot().log(Level.WARNING, "Could not fetch user: " + var2.getMessage());
                                    }
                                });
                            } catch (final IOException var3) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        ConnectToServiceCard.this.connecting(false);
                                        ConnectToServiceCard.this.showError(var3.getMessage());
                                        LogService.getRoot().log(Level.WARNING, "Could not fetch user: " + var3.getMessage());
                                    }
                                });
                            } catch (IllegalAccessException var4) {
                                ;
                            }

                            ConnectToServiceCard.this.connecting(false);
                        } else {
                            ConnectToServiceCard.this.showError(var5.getMessage());
                            LogService.getRoot().log(Level.WARNING, "Could not fetch user: " + var5.getMessage());
                        }

                    }
                });
            } catch (final IOException var6) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ConnectToServiceCard.this.connecting(false);
                        ConnectToServiceCard.this.showError(I18N.getGUILabel("nexus.connection_error", new Object[0]));
                        LogService.getRoot().log(Level.WARNING, "Could not fetch license information: " + var6.getMessage());
                    }
                });
            }

        }
    };
    private JCheckBox chbRemember;
    private JTextField tfEmail;
    private JPasswordField tfPassword;
    private JLabel lbError;
    private JLabel lbRMLogoIcon;
    private LinkRemoteButton btResetCredentials;
    private LinkLocalButton btSignUp;
    private JButton btLogin;
    private OnboardingDialog onboarding = null;

    public ConnectToServiceCard(OnboardingDialog owner) {
        super("connectToService", owner);
        this.onboarding = owner;
    }

    public JPanel getHeader() {
        return OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.connect_to_service", new Object[0]));
    }

    public JPanel getContent() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, 1));
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        this.lbRMLogoIcon = new JLabel(RM_ICON);
        this.lbRMLogoIcon.setAlignmentX(0.5F);
        panel.add(this.lbRMLogoIcon);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(OnboardingDialog.createLabel("onboarding.connect_to_service_info1", "normal"));
        panel.add(OnboardingDialog.createLabel("onboarding.connect_to_service_info2", "normal"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.tfEmail = new JTextField();
        this.tfEmail.setPreferredSize(TEXT_FIELD_DIMENSION);
        this.tfEmail.setMaximumSize(TEXT_FIELD_DIMENSION);
        panel.add(this.tfEmail);
        PromptSupport.setForeground(Color.GRAY, this.tfEmail);
        PromptSupport.setPrompt(I18N.getGUILabel("onboarding.rm_email", new Object[0]), this.tfEmail);
        PromptSupport.setFontStyle(Integer.valueOf(2), this.tfEmail);
        PromptSupport.setFocusBehavior(FocusBehavior.SHOW_PROMPT, this.tfEmail);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.tfPassword = new JPasswordField();
        this.tfPassword.setPreferredSize(TEXT_FIELD_DIMENSION);
        this.tfPassword.setMaximumSize(TEXT_FIELD_DIMENSION);
        panel.add(this.tfPassword);
        PromptSupport.setForeground(Color.GRAY, this.tfPassword);
        PromptSupport.setPrompt(I18N.getGUILabel("onboarding.rm_pw", new Object[0]), this.tfPassword);
        PromptSupport.setFontStyle(Integer.valueOf(2), this.tfPassword);
        PromptSupport.setFocusBehavior(FocusBehavior.SHOW_PROMPT, this.tfPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.chbRemember = new JCheckBox(I18N.getGUILabel("onboarding.remember_pw", new Object[0]));
        this.chbRemember.putClientProperty("com.rapidminer.ui.label.type", "normal");
        this.chbRemember.setOpaque(false);
        this.chbRemember.setSelected(true);
        this.chbRemember.setMargin(new Insets(0, 0, 0, 0));
        JPanel signupPanel = new JPanel(new FlowLayout(0, 0, 0));
        signupPanel.setOpaque(false);
        signupPanel.setMinimumSize(new Dimension(275, 25));
        signupPanel.setMaximumSize(new Dimension(275, 25));
        signupPanel.add(this.chbRemember);
        panel.add(signupPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        this.lbError = new JLabel(" ");
        this.lbError.setAlignmentX(0.5F);
        this.lbError.putClientProperty("com.rapidminer.ui.label.type", "bold");
        this.lbError.putClientProperty("com.rapidminer.ui.label.foreground", Color.RED);
        panel.add(this.lbError);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        this.btLogin = new JButton(new ResourceAction("onboarding.login_and_install", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                String error = ConnectToServiceCard.this.validateInput();
                if(error == null) {
                    if(ConnectToServiceCard.this.chbRemember.isSelected()) {
                        UserCredential credentials = new UserCredential("https://my.rapidminer.com", ConnectToServiceCard.this.tfEmail.getText(), ConnectToServiceCard.this.tfPassword.getPassword());
                        Wallet.getInstance().registerCredentials("RapidMiner.com account", credentials);
                        Wallet.getInstance().saveCache();
                    }

                    ConnectToServiceCard.this.showError((String)null);
                    ConnectToServiceCard.this.connecting(true);
                    ConnectToServiceCard.this.downloadAndInstallLicenses.start();
                } else {
                    ConnectToServiceCard.this.showError(error);
                }

            }
        });
        this.btLogin.putClientProperty("com.rapidminer.ui.button.type", "cfa");
        this.btLogin.setAlignmentX(0.5F);
        panel.add(this.btLogin);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        signupPanel = new JPanel(new FlowLayout(1, 0, 0));
        this.btResetCredentials = new LinkRemoteButton(new ResourceAction(false, "reset_credentials", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                OnboardingDialog.openURL(ConnectToServiceCard.RESET_CREDENTIALS_URL);
            }
        });
        this.btResetCredentials.putClientProperty("com.rapidmniner.ui.link_button.id", "reset_credentials");
        this.btResetCredentials.setFocusable(false);
        signupPanel.setOpaque(false);
        signupPanel.add(this.btResetCredentials);
        signupPanel.setMaximumSize(signupPanel.getPreferredSize());
        panel.add(signupPanel);
        signupPanel = new JPanel(new FlowLayout(1, 0, 0));
        this.btSignUp = new LinkLocalButton(new ResourceAction(false, "onboarding.sign_up", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                ConnectToServiceCard.this.onboarding.showSignupCard("onboarding.login.back");
            }
        });
        this.btSignUp.setFocusable(false);
        this.btSignUp.putClientProperty("com.rapidmniner.ui.link_button.id", "onboarding.sign_up");
        signupPanel.setOpaque(false);
        signupPanel.add(this.btSignUp);
        signupPanel.setMaximumSize(signupPanel.getPreferredSize());
        panel.add(signupPanel);
        return panel;
    }

    public void showCard() {
        String errorMessage = (String)this.getContainer().getSharedObject("com.rapidminer.onboarding.error");
        if(errorMessage != null) {
            this.showError(errorMessage);
        }

        UserCredential credentials = Wallet.getInstance().getEntry("RapidMiner.com account", "https://my.rapidminer.com");
        if(credentials != null) {
            this.tfEmail.setText(credentials.getUsername());
            this.tfPassword.setText(credentials.getPassword() != null?new String(credentials.getPassword()):null);
        }

        this.tfEmail.requestFocusInWindow();
        this.getContainer().getRootPane().setDefaultButton(this.btLogin);
    }

    public Runnable getCloseAction() {
        return this.standardAskAndCloseAction;
    }

    private String validateInput() {
        String user = this.tfEmail.getText();
        String pw = new String(this.tfPassword.getPassword());
        return user != null && !user.trim().isEmpty()?(pw != null && !pw.trim().isEmpty()?null:ERROR_EMPTY_PASSWORD):ERROR_EMPTY_USER;
    }

    private void connecting(boolean value) {
        if(value) {
            this.lbRMLogoIcon.setIcon(RM_ICON_LOADING);
        } else {
            this.lbRMLogoIcon.setIcon(RM_ICON);
        }

        this.tfEmail.setEnabled(!value);
        this.tfPassword.setEnabled(!value);
        this.btLogin.setEnabled(!value);
        this.btSignUp.setEnabled(!value);
        this.btResetCredentials.setEnabled(!value);
        this.chbRemember.setEnabled(!value);
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
        this.getContainer().showEmailVerificationCard(userName, password, "onboarding.login.back");
    }

    private NexusAuthenticationToken getToken(String userName, char[] password) throws IllegalAccessException, NexusCommunicationException, IOException {
        return RapidMinerAccount.getRMAccountAuthToken(userName, password);
    }
}
