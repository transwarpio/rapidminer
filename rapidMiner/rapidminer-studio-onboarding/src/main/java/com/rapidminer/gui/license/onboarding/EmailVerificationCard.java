package com.rapidminer.gui.license.onboarding;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.license.LicenseTools;
import com.rapidminer.gui.license.onboarding.AbstractCard;
import com.rapidminer.gui.license.onboarding.ActivationSuccessfulCard;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.security.UserCredential;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.ResourceLabel;
import com.rapidminer.gui.tools.components.LinkLocalButton;
import com.rapidminer.gui.tools.dialogs.ConfirmDialog;
import com.rapidminer.license.License;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.nexus.NexusAuthenticationToken;
import com.rapidminer.tools.nexus.NexusCommunicationException;
import com.rapidminer.tools.nexus.NexusConnectionManager;
import com.rapidminer.tools.nexus.NexusUtilities;
import com.rapidminer.tools.nexus.RapidMinerAccount;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

public class EmailVerificationCard extends AbstractCard {
    public static final String CARD_ID = "emailVerification";
    private static final Icon RM_ICON = OnboardingDialog.getIcon("onboarding.welcome_header");
    private static final Icon RM_ICON_LOADING = OnboardingDialog.getIcon("onboarding.welcome_header_loading");
    private static final Icon CHECK_GRAY = OnboardingDialog.getIcon("onboarding.check_small_gray");
    private static final Icon CHECK_GREEN = OnboardingDialog.getIcon("onboarding.check_small");
    private static final String STATUS_MESSAGE_POLLING = I18N.getGUILabel("onboarding.email_verification_status_polling", new Object[0]);
    private JPanel panel;
    private JLabel lbMessage;
    private JLabel lbError;
    private JLabel lbRMLogoIcon;
    private JLabel lbInfoWithEmail;
    private JLabel labelActivationDescription;
    private JLabel lbActivationState;
    private JLabel lbResendEmail;
    private JButton btRefresh;
    private LinkLocalButton btResendEmail;
    private JButton btBack;
    private NexusAuthenticationToken authToken = null;
    private OnboardingDialog onboarding = null;
    private Timer verificationTimer = null;
    private boolean cancelled = false;
    private final ProgressThread downloadAndInstallLicenses = new ProgressThread("download_licenses") {
        public void run() {
            try {
                String e = (String)EmailVerificationCard.this.onboarding.getSharedObject("com.rapidminer.onboarding.user");
                char[] password = (char[])((char[])EmailVerificationCard.this.onboarding.getSharedObject("com.rapidminer.onboarding.password"));
                NexusConnectionManager.INSTANCE.installAvailableLicenses(new UserCredential("", e, password));
                final License activeLicense = ProductConstraintManager.INSTANCE.getActiveLicense();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        EmailVerificationCard.this.getContainer().putSharedObject("com.rapidminer.onboarding.license.owner", activeLicense.getLicenseUser().getName());
                        EmailVerificationCard.this.getContainer().putSharedObject("com.rapidminer.onboarding.license.edition", LicenseTools.translateProductEdition(activeLicense));
                        EmailVerificationCard.this.getContainer().putSharedObject("com.rapidminer.onboarding.license.expiration", activeLicense.getExpirationDate() != null?df.format(activeLicense.getExpirationDate()):I18N.getGUILabel("license.no_end_date", new Object[0]));
                        if(activeLicense.getProductEdition().equals("trial")) {
                            EmailVerificationCard.this.getContainer().putSharedObject("com.rapidminer.onboarding.license.additional_info", ActivationSuccessfulCard.getRemaingTrialDaysMessage(activeLicense));
                            EmailVerificationCard.this.getContainer().showActivationSuccessfulCard();
                        } else {
                            EmailVerificationCard.this.getContainer().showActivationSuccessfulCard();
                        }

                        EmailVerificationCard.this.connecting(false);
                    }
                });
            } catch (final NexusCommunicationException var4) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EmailVerificationCard.this.connecting(false);
                        EmailVerificationCard.this.showError(NexusUtilities.translateApplicationStatusCode(var4.getApplicationStatusCode()));
                        EmailVerificationCard.this.showMessage((String)null);
                        LogService.getRoot().log(Level.WARNING, "Could not fetch license information: " + var4.getMessage());
                    }
                });
            } catch (final IOException var5) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EmailVerificationCard.this.connecting(false);
                        EmailVerificationCard.this.showError(I18N.getGUILabel("nexus.connection_error", new Object[0]));
                        EmailVerificationCard.this.showMessage((String)null);
                        LogService.getRoot().log(Level.WARNING, "Could not fetch license information: " + var5.getMessage());
                    }
                });
            }

        }
    };
    private final ProgressThread checkVerificationStatus = new ProgressThread("verification_status") {
        public void run() {
            EmailVerificationCard.this.connecting(true);
            EmailVerificationCard.this.showError((String)null);

            try {
                String e = (String)EmailVerificationCard.this.onboarding.getSharedObject("com.rapidminer.onboarding.user");
                char[] password = (char[])((char[])EmailVerificationCard.this.onboarding.getSharedObject("com.rapidminer.onboarding.password"));
                if(!NexusConnectionManager.INSTANCE.isEmailVerified(EmailVerificationCard.this.authToken)) {
                    if(EmailVerificationCard.this.cancelled) {
                        EmailVerificationCard.this.connecting(false);
                        EmailVerificationCard.this.showError("Email address is still not verified.");
                        EmailVerificationCard.this.showMessage((String)null);
                        LogService.getRoot().log(Level.WARNING, "Could not fetch user: Email address is still not verified.");
                    }
                } else {
                    EmailVerificationCard.this.verificationTimer.cancel();
                    EmailVerificationCard.this.accountActivated();
                    EmailVerificationCard.this.connecting(true);
                    EmailVerificationCard.this.authToken = EmailVerificationCard.this.getToken(e, password, true);
                    EmailVerificationCard.this.downloadAndInstallLicenses.start();
                }
            } catch (final NexusCommunicationException var3) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EmailVerificationCard.this.cancelled = true;
                        EmailVerificationCard.this.connecting(false);
                        EmailVerificationCard.this.showError(var3.getMessage());
                        EmailVerificationCard.this.showMessage((String)null);
                        LogService.getRoot().log(Level.WARNING, "Could not fetch user: " + var3.getMessage());
                    }
                });
            } catch (final IOException var4) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EmailVerificationCard.this.cancelled = true;
                        EmailVerificationCard.this.connecting(false);
                        EmailVerificationCard.this.showError(I18N.getGUILabel("nexus.connection_error", new Object[0]));
                        EmailVerificationCard.this.showMessage((String)null);
                        LogService.getRoot().log(Level.WARNING, "Could not fetch user: " + var4.getMessage());
                    }
                });
            } catch (IllegalAccessException var5) {
                throw new RuntimeException(var5);
            }

            if(EmailVerificationCard.this.cancelled) {
                EmailVerificationCard.this.verificationTimer.cancel();
                EmailVerificationCard.this.connecting(false);
                EmailVerificationCard.this.btRefresh.setVisible(true);
            }

        }
    };
    private final ProgressThread checkAccountVerification = new ProgressThread("account_verification") {
        public void run() {
            String userName = (String)EmailVerificationCard.this.onboarding.getSharedObject("com.rapidminer.onboarding.user");
            char[] password = (char[])((char[])EmailVerificationCard.this.onboarding.getSharedObject("com.rapidminer.onboarding.password"));

            try {
                EmailVerificationCard.this.showError((String)null);
                EmailVerificationCard.this.showMessage(EmailVerificationCard.STATUS_MESSAGE_POLLING);
                EmailVerificationCard.this.connecting(true);
                EmailVerificationCard.this.authToken = EmailVerificationCard.this.getToken(userName, password, true);
                EmailVerificationCard.VerificationCheckTimerTask e = EmailVerificationCard.this.new VerificationCheckTimerTask();
                EmailVerificationCard.this.verificationTimer = new Timer(true);
                EmailVerificationCard.this.verificationTimer.scheduleAtFixedRate(e, 0L, 10000L);

                try {
                    Thread.sleep(300000L);
                } catch (InterruptedException var5) {
                    ;
                }

                EmailVerificationCard.this.verificationTimer.cancel();
                EmailVerificationCard.this.connecting(false);
                EmailVerificationCard.this.cancelled = true;
            } catch (final NexusCommunicationException var6) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EmailVerificationCard.this.cancelled = true;
                        EmailVerificationCard.this.connecting(false);
                        EmailVerificationCard.this.showError(var6.getMessage());
                        EmailVerificationCard.this.showMessage((String)null);
                        LogService.getRoot().log(Level.WARNING, "Could not fetch user: " + var6.getMessage());
                    }
                });
            } catch (final IOException var7) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EmailVerificationCard.this.cancelled = true;
                        EmailVerificationCard.this.connecting(false);
                        EmailVerificationCard.this.showError(I18N.getGUILabel("nexus.connection_error", new Object[0]));
                        EmailVerificationCard.this.showMessage((String)null);
                        LogService.getRoot().log(Level.WARNING, "Could not fetch user: " + var7.getMessage());
                    }
                });
            } catch (IllegalAccessException var8) {
                throw new RuntimeException(var8);
            }

            if(EmailVerificationCard.this.authToken != null) {
                if(!EmailVerificationCard.this.authToken.isEmailVerified()) {
                    EmailVerificationCard.this.showError("Email address is still not verified.");
                    EmailVerificationCard.this.showMessage((String)null);
                    EmailVerificationCard.this.btRefresh.setVisible(true);
                }
            } else {
                EmailVerificationCard.this.btRefresh.setVisible(true);
            }

        }
    };
    private final ProgressThread resendEmail = new ProgressThread("resend_email") {
        public void run() {
            String userName = (String)EmailVerificationCard.this.onboarding.getSharedObject("com.rapidminer.onboarding.user");
            char[] password = (char[])((char[])EmailVerificationCard.this.onboarding.getSharedObject("com.rapidminer.onboarding.password"));

            try {
                EmailVerificationCard.this.connecting(true);
                EmailVerificationCard.this.authToken = EmailVerificationCard.this.getToken(userName, password, false);
                NexusConnectionManager.INSTANCE.sendVerificationEmail(EmailVerificationCard.this.authToken);
                EmailVerificationCard.this.connecting(false);
                EmailVerificationCard.this.btResendEmail.setVisible(false);
                EmailVerificationCard.this.lbResendEmail.setVisible(true);
            } catch (final NexusCommunicationException var4) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EmailVerificationCard.this.connecting(false);
                        EmailVerificationCard.this.showError(var4.getMessage());
                        EmailVerificationCard.this.showMessage((String)null);
                        LogService.getRoot().log(Level.WARNING, "Could not send verification email: " + var4.getMessage());
                    }
                });
            } catch (final IOException var5) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        EmailVerificationCard.this.connecting(false);
                        EmailVerificationCard.this.showError(I18N.getGUILabel("nexus.connection_error", new Object[0]));
                        EmailVerificationCard.this.showMessage((String)null);
                        LogService.getRoot().log(Level.WARNING, "Could not send verification email: " + var5.getMessage());
                    }
                });
            } catch (IllegalAccessException var6) {
                throw new RuntimeException(var6);
            }

        }
    };

    public EmailVerificationCard(OnboardingDialog onboardingDialog) {
        super("emailVerification", onboardingDialog);
        this.onboarding = onboardingDialog;
    }

    public JPanel getHeader() {
        return OnboardingDialog.createSimpleHeader(I18N.getGUILabel("onboarding.email_verification", new Object[0]));
    }

    public JPanel getContent() {
        this.panel = new JPanel();
        this.panel.setOpaque(false);
        this.panel.setLayout(new BoxLayout(this.panel, 1));
        this.panel.add(Box.createRigidArea(new Dimension(0, 30)));
        this.lbRMLogoIcon = new JLabel(RM_ICON);
        this.lbRMLogoIcon.setAlignmentX(0.5F);
        this.panel.add(this.lbRMLogoIcon);
        this.panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.panel.add(OnboardingDialog.createLabel("onboarding.email_verification_info3", "normal"));
        JPanel separatorPanel = new JPanel();
        separatorPanel.setOpaque(false);
        String separator = "email@unknown.com";
        this.lbInfoWithEmail = new ResourceLabel("onboarding.email_verification_info4", new Object[]{separator});
        this.lbInfoWithEmail.putClientProperty("com.rapidminer.ui.label.type", "normal");
        this.lbInfoWithEmail.setAlignmentX(0.5F);
        separatorPanel.add(this.lbInfoWithEmail);
        this.panel.add(separatorPanel);
        this.panel.add(OnboardingDialog.createLabel("onboarding.email_verification_info5", "normal"));
        this.panel.add(Box.createRigidArea(new Dimension(0, 5)));
        separatorPanel = new JPanel();
        separatorPanel.setLayout(new GridBagLayout());
        separatorPanel.setOpaque(false);
        GridBagConstraints separator1 = new GridBagConstraints();
        separator1.gridx = 0;
        separator1.ipadx = 20;
        separator1.gridy = 0;
        separator1.anchor = 10;
        this.labelActivationDescription = OnboardingDialog.createLabel("onboarding.email_verification_info1", "normal");
        this.lbActivationState = new JLabel(CHECK_GRAY);
        separatorPanel.add(this.lbActivationState, separator1);
        ++separator1.gridx;
        separatorPanel.add(this.labelActivationDescription, separator1);
        this.panel.add(separatorPanel);
        this.panel.add(Box.createRigidArea(new Dimension(0, 5)));
        separatorPanel = new JPanel(new GridBagLayout());
        separatorPanel.setOpaque(false);
        separator1 = new GridBagConstraints();
        separator1.gridx = 0;
        separator1.gridy = 0;
        separator1.anchor = 10;
        this.lbError = new JLabel(" ");
        this.lbError.setAlignmentX(0.5F);
        this.lbError.putClientProperty("com.rapidminer.ui.label.type", "bold");
        this.lbError.putClientProperty("com.rapidminer.ui.label.foreground", Color.RED);
        separatorPanel.add(this.lbError, separator1);
        this.lbMessage = new JLabel(" ");
        this.lbMessage.setAlignmentX(0.5F);
        this.lbMessage.putClientProperty("com.rapidminer.ui.label.type", "bold");
        this.lbMessage.putClientProperty("com.rapidminer.ui.label.foreground", Color.BLACK);
        separatorPanel.add(this.lbMessage, separator1);
        this.panel.add(separatorPanel);
        this.panel.add(Box.createRigidArea(new Dimension(0, 5)));
        this.btRefresh = new JButton(new ResourceAction("onboarding.verification_done", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                EmailVerificationCard.this.checkVerificationStatus.start();
            }
        });
        this.btRefresh.putClientProperty("com.rapidminer.ui.button.type", "cfa");
        this.btRefresh.setAlignmentX(0.5F);
        this.panel.add(this.btRefresh);
        this.panel.add(Box.createRigidArea(new Dimension(0, 5)));
        separatorPanel = new JPanel();
        separatorPanel.setOpaque(false);
        JSeparator separator2 = new JSeparator();
        separator2.setForeground(Color.LIGHT_GRAY);
        separator2.setMinimumSize(new Dimension(350, separator2.getPreferredSize().height));
        separator2.setPreferredSize(new Dimension(350, separator2.getPreferredSize().height));
        separatorPanel.add(separator2);
        this.panel.add(separatorPanel);
        this.btResendEmail = new LinkLocalButton(new ResourceAction("onboarding.resend_verification_mail", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                EmailVerificationCard.this.resendEmail.start();
            }
        });
        this.btResendEmail.putClientProperty("com.rapidminer.ui.button.type", "cfa");
        this.btResendEmail.setFocusable(false);
        this.lbResendEmail = OnboardingDialog.createLabel("onboarding.email_resend", "normal");
        this.lbResendEmail.setAlignmentX(0.5F);
        this.lbResendEmail.setVisible(false);
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = 10;
        outerPanel.setOpaque(false);
        outerPanel.add(this.btResendEmail, gbc);
        outerPanel.add(this.lbResendEmail, gbc);
        this.panel.add(outerPanel);
        this.panel.add(Box.createVerticalGlue());
        this.btBack = new JButton(new ResourceAction("onboarding.back", new Object[0]) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                EmailVerificationCard.this.verificationTimer.cancel();
                String key = EmailVerificationCard.this.getContainer().getSharedObject("com.rapidminer.onboarding.back_button").toString();
                EmailVerificationCard.this.showError((String)null);
                if(key.equals("onboarding.back_signup")) {
                    EmailVerificationCard.this.getContainer().showSignupCard("onboarding.back_welcome");
                } else if(key.equals("onboarding.initial.login.back")) {
                    EmailVerificationCard.this.getContainer().showInitialConnectToServiceCard("onboarding.back_welcome");
                } else if(key.equals("onboarding.login.back")) {
                    EmailVerificationCard.this.getContainer().showConnectToServiceCard();
                } else {
                    EmailVerificationCard.this.getContainer().showWelcomeCard();
                }

            }
        });
        this.btBack.setFocusable(false);
        this.btBack.putClientProperty("com.rapidminer.ui.button.type", "normal");
        this.panel.add(OnboardingDialog.createButtonPanel(false, new JButton[]{this.btBack}));
        return this.panel;
    }

    public void showCard() {
        this.btRefresh.setVisible(false);
        this.lbResendEmail.setVisible(false);
        this.btResendEmail.setVisible(true);
        this.fillContent((String)this.onboarding.getSharedObject("com.rapidminer.onboarding.user"));
        this.btBack.requestFocusInWindow();
        this.checkAccountVerification.start();
    }

    public Runnable getCloseAction() {
        return new Runnable() {
            public void run() {
                int answer = ConfirmDialog.showConfirmDialogWithOptionalCheckbox(ApplicationFrame.getApplicationFrame(), "free_edition", 0, (String)null, 0, false, new Object[0]);
                if(answer == 0) {
                    EmailVerificationCard.this.verificationTimer.cancel();
                    EmailVerificationCard.this.getContainer().dispose();
                }

            }
        };
    }

    private void connecting(boolean value) {
        if(value) {
            this.lbRMLogoIcon.setIcon(RM_ICON_LOADING);
        } else {
            this.lbRMLogoIcon.setIcon(RM_ICON);
        }

    }

    private void showError(String errorMsg) {
        if(errorMsg == null) {
            this.lbError.setText(" ");
        } else {
            this.lbError.setText(errorMsg);
        }

    }

    private void showMessage(String msg) {
        if(msg == null) {
            this.lbMessage.setText(" ");
        } else {
            this.lbMessage.setText(msg);
        }

    }

    private NexusAuthenticationToken getToken(String userName, char[] password, boolean forceReload) throws IllegalAccessException, NexusCommunicationException, IOException {
        return RapidMinerAccount.getRMAccountAuthToken(userName, password, forceReload);
    }

    private void fillContent(String email) {
        if(email != null) {
            this.lbInfoWithEmail.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.label.onboarding.email_verification_info4.label", new Object[]{email}));
        } else {
            this.lbInfoWithEmail.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.label.onboarding.email_verification_info4.label", new Object[]{"email@unknown.com"}));
        }

    }

    private void accountActivated() {
        this.lbActivationState.setIcon(CHECK_GREEN);
        this.labelActivationDescription.setText(I18N.getMessage(I18N.getGUIBundle(), "gui.label.onboarding.email_verification_info2.label", new Object[0]));
    }

    class VerificationCheckTimerTask extends TimerTask {
        VerificationCheckTimerTask() {
        }

        public void run() {
            EmailVerificationCard.this.checkVerificationStatus.startAndWait();
        }
    }
}
