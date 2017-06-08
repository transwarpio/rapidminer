package com.rapidminer.gui.license.onboarding;

import com.rapidminer.RapidMiner;
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.gui.ApplicationFrame;
import com.rapidminer.gui.MainFrame;
import com.rapidminer.gui.internal.GUIStartupListener;
import com.rapidminer.gui.license.LicenseTools;
import com.rapidminer.gui.license.onboarding.OnboardingManager.ReminderType;
import com.rapidminer.gui.license.onboarding.OnboardingManager.WelcomeType;
import com.rapidminer.gui.security.UserCredential;
import com.rapidminer.gui.security.Wallet;
import com.rapidminer.gui.startup.GettingStartedDialog;
import com.rapidminer.gui.tools.ProgressThread;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.license.License;
import com.rapidminer.parameter.ParameterTypeString;
import com.rapidminer.studio.internal.StartupDialogProvider;
import com.rapidminer.studio.internal.StartupDialogRegistry;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.nexus.NexusCommunicationException;
import com.rapidminer.tools.nexus.NexusConnectionManager;
import com.rapidminer.tools.nexus.NexusUtilities;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class OnboardingGUIStartupListener implements GUIStartupListener {
    private static final int TRIAL_WARNING = 7;
    private Properties lastActiveLicenseProperties;
    private GettingStartedDialog dialog;

    public OnboardingGUIStartupListener() {
        RapidMiner.registerParameter(new ParameterTypeString("account_server_url", "", "https://nexus.rapidminer.com"));
    }

    public void splashWillBeShown() {
        this.lastActiveLicenseProperties = LicenseTools.loadLastActiveLicenseProperties();
        StartupDialogRegistry.INSTANCE.register(new StartupDialogProvider() {
            public void show(ToolbarButton button) {
                (new GettingStartedDialog(button)).setVisible(true);
            }
        });
    }

    public void mainFrameInitialized(MainFrame mainFrame) {
        SwingTools.invokeAndWait(new Runnable() {
            public void run() {
                OnboardingGUIStartupListener.this.dialog = new GettingStartedDialog();
            }
        });
    }

    public void splashWasHidden() {
        if(!ProductConstraintManager.INSTANCE.wasAnyLicenseActivated()) {
            if(!OnboardingManager.INSTANCE.welcomeScreenShown()) {
                SwingTools.invokeLater(new Runnable() {
                    public void run() {
                        OnboardingDialog onboarding = new OnboardingDialog(WelcomeType.FIRST_WELCOME);
                        onboarding.showWelcomeCard();
                        onboarding.setVisible(true);
                        OnboardingManager.INSTANCE.rememberWelcomeScreenShown();
                        OnboardingGUIStartupListener.this.dialog.setLocationRelativeTo(ApplicationFrame.getApplicationFrame());
                        OnboardingGUIStartupListener.this.dialog.setVisible(true);
                        OnboardingGUIStartupListener.this.dialog = null;
                    }
                });
            } else if(OnboardingManager.INSTANCE.repeatJoinCommunityReminder()) {
                SwingTools.invokeLater(new Runnable() {
                    public void run() {
                        OnboardingDialog onboarding = new OnboardingDialog(WelcomeType.WELCOME_REMINDER);
                        onboarding.showWelcomeCard();
                        onboarding.setVisible(true);
                        OnboardingGUIStartupListener.this.dialog.setLocationRelativeTo(ApplicationFrame.getApplicationFrame());
                        OnboardingGUIStartupListener.this.dialog.setVisible(true);
                        OnboardingGUIStartupListener.this.dialog = null;
                    }
                });
            } else {
                SwingTools.invokeLater(new Runnable() {
                    public void run() {
                        OnboardingGUIStartupListener.this.dialog.setLocationRelativeTo(ApplicationFrame.getApplicationFrame());
                        OnboardingGUIStartupListener.this.dialog.setVisible(true);
                        OnboardingGUIStartupListener.this.dialog = null;
                    }
                });
            }
        } else {
            this.checkForResetReminderInterval();
            boolean expired = this.checkForLicenseExpirationDialog(this.lastActiveLicenseProperties);
            if(!expired) {
                this.checkForExpirationReminderDialog();
            }

            SwingTools.invokeLater(new Runnable() {
                public void run() {
                    OnboardingGUIStartupListener.this.dialog.setLocationRelativeTo(ApplicationFrame.getApplicationFrame());
                    OnboardingGUIStartupListener.this.dialog.setVisible(true);
                    OnboardingGUIStartupListener.this.dialog = null;
                }
            });
            final UserCredential authentication = Wallet.getInstance().getEntry("RapidMiner.com account", "https://my.rapidminer.com");
            if(authentication != null) {
                ProgressThread downloadLicenses = new ProgressThread("download_licenses") {
                    public void run() {
                        try {
                            NexusConnectionManager.INSTANCE.checkLicenses(authentication);
                        } catch (NexusCommunicationException var2) {
                            LogService.getRoot().log(Level.WARNING, NexusUtilities.translateApplicationStatusCode(var2.getApplicationStatusCode()));
                            if(var2.getApplicationStatusCode().equals("AUTH-11")) {
                                SwingTools.invokeLater(new Runnable() {
                                    public void run() {
                                        OnboardingDialog onboarding = new OnboardingDialog(WelcomeType.WELCOME_REMINDER);
                                        onboarding.showConnectToService(I18N.getGUILabel("nexus.auth_error", new Object[0]));
                                        onboarding.setVisible(true);
                                    }
                                });
                            }
                        } catch (IOException var3) {
                            LogService.getRoot().log(Level.SEVERE, "com.rapidminer.gui.RapidMinerGUI.license_io_error", var3);
                        }

                    }
                };
                downloadLicenses.start();
            }
        }

    }

    public void startupCompleted() {
    }

    private boolean checkForLicenseExpirationDialog(Properties lastActiveLicenseProperties) {
        final License activeLicense = ProductConstraintManager.INSTANCE.getActiveLicense();
        String precedenceProperty = lastActiveLicenseProperties.getProperty("license.last_active.precedence");
        String productIDProperty = lastActiveLicenseProperties.getProperty("license.last_active.product_id");
        final String productEditionProperty = lastActiveLicenseProperties.getProperty("license.last_active.product_edition");
        if(precedenceProperty != null && productIDProperty != null && productEditionProperty != null) {
            Integer precedence = Integer.valueOf(precedenceProperty);
            if(precedence.intValue() > activeLicense.getPrecedence()) {
                if(productEditionProperty.equalsIgnoreCase("trial")) {
                    SwingTools.invokeAndWait(new Runnable() {
                        public void run() {
                            OnboardingDialog onboarding = new OnboardingDialog(ReminderType.EXPIRED);
                            onboarding.showTrialReminderCard();
                            onboarding.setVisible(true);
                        }
                    });
                    return true;
                }

                if(activeLicense.getProductEdition().equalsIgnoreCase("community") || activeLicense.getProductEdition().equalsIgnoreCase("starter")) {
                    SwingTools.invokeAndWait(new Runnable() {
                        public void run() {
                            String previousProductEdition = I18N.getMessage(I18N.getGUIBundle(), "gui.license.rapidminer-studio." + productEditionProperty + ".label", new Object[0]);
                            OnboardingDialog onboarding = new OnboardingDialog(ReminderType.EXPIRED);
                            onboarding.showExpirationReminderCard(previousProductEdition, LicenseTools.translateProductEdition(activeLicense));
                            onboarding.setVisible(true);
                        }
                    });
                    return true;
                }
            }
        }

        return false;
    }

    private void checkForExpirationReminderDialog() {
        final License activeLicense = ProductConstraintManager.INSTANCE.getActiveLicense();
        final License upcomingLicense = ProductConstraintManager.INSTANCE.getUpcomingLicense();
        final Date expirationDate = activeLicense.getExpirationDate();
        if("trial".equals(activeLicense.getProductEdition())) {
            if(expirationDate != null) {
                long diffMillis = expirationDate.getTime() - System.currentTimeMillis();
                if(TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS) < 7L) {
                    SwingTools.invokeAndWait(new Runnable() {
                        public void run() {
                            OnboardingDialog onboarding = new OnboardingDialog(ReminderType.REMINDER);
                            onboarding.showTrialReminderCard(expirationDate);
                            onboarding.setVisible(true);
                        }
                    });
                }
            }
        } else if(activeLicense.getPrecedence() > upcomingLicense.getPrecedence() && OnboardingManager.INSTANCE.showLicenseExpirationReminder()) {
            SwingTools.invokeAndWait(new Runnable() {
                public void run() {
                    OnboardingDialog onboarding = new OnboardingDialog(ReminderType.REMINDER);
                    onboarding.showExpirationReminderCard(expirationDate, LicenseTools.translateProductEdition(activeLicense), LicenseTools.translateProductEdition(upcomingLicense));
                    onboarding.setVisible(true);
                }
            });
        }

    }

    private void checkForResetReminderInterval() {
        License activeLicense = ProductConstraintManager.INSTANCE.getActiveLicense();
        Date expirationDate = activeLicense.getExpirationDate();
        String lastEdition = this.lastActiveLicenseProperties.getProperty("license.last_active.product_edition");
        String lastExpirationDate = this.lastActiveLicenseProperties.getProperty("license.last_active.expiration_date");
        boolean isEditionDifferent = lastEdition == null || !lastEdition.equals(activeLicense.getProductEdition());
        boolean isExpirationDateDifferent;
        if(expirationDate != null) {
            isExpirationDateDifferent = !(LicenseTools.ISO_DATE_FORMATTER.get()).format(expirationDate).equals(lastExpirationDate);
        } else {
            isExpirationDateDifferent = lastExpirationDate != null;
        }

        if(isEditionDifferent || isExpirationDateDifferent) {
            OnboardingManager.INSTANCE.resetLicenseExpirationReminder();
        }

    }
}
