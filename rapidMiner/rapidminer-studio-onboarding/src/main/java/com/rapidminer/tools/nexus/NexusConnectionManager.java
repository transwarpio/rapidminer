package com.rapidminer.tools.nexus;

/**
 * Created by mk on 3/9/16.
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.gui.license.GUILicenseManagerListener;
import com.rapidminer.gui.license.LicenseTools;
import com.rapidminer.gui.license.onboarding.ActivationSuccessfulCard;
import com.rapidminer.gui.license.onboarding.OnboardingDialog;
import com.rapidminer.gui.license.onboarding.OnboardingManager.WelcomeType;
import com.rapidminer.gui.security.UserCredential;
import com.rapidminer.license.License;
import com.rapidminer.license.LicenseStatus;
import com.rapidminer.license.LicenseValidationException;
import com.rapidminer.license.UnknownProductException;
import com.rapidminer.license.location.LicenseStoringException;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.WebServiceTools;
import com.rapidminer.tools.nexus.DownloadedLicense;
import com.rapidminer.tools.nexus.EmailVerificationStatus;
import com.rapidminer.tools.nexus.NexusAuthenticationToken;
import com.rapidminer.tools.nexus.NexusCommunicationException;
import com.rapidminer.tools.nexus.NexusError;
import com.rapidminer.tools.nexus.NexusUtilities;
import com.rapidminer.tools.nexus.RapidMinerAccount;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import javax.swing.SwingUtilities;

public enum NexusConnectionManager {
    INSTANCE;

    private static final String LICENSES = "/licenses";
    private static final String TRIAL_URI = "&edition=trial";
    private static final String VERIFICATION_EMAIL_URI = "/send_verification_email/";
    private static final String VERIFICATION_STATUS_URI = "/verification_status/";
    private static final String LICENSES_PRODUCT_URI = "/licenses?product=%s&version=%s";
    private static final String LICENSE_API_VERSION = "v1";
    private static final String LICENSE_BASE_URI = "/v1/users/";
    private static final Charset ENCODING;

    private NexusConnectionManager() {
    }

    private DownloadedLicense installLicenses(DownloadedLicense[] downloadedLicenses) {
        License activeLicense = ProductConstraintManager.INSTANCE.getActiveLicense();
        int bestPrecedence = activeLicense.getPrecedence();
        DownloadedLicense bestNewLicense = null;
        GUILicenseManagerListener.INSTANCE.disableLicenseStoredNotification();
        DownloadedLicense[] var5 = downloadedLicenses;
        int var6 = downloadedLicenses.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            DownloadedLicense license = var5[var7];
            if(!license.isExpired() || "trial".equals(license.getProductEdition())) {
                try {
                    License e = ProductConstraintManager.INSTANCE.installNewLicense(license.getLicenseKey());
                    if("rapidminer-studio".equals(e.getProductId()) && (e.getPrecedence() > bestPrecedence || !"trial".equals(license.getProductEdition()) && "trial".equals(activeLicense.getProductEdition()) && e.getPrecedence() == bestPrecedence) && e.getStatus() == LicenseStatus.VALID) {
                        bestNewLicense = license;
                        bestPrecedence = e.getPrecedence();
                    }
                } catch (UnknownProductException var10) {
                    LogService.getRoot().log(Level.FINE, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.license.LicenseDownloadManager.license_install_unknown", new Object[0]), license.getProductKey());
                } catch (LicenseValidationException | LicenseStoringException var11) {
                    LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.license.LicenseDownloadManager.license_install_error", new Object[0]), var11);
                }
            }
        }

        GUILicenseManagerListener.INSTANCE.enableLicenseStoredNotification();
        return bestNewLicense;
    }

    private boolean installTrial(UserCredential credentials) {
        try {
            NexusAuthenticationToken e = null;

            try {
                e = RapidMinerAccount.getRMAccountAuthToken(credentials.getUsername(), credentials.getPassword());
            } catch (IllegalAccessException var8) {
                LogService.getRoot().log(Level.WARNING, "com.rapidminer.license.LicenseDownloadManager.illegal_account_access");
                return false;
            }

            String userId = e.getIdentityProviderId();
            if(userId == null) {
                throw new IOException("Could not extract user id from authToken");
            } else {
                ProductConstraintManager productConstManager = ProductConstraintManager.INSTANCE;
                String uri = "/v1/users/" + encodeString(userId) + String.format("/licenses?product=%s&version=%s", new Object[]{encodeString(productConstManager.getProduct().getProductId()), encodeString(productConstManager.getProduct().getProductVersion())}) + "&edition=trial";
                String json = this.downloadFromLicenseServer(e, uri, "POST");
                DownloadedLicense trial = (DownloadedLicense)NexusUtilities.parseJacksonString(json, DownloadedLicense.class);
                GUILicenseManagerListener.INSTANCE.disableLicenseStoredNotification();
                ProductConstraintManager.INSTANCE.installNewLicense(trial.getLicenseKey());
                GUILicenseManagerListener.INSTANCE.enableLicenseStoredNotification();
                return true;
            }
        } catch (IOException var9) {
            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.license.LicenseDownloadManager.license_trial_download_error", new Object[0]), var9);
            return false;
        } catch (UnknownProductException | LicenseStoringException | LicenseValidationException var10) {
            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.license.LicenseDownloadManager.license_trial_install_error", new Object[0]), var10);
            return false;
        }
    }

    public void installAvailableLicenses(UserCredential credentials) throws IOException {
        this.installLicenses(credentials);
        this.tryInstallingTrial(credentials);
    }

    private boolean tryInstallingTrial(UserCredential credentials) {
        return ProductConstraintManager.INSTANCE.shouldTrialBeOffered()?this.installTrial(credentials):false;
    }

    private DownloadedLicense installLicenses(UserCredential credentials) throws IOException {
        NexusAuthenticationToken authToken = null;

        try {
            authToken = RapidMinerAccount.getRMAccountAuthToken(credentials.getUsername(), credentials.getPassword());
        } catch (IllegalAccessException var6) {
            LogService.getRoot().log(Level.WARNING, "com.rapidminer.license.LicenseDownloadManager.illegal_account_access");
            return null;
        }

        DownloadedLicense[] licenses;
        try {
            licenses = this.downloadLicenses(authToken);
        } catch (IOException var5) {
            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.license.LicenseDownloadManager.license_download_error", new Object[0]), var5);
            throw var5;
        }

        return this.installLicenses(licenses);
    }

    public void checkLicenses(UserCredential credentials) throws NexusCommunicationException, IOException {
        DownloadedLicense bestLicense = this.installLicenses(credentials);
        boolean trialInstalled = this.tryInstallingTrial(credentials);
        if(bestLicense != null || trialInstalled) {
            this.showActivationSuccessDialog(ProductConstraintManager.INSTANCE.getActiveLicense());
        }

    }

    private void showActivationSuccessDialog(final License activeLicense) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String additionalInfo = null;
                if("trial".equals(activeLicense.getProductEdition())) {
                    additionalInfo = ActivationSuccessfulCard.getRemaingTrialDaysMessage(activeLicense);
                }

                OnboardingDialog onboardingDialog = new OnboardingDialog(WelcomeType.WELCOME_REMINDER);
                onboardingDialog.showActivationSuccessfulCard(activeLicense.getLicenseUser().getName(), LicenseTools.translateProductEdition(activeLicense), activeLicense.getExpirationDate() != null?df.format(activeLicense.getExpirationDate()):I18N.getGUILabel("license.no_end_date", new Object[0]), true, additionalInfo);
                onboardingDialog.setVisible(true);
            }
        });
    }

    private DownloadedLicense[] downloadLicenses(NexusAuthenticationToken authToken) throws IOException {
        String userId = authToken.getIdentityProviderId();
        if(userId == null) {
            throw new IOException("Could not extract user id from authToken");
        } else {
            String uri = "/v1/users/" + encodeString(userId) + "/licenses";
            String json = this.downloadFromLicenseServer(authToken, uri, "GET");
            return (DownloadedLicense[])NexusUtilities.parseJacksonString(json, DownloadedLicense[].class);
        }
    }

    private String downloadFromLicenseServer(NexusAuthenticationToken authToken, String uri, String typeString) throws IOException {
        HttpURLConnection connection = null;
        URL url = new URL(NexusUtilities.getNexusRESTUrl() + "/nexus/rest" + uri);
        connection = (HttpURLConnection)url.openConnection();
        WebServiceTools.setURLConnectionDefaults(connection);
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setRequestMethod(typeString);
        connection.setRequestProperty("Auth-Token", authToken.getAuthenticationToken());
        int code = connection.getResponseCode();
        if(code != 200 && code != 201) {
            this.handleConnectionErrorStream(connection, code);
            return null;
        } else {
            return Tools.parseInputStreamToString(connection.getInputStream());
        }
    }

    public static int getRemainingDays(License license) {
        Date expirationDate = license.getExpirationDate();
        if(expirationDate == null) {
            return 2147483647;
        } else {
            Calendar now = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();
            endDate.setTime(expirationDate);

            int daysBetween;
            for(daysBetween = 0; now.before(endDate); ++daysBetween) {
                now.add(5, 1);
            }

            return daysBetween;
        }
    }

    public void createUser(UserCredential credentials) throws IOException {
        HttpURLConnection connection = null;
        URL url = new URL(NexusUtilities.getNexusRESTUrl() + "/nexus/rest" + "/v1/users/");
        connection = (HttpURLConnection)url.openConnection();
        WebServiceTools.setURLConnectionDefaults(connection);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("content-type", "application/json");
        ObjectMapper mapper = new ObjectMapper();
        HashMap jsonMap = new HashMap();
        jsonMap.put("email", credentials.getUsername());
        jsonMap.put("password", credentials.getPassword());
        String jsonCredentialsRepresentation = mapper.writeValueAsString(jsonMap);
        OutputStream code = connection.getOutputStream();
        Throwable var8 = null;

        try {
            code.write(jsonCredentialsRepresentation.getBytes(StandardCharsets.UTF_8));
        } catch (Throwable var17) {
            var8 = var17;
            throw var17;
        } finally {
            if(code != null) {
                if(var8 != null) {
                    try {
                        code.close();
                    } catch (Throwable var16) {
                        var8.addSuppressed(var16);
                    }
                } else {
                    code.close();
                }
            }

        }

        int code1 = connection.getResponseCode();
        if(code1 != 200 && code1 != 201) {
            this.handleConnectionErrorStream(connection, code1);
        }

    }

    public boolean isEmailVerified(NexusAuthenticationToken token) throws IOException {
        HttpURLConnection connection = null;
        String userId = token.getIdentityProviderId();
        if(userId == null) {
            throw new IOException("Could not extract user id from authToken");
        } else {
            URL url = new URL(NexusUtilities.getNexusRESTUrl() + "/nexus/rest" + "/v1/users/" + encodeString(userId) + "/verification_status/");
            connection = (HttpURLConnection)url.openConnection();
            WebServiceTools.setURLConnectionDefaults(connection);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Auth-Token", token.getAuthenticationToken());
            int code = connection.getResponseCode();
            if(code != 200 && code != 201) {
                this.handleConnectionErrorStream(connection, code);
                return false;
            } else {
                String json = Tools.parseInputStreamToString(connection.getInputStream());
                EmailVerificationStatus status = (EmailVerificationStatus)NexusUtilities.parseJacksonString(json, EmailVerificationStatus.class);
                return status.isEmailVerified();
            }
        }
    }

    public void sendVerificationEmail(NexusAuthenticationToken token) throws IOException {
        String userId = token.getIdentityProviderId();
        if(userId == null) {
            throw new IOException("Could not extract user id from authToken");
        } else {
            String uri = "/v1/users/" + encodeString(userId) + "/send_verification_email/";
            HttpURLConnection connection = null;
            URL url = new URL(NexusUtilities.getNexusRESTUrl() + "/nexus/rest" + uri);
            connection = (HttpURLConnection)url.openConnection();
            WebServiceTools.setURLConnectionDefaults(connection);
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Auth-Token", token.getAuthenticationToken());
            int code = connection.getResponseCode();
            if(code != 200 && code != 201) {
                this.handleConnectionErrorStream(connection, code);
            }

        }
    }

    public final boolean hasUserAcceptedCloudEULA(String username, char[] password) throws NexusCommunicationException {
        short code = 500;

        try {
            NexusAuthenticationToken e = RapidMinerAccount.getRMAccountAuthToken(username, password);
            String userId = e.getIdentityProviderId();
            if(userId == null) {
                throw new IOException("Could not extract user id from authToken");
            } else {
                HttpURLConnection connection = null;
                URL url = new URL(NexusUtilities.getNexusRESTUrl() + "/nexus/rest" + "/v1/users/" + URLEncoder.encode(userId, StandardCharsets.UTF_8.name()) + "/affirmations/cloud-eula");
                connection = (HttpURLConnection)url.openConnection();
                WebServiceTools.setURLConnectionDefaults(connection);
                connection.setDoInput(true);
                connection.setDoOutput(false);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Auth-Token", e.getAuthenticationToken());
                int code1 = connection.getResponseCode();
                if(code1 == 200) {
                    return true;
                } else if(code1 == 404) {
                    return false;
                } else {
                    String errorString = Tools.parseInputStreamToString(connection.getErrorStream());

                    NexusError error;
                    try {
                        error = (NexusError)NexusUtilities.parseJacksonString(errorString, NexusError.class);
                    } catch (IOException var11) {
                        throw new NexusCommunicationException(code1);
                    }

                    throw new NexusCommunicationException(error);
                }
            }
        } catch (IllegalAccessException var12) {
            LogService.getRoot().log(Level.WARNING, "Failed to read Cloud EULA status.", var12);
            return false;
        } catch (NexusCommunicationException var13) {
            throw var13;
        } catch (IOException var14) {
            throw new NexusCommunicationException(code);
        }
    }

    public final void storeUserAcceptedCloudEULA(String username, char[] password) throws NexusCommunicationException {
        short code = 500;

        try {
            NexusAuthenticationToken e = RapidMinerAccount.getRMAccountAuthToken(username, password);
            String userId = e.getIdentityProviderId();
            if(userId == null) {
                throw new IOException("Could not extract user id from authToken");
            }

            HttpURLConnection connection = null;
            URL url = new URL(NexusUtilities.getNexusRESTUrl() + "/nexus/rest" + "/v1/users/" + URLEncoder.encode(userId, StandardCharsets.UTF_8.name()) + "/affirmations/cloud-eula");
            connection = (HttpURLConnection)url.openConnection();
            WebServiceTools.setURLConnectionDefaults(connection);
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Auth-Token", e.getAuthenticationToken());
            int code1 = connection.getResponseCode();
            if(code1 != 200 && code1 != 201) {
                String errorString = Tools.parseInputStreamToString(connection.getErrorStream());

                NexusError error;
                try {
                    error = (NexusError)NexusUtilities.parseJacksonString(errorString, NexusError.class);
                } catch (IOException var11) {
                    throw new NexusCommunicationException(code1);
                }

                throw new NexusCommunicationException(error);
            }
        } catch (IllegalAccessException var12) {
            LogService.getRoot().log(Level.WARNING, "Failed to store Cloud EULA status.", var12);
        } catch (NexusCommunicationException var13) {
            throw var13;
        } catch (IOException var14) {
            throw new NexusCommunicationException(code);
        }

    }

    private void handleConnectionErrorStream(HttpURLConnection connection, int responseCode) throws IOException {
        String errorString = Tools.parseInputStreamToString(connection.getErrorStream());

        NexusError error;
        try {
            error = (NexusError)NexusUtilities.parseJacksonString(errorString, NexusError.class);
        } catch (IOException var6) {
            throw new NexusCommunicationException(responseCode);
        }

        throw new NexusCommunicationException(error);
    }

    private static final String encodeString(String string) {
        try {
            return URLEncoder.encode(string, ENCODING.name());
        } catch (UnsupportedEncodingException var2) {
            throw new RuntimeException(ENCODING.name() + " encoding is not supported!");
        }
    }

    static {
        ENCODING = StandardCharsets.UTF_8;
    }
}
