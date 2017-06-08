package com.rapidminer.gui.license.onboarding;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.license.License;
import com.rapidminer.tools.FileSystemService;
import com.rapidminer.tools.LogService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public enum OnboardingManager {
    INSTANCE;

    private static final int JOIN_COMMUNITY_REMINDER_INTERVAL = 14;
    private static final String PROPERTIES_ONBOARDING = "onboarding.properties";
    private static final String LAST_COMMUNITY_REMINDER_KEY = "last_community_reminder";
    private static final String NEXT_LICENSE_EXPIRATION_REMINDER_KEY = "next_license_expiration_reminder";
    private static final String WELCOME_SCREEN_SHOWN = "welcome_screen_shown";
    private static final ThreadLocal<DateFormat> ISO_DATE_FORMATTER;
    private Properties properties;

    private OnboardingManager() {
        this.loadProperties();
    }

    public void putProperty(String key, String value) {
        this.properties.put(key, value);
        this.saveProperties();
    }

    public String getProperty(String key) {
        Object result = this.properties.get(key);
        return result != null?String.valueOf(result):null;
    }

    public boolean welcomeScreenShown() {
        String welcome = this.getProperty("welcome_screen_shown");
        return welcome != null?welcome.equals("true"):false;
    }

    public void rememberWelcomeScreenShown() {
        this.putProperty("welcome_screen_shown", "true");
    }

    public boolean repeatJoinCommunityReminder() {
        String lastReminderString = this.getProperty("last_community_reminder");
        if(lastReminderString == null) {
            return true;
        } else {
            Date lastReminder = null;

            try {
                lastReminder = ((DateFormat)ISO_DATE_FORMATTER.get()).parse(lastReminderString);
            } catch (ParseException var5) {
                return true;
            }

            long diffMillis = System.currentTimeMillis() - lastReminder.getTime();
            return TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS) >= 14L;
        }
    }

    public boolean showLicenseExpirationReminder() {
        String nextLicenseExpirationReminderString = this.getProperty("next_license_expiration_reminder");
        int nextLicenseExpirationReminder = OnboardingManager.TimeTillExpiration.NEVER.getDays();
        if(nextLicenseExpirationReminderString != null) {
            nextLicenseExpirationReminder = Integer.parseInt(nextLicenseExpirationReminderString);
        }

        if(nextLicenseExpirationReminder != OnboardingManager.TimeTillExpiration.NEVER.getDays()) {
            License activeLicense = ProductConstraintManager.INSTANCE.getActiveLicense();
            Date expirationDate = activeLicense.getExpirationDate();
            if(expirationDate != null) {
                long diffMillis = expirationDate.getTime() - System.currentTimeMillis();
                int daysUntilExpiration = (int)TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
                if(daysUntilExpiration > 0) {
                    OnboardingManager.TimeTillExpiration nextReminder = OnboardingManager.TimeTillExpiration.getNextCheck(daysUntilExpiration);
                    boolean showExpirationDialog = OnboardingManager.TimeTillExpiration.showReminder(OnboardingManager.TimeTillExpiration.getTimeTillExpiration(nextLicenseExpirationReminder), nextReminder);
                    if(showExpirationDialog) {
                        this.putProperty("next_license_expiration_reminder", String.valueOf(nextReminder.getDays()));
                    }

                    return showExpirationDialog;
                }
            }
        }

        return false;
    }

    public void resetJoinCommunityReminder() {
        Date now = new Date();
        String dateString = ((DateFormat)ISO_DATE_FORMATTER.get()).format(now);
        this.putProperty("last_community_reminder", dateString);
    }

    public void resetLicenseExpirationReminder() {
        License activeLicense = ProductConstraintManager.INSTANCE.getActiveLicense();
        OnboardingManager.TimeTillExpiration nextCheck = OnboardingManager.TimeTillExpiration.NEVER;
        if(!activeLicense.getProductEdition().equals("community") && !activeLicense.getProductEdition().equals("starter") && !activeLicense.getProductEdition().equals("trial")) {
            Date expirationDate = activeLicense.getExpirationDate();
            if(expirationDate != null) {
                long diffMillis = expirationDate.getTime() - System.currentTimeMillis();
                int daysUntilExpiration = (int)TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
                nextCheck = OnboardingManager.TimeTillExpiration.getNextCheck(daysUntilExpiration);
            }
        }

        this.putProperty("next_license_expiration_reminder", String.valueOf(nextCheck.getDays()));
    }

    private void loadProperties() {
        this.properties = new Properties();
        File file = FileSystemService.getUserConfigFile("onboarding.properties");

        try {
            if(!file.exists() && !file.createNewFile()) {
                throw new IOException("Error creating oboarding.properties file");
            }

            FileInputStream e = new FileInputStream(file);
            Throwable var3 = null;

            try {
                this.properties.load(e);
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        e.close();
                    }
                }

            }
        } catch (IOException var15) {
            LogService.getRoot().log(Level.INFO, "com.rapidminer.gui.license.onboarding.properties_load_error", var15);
        }

    }

    private void saveProperties() {
        File file = FileSystemService.getUserConfigFile("onboarding.properties");

        try {
            FileOutputStream e = new FileOutputStream(file);
            Throwable var3 = null;

            try {
                this.properties.store(e, (String)null);
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        e.close();
                    }
                }

            }
        } catch (IOException var15) {
            LogService.getRoot().log(Level.INFO, "com.rapidminer.gui.license.onboarding.properties_save_error", var15);
        }

    }

    static {
        ISO_DATE_FORMATTER = new ThreadLocal() {
            protected DateFormat initialValue() {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mmXXX", Locale.UK);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                return format;
            }
        };
    }

    public static enum TimeTillExpiration {
        THREE_MONTHS(90),
        TWO_MONTHS(60),
        ONE_MONTH(30),
        THREE_WEEKS(21),
        TWO_WEEKS(14),
        ONE_WEEK(7),
        NEVER(-1);

        private int days = -1;

        private TimeTillExpiration(int days) {
            this.days = days;
        }

        public int getDays() {
            return this.days;
        }

        public static OnboardingManager.TimeTillExpiration getTimeTillExpiration(int days) {
            return days == THREE_MONTHS.getDays()?THREE_MONTHS:(days == TWO_MONTHS.getDays()?TWO_MONTHS:(days == ONE_MONTH.getDays()?ONE_MONTH:(days == THREE_WEEKS.getDays()?THREE_WEEKS:(days == TWO_WEEKS.getDays()?TWO_WEEKS:(days == ONE_WEEK.getDays()?ONE_WEEK:NEVER)))));
        }

        public static OnboardingManager.TimeTillExpiration getNextCheck(int daysLeft) {
            return daysLeft > THREE_MONTHS.getDays()?THREE_MONTHS:(daysLeft > TWO_MONTHS.getDays()?TWO_MONTHS:(daysLeft > ONE_MONTH.getDays()?ONE_MONTH:(daysLeft > THREE_WEEKS.getDays()?THREE_WEEKS:(daysLeft > TWO_WEEKS.getDays()?TWO_WEEKS:(daysLeft > ONE_WEEK.getDays()?ONE_WEEK:NEVER)))));
        }

        public static boolean showReminder(OnboardingManager.TimeTillExpiration savedTime, OnboardingManager.TimeTillExpiration nextTime) {
            return nextTime.getDays() < savedTime.getDays();
        }
    }

    public static enum ReminderType {
        REMINDER,
        EXPIRED;

        private ReminderType() {
        }
    }

    public static enum WelcomeType {
        FIRST_WELCOME,
        WELCOME_REMINDER,
        COMMUNITY_FEATURE;

        private WelcomeType() {
        }
    }
}
