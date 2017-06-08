package com.rapidminer.tools.nexus;

/**
 * Created by mk on 3/9/16.
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidminer.gui.license.onboarding.ConnectToServiceCard;
import com.rapidminer.gui.license.onboarding.EmailVerificationCard;
import com.rapidminer.gui.license.onboarding.InitialConnectToServiceCard;
import com.rapidminer.gui.license.onboarding.SignUpCard;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.nexus.NexusAuthenticationToken;
import com.rapidminer.tools.nexus.NexusCommunicationException;
import com.rapidminer.tools.nexus.NexusConnectionManager;
import com.rapidminer.tools.nexus.NexusError;
import com.rapidminer.tools.nexus.NexusUtilities;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;

public final class RapidMinerAccount {
    public static final String URL = "https://my.rapidminer.com";
    public static final String ID = "RapidMiner.com account";
    private static final ThreadLocal<DateFormat> ISO_DATE_FORMATTER = new ThreadLocal() {
        protected DateFormat initialValue() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mmXXX", Locale.UK);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format;
        }
    };
    private static final Set<Class<?>> ACCESS_WHITELIST;
    private static final String LOGIN_API_VERSION = "v1";
    private static final String LOGIN_BASE_URI = "/v1/login";
    private static final Map<String, NexusAuthenticationToken> TOKEN_CACHE;

    private RapidMinerAccount() {
        try {
            checkAccess((new RapidMinerAccount.SecurityManagerExtension()).getCaller());
        } catch (IllegalAccessException var2) {
            throw new IllegalStateException();
        }
    }

    public static final NexusAuthenticationToken getRMAccountAuthToken(String username, char[] password) throws IllegalAccessException, NexusCommunicationException, IOException {
        return getRMAccountAuthToken(username, password, false);
    }

    public static final NexusAuthenticationToken getRMAccountAuthToken(String username, char[] password, boolean forceReload) throws IllegalAccessException, NexusCommunicationException, IOException {
        checkAccess((new RapidMinerAccount.SecurityManagerExtension()).getCaller());
        if(username == null) {
            throw new IllegalArgumentException("username must not be null!");
        } else if(password == null) {
            throw new IllegalArgumentException("password must not be null!");
        } else {
            if(forceReload && TOKEN_CACHE.containsKey(username)) {
                TOKEN_CACHE.remove(username);
            }

            if(TOKEN_CACHE.containsKey(username)) {
                NexusAuthenticationToken authConnection = (NexusAuthenticationToken)TOKEN_CACHE.get(username);

                try {
                    if(authConnection != null) {
                        Date code = ((DateFormat)ISO_DATE_FORMATTER.get()).parse(authConnection.getExpirationDate());
                        if(code.after(new Date())) {
                            return authConnection;
                        }
                    }
                } catch (ParseException var6) {
                    ;
                }
            }

            HttpURLConnection authConnection1 = createAuthConnection(username, password);
            int code1 = authConnection1.getResponseCode();
            if(code1 == 200) {
                NexusAuthenticationToken error1 = readAuthTokenFromConnection(authConnection1);
                TOKEN_CACHE.put(username, error1);
                return error1;
            } else if(code1 == 404) {
                throw new NexusCommunicationException(code1);
            } else {
                NexusError error = readErrorFromConnection(authConnection1);
                throw new NexusCommunicationException(error);
            }
        }
    }

    private static HttpURLConnection createAuthConnection(String username, char[] password) throws IllegalAccessException, IOException {
        checkAccess((new RapidMinerAccount.SecurityManagerExtension()).getCaller());
        String nexusLoginURL = "/nexus/rest/v1/login/token";
        URL url = new URL(NexusUtilities.getNexusRESTUrl() + nexusLoginURL);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
        ObjectMapper mapper = new ObjectMapper();
        HashMap jsonMap = new HashMap();
        jsonMap.put("email", username);
        jsonMap.put("password", String.valueOf(password));
        String jsonCredentialsRepresentation = mapper.writeValueAsString(jsonMap);

        try {
            OutputStream e = connection.getOutputStream();
            Throwable var9 = null;

            try {
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(e, StandardCharsets.UTF_8), true);
                byte[] buffer = new byte[4096];
                ByteArrayInputStream jsonIn = new ByteArrayInputStream(jsonCredentialsRepresentation.getBytes(StandardCharsets.UTF_8));

                int length;
                while((length = jsonIn.read(buffer)) > 0) {
                    e.write(buffer, 0, length);
                }

                e.flush();
                writer.close();
                return connection;
            } catch (Throwable var22) {
                var9 = var22;
                throw var22;
            } finally {
                if(e != null) {
                    if(var9 != null) {
                        try {
                            e.close();
                        } catch (Throwable var21) {
                            var9.addSuppressed(var21);
                        }
                    } else {
                        e.close();
                    }
                }

            }
        } catch (IllegalArgumentException var24) {
            throw new IOException(var24);
        }
    }

    private static NexusAuthenticationToken readAuthTokenFromConnection(HttpURLConnection connection) throws NexusCommunicationException {
        try {
            return (NexusAuthenticationToken)NexusUtilities.parseJacksonString(Tools.parseInputStreamToString(connection.getInputStream()), NexusAuthenticationToken.class);
        } catch (IOException var2) {
            LogService.getRoot().log(Level.WARNING, "com.rapidminer.tools.account.RapidMinerAccount.token_parse_failure", var2);
            throw new NexusCommunicationException(500);
        }
    }

    private static NexusError readErrorFromConnection(HttpURLConnection connection) throws NexusCommunicationException {
        try {
            InputStream e = connection == null?null:connection.getErrorStream();
            if(e == null) {
                LogService.getRoot().log(Level.WARNING, "com.rapidminer.tools.account.RapidMinerAccount.error_parse_failure", "No available error stream.");
                throw new NexusCommunicationException(500);
            } else {
                return (NexusError)NexusUtilities.parseJacksonString(Tools.parseInputStreamToString(e), NexusError.class);
            }
        } catch (IOException var2) {
            LogService.getRoot().log(Level.WARNING, "com.rapidminer.tools.account.RapidMinerAccount.error_parse_failure", var2.getMessage());
            throw new NexusCommunicationException(500);
        }
    }

    private static final void checkAccess(Class<?> caller) throws IllegalAccessException {
        if(caller == null) {
            throw new IllegalAccessException();
        } else {
            Iterator var1 = ACCESS_WHITELIST.iterator();

            Class allowed;
            do {
                if(!var1.hasNext()) {
                    throw new IllegalAccessException();
                }

                allowed = (Class)var1.next();
            } while(!allowed.equals(caller));

        }
    }

    static {
        HashSet whitelistSet = new HashSet();
        whitelistSet.add(NexusConnectionManager.class);
        whitelistSet.add(RapidMinerAccount.class);
        whitelistSet.add(EmailVerificationCard.class);
        whitelistSet.add(SignUpCard.class);
        whitelistSet.add(InitialConnectToServiceCard.class);
        whitelistSet.add(ConnectToServiceCard.class);
        ACCESS_WHITELIST = Collections.unmodifiableSet(whitelistSet);
        TOKEN_CACHE = new HashMap();
    }

    private static final class SecurityManagerExtension extends SecurityManager {
        private static final int OFFSET = 4;

        private SecurityManagerExtension() {
        }

        protected Class<?>[] getClassContext() {
            return super.getClassContext();
        }

        private final Class<?> getCaller() {
            try {
                return this.getClassContext()[4];
            } catch (ArrayIndexOutOfBoundsException var2) {
                return null;
            }
        }
    }
}
