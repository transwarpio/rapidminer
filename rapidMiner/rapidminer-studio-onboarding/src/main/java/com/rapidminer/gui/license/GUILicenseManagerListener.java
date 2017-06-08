package com.rapidminer.gui.license;

import com.rapidminer.RapidMiner;
import com.rapidminer.gui.MainFrame;
import com.rapidminer.gui.RapidMinerGUI;
import com.rapidminer.gui.actions.UpgradeLicenseAction;
import com.rapidminer.gui.license.LicenseTools;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.license.onboarding.OnboardingManager;
import com.rapidminer.gui.license.onboarding.OnboardingManager.ReminderType;
import com.rapidminer.gui.tools.NotificationPopup;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.NotificationPopup.PopupLocation;
import com.rapidminer.license.License;
import com.rapidminer.license.LicenseEvent;
import com.rapidminer.license.LicenseManagerListener;
import com.rapidminer.license.LicenseEvent.LicenseEventType;
import com.rapidminer.license.violation.LicenseConstraintViolation;
import com.rapidminer.license.violation.LicenseLevelViolation;
import com.rapidminer.license.violation.LicenseViolation;
import com.rapidminer.license.violation.LicenseViolation.ViolationType;
import com.rapidminer.operator.Operator;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public enum GUILicenseManagerListener implements LicenseManagerListener {
    INSTANCE;

    private static final String LICENSE_CONSTRAINT_VIOLATION = "license.constraint_violation.";
    private static final String RMX = "rmx_";
    private static final String LICENSE_LEVEL_VIOLATION = "license.license_level_violation.";
    private static final int NOTIFICATION_POPUP_DELAY_SUCCESS = 5000;
    private static final int NOTIFICATION_POPUP_DELAY_ERROR = 20000;
    private static final int NOTIFICATION_POPUP_PADDING_X = 35;
    private static final int NOTIFICATION_POPUP_PADDING_Y = 50;
    private boolean showLicenseStoredPopup = true;

    private GUILicenseManagerListener() {
    }

    public void disableLicenseStoredNotification() {
        this.showLicenseStoredPopup = false;
    }

    public void enableLicenseStoredNotification() {
        this.showLicenseStoredPopup = true;
    }

    public <S, C> void handleLicenseEvent(LicenseEvent<S, C> event) {
        LicenseEventType type = event.getType();
        switch(type.ordinal()) {
            case 1:
                this.licenseViolated(event.getLicenseViolations());
                break;
            case 2:
                this.licenseExpired(event.getExpiredLicense(), event.getNewLicense());
                break;
            case 3:
                this.activeLicenseChanged(event.getNewLicense());
                break;
            case 4:
                this.showLicenseStoredNotification(event.getNewLicense());
                break;
            default:
                throw new RuntimeException("Unknown license event type: " + type);
        }

    }

    private void licenseViolated(List<LicenseViolation> licenseViolations) {
        if(licenseViolations.size() > 1) {
            Iterator violation = licenseViolations.iterator();

            while(violation.hasNext()) {
                LicenseViolation violation1 = (LicenseViolation)violation.next();
                if(violation1.getViolationType() == ViolationType.LICENSE_CONSTRAINT_VIOLATED) {
                    this.showLicenseConstraintViolationDialog((LicenseConstraintViolation)violation1);
                    break;
                }

                if(violation1.getViolationType() == ViolationType.LICENSE_LEVEL_VIOLATED) {
                    this.showLicenseLevelViolationDialog((LicenseLevelViolation)violation1);
                    break;
                }
            }
        } else {
            LicenseViolation violation2 = licenseViolations.get(0);
            switch(violation2.getViolationType().ordinal()) {
                case 1:
                    this.showLicenseConstraintViolationDialog((LicenseConstraintViolation)violation2);
                    break;
                case 2:
                    this.showLicenseLevelViolationDialog((LicenseLevelViolation)violation2);
                    break;
                default:
                    throw new RuntimeException("Unknown violation type " + violation2.getViolationType());
            }
        }

    }

    private void showLicenseLevelViolationDialog(LicenseLevelViolation violation) {
        License violatingLicense = violation.getLicense();
        if(violatingLicense != null) {
            String key = "";
            StringBuilder keyBuilder = new StringBuilder("license.license_level_violation.");
            String i18nKey = violation.getI18nKey();
            if(i18nKey != null && !i18nKey.isEmpty()) {
                keyBuilder.append(i18nKey);
                key = keyBuilder.toString();
            } else {
                String dialog;
                if(violation.getViolatingObject() instanceof Operator) {
                    dialog = ((Operator)violation.getViolatingObject()).getOperatorDescription().getProviderNamespace();
                    keyBuilder.append(dialog.replace("rmx_", ""));
                    keyBuilder.append(".");
                }

                keyBuilder.append(violation.getLicenseAnnotation().productId());
                key = keyBuilder.toString();
                dialog = "gui.dialog." + key + ".message";
                String newFormatMessage = I18N.getMessage(I18N.getGUIBundle(), dialog, new Object[0]);
                if(newFormatMessage.equals(dialog)) {
                    StringBuilder oldFormatKeyBuilder = new StringBuilder("license.license_level_violation.");
                    oldFormatKeyBuilder.append(violation.getLicenseAnnotation().productId());
                    key = oldFormatKeyBuilder.toString();
                }
            }

            LicenseUpgradeDialog dialog1 = new LicenseUpgradeDialog(new UpgradeLicenseAction(), false, key, true, false, new Object[]{LicenseTools.translateProductName(violatingLicense), LicenseTools.translateProductEdition(violatingLicense)});
            dialog1.setVisible(true);
        }
    }

    private void showLicenseConstraintViolationDialog(LicenseConstraintViolation<?, ?> constraintViolation) {
        License violatingLicense = constraintViolation.getLicense();
        if(violatingLicense == null) {
            LogService.getRoot().log(Level.SEVERE, "com.rapidminer.gui.license.GUILicenseManagerListener.no_license_available");
        } else {
            String i18nKey = constraintViolation.getI18nKey();
            StringBuilder keyBuilder = new StringBuilder("license.constraint_violation.");
            if(i18nKey != null && !i18nKey.isEmpty()) {
                keyBuilder.append(i18nKey);
            } else {
                keyBuilder.append(constraintViolation.getConstraint().getKey());
            }

            LicenseUpgradeDialog dialog = new LicenseUpgradeDialog(new UpgradeLicenseAction(), false, keyBuilder.toString(), true, false, new Object[]{LicenseTools.translateProductName(violatingLicense), LicenseTools.translateProductEdition(violatingLicense), constraintViolation.getConstraintValue(), constraintViolation.getViolatingValue()});
            dialog.setVisible(true);
        }
    }

    private void activeLicenseChanged(License newLicense) {
        RapidMinerGUI.getMainFrame().setTitle();
        if(newLicense.getProductId().equals("rapidminer-studio")) {
            OnboardingManager.INSTANCE.resetLicenseExpirationReminder();
        }

        LicenseTools.storeActiveLicenseProperties(newLicense);
    }

    private void licenseExpired(License expiredLicense, License newLicense) {
        if(expiredLicense.getPrecedence() > newLicense.getPrecedence() && expiredLicense.getProductId().equals("rapidminer-studio") && newLicense.getProductId().equals("rapidminer-studio")) {
            OnboardingDialog onboarding;
            if(expiredLicense.getProductEdition().equalsIgnoreCase("trial")) {
                onboarding = new OnboardingDialog(ReminderType.EXPIRED);
                onboarding.showTrialReminderCard();
                onboarding.setVisible(true);
            } else if(newLicense.getProductEdition().equalsIgnoreCase("community") || newLicense.getProductEdition().equalsIgnoreCase("starter")) {
                onboarding = new OnboardingDialog(ReminderType.EXPIRED);
                onboarding.showExpirationReminderCard(LicenseTools.translateProductEdition(expiredLicense), LicenseTools.translateProductEdition(newLicense));
                onboarding.setVisible(true);
            }
        }

    }

    private void showLicenseStoredNotification(License license) {
        if(!RapidMiner.getExecutionMode().isHeadless() && this.showLicenseStoredPopup) {
            JPanel notificationPanel = new JPanel();
            notificationPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            JLabel notifactionLabel;
            ImageIcon icon;
            Integer fadeDelay;
            if(license.getExpirationDate() != null && (new Date()).after(license.getExpirationDate())) {
                icon = SwingTools.createIcon("48/" + I18N.getMessage(I18N.getGUIBundle(), "gui.notification.license.stored_expired.icon", new Object[0]));
                notifactionLabel = new JLabel(I18N.getMessage(I18N.getGUIBundle(), "gui.notification.license.stored_expired.label", new Object[]{LicenseTools.translateProductName(license), LicenseTools.translateProductEdition(license)}));
                fadeDelay = Integer.valueOf(20000);
            } else if(license.getStartDate() != null && license.getStartDate().after(new Date())) {
                icon = SwingTools.createIcon("48/" + I18N.getMessage(I18N.getGUIBundle(), "gui.notification.license.stored_future.icon", new Object[0]));
                notifactionLabel = new JLabel(I18N.getMessage(I18N.getGUIBundle(), "gui.notification.license.stored_future.label", new Object[]{LicenseTools.translateProductName(license), LicenseTools.translateProductEdition(license)}));
                fadeDelay = Integer.valueOf(20000);
            } else {
                icon = SwingTools.createIcon("48/" + I18N.getMessage(I18N.getGUIBundle(), "gui.notification.license.stored_ok.icon", new Object[0]));
                notifactionLabel = new JLabel(I18N.getMessage(I18N.getGUIBundle(), "gui.notification.license.stored_ok.label", new Object[]{LicenseTools.translateProductName(license), LicenseTools.translateProductEdition(license)}));
                fadeDelay = Integer.valueOf(5000);
            }

            notifactionLabel.setIcon(icon);
            notifactionLabel.setBorder(new EmptyBorder(0, 10, 10, 0));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 0, 10);
            notificationPanel.add(notifactionLabel, gbc);
            NotificationPopup.showFadingPopup(notificationPanel, MainFrame.getApplicationFrame(), PopupLocation.LOWER_RIGHT, fadeDelay.intValue(), 35, 50, BorderFactory.createLineBorder(Color.GRAY, 1, false));
        }
    }
}
