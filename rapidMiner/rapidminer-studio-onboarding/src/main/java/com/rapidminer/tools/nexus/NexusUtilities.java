package com.rapidminer.tools.nexus;

/**
 * Created by mk on 3/9/16.
 */
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.ParameterService;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public final class NexusUtilities {
    private static final Charset ENCODING;
    public static final String DEFAULT_ACCOUNT_SERVER_URL = "https://nexus.rapidminer.com";
    public static final String PARAMETER_ACCOUNT_SERVER_URL = "account_server_url";
    public static final String NEXUS_REST_URI = "/nexus/rest";
    public static final String REQUEST_KEY_CONTENT_TYPE = "Content-Type";
    public static final String REQUEST_VALUE_CONTENT_TYPE_JSON = "application/json";
    public static final String CONNECTION_HEADER_ACCEPT_CHARSET = "Accept-Charset";
    public static final int BUFFER_SIZE = 4096;
    public static final ThreadLocal<DateFormat> ISO_DATE_FORMATTER;
    private static final String GENERIC_ERROR;
    private static final String GENERATION_ERROR;
    private static final String AUTH_ERROR;
    private static final String UNVERIFIED_ERROR;
    private static final String ACCOUNT_ERROR;
    private static final String LIC01 = "LIC-01";
    private static final String LIC10 = "LIC-10";
    private static final String LIC11 = "LIC-11";
    private static final String LICXX = "LIC-XX";
    public static final String PRO10 = "PRO-10";
    public static final String AUTH11 = "AUTH-11";
    public static final String AUTH04 = "AUTH-04";
    public static final String NEX41 = "NEX-41";

    public NexusUtilities() {
    }

    public static String translateApplicationStatusCode(String applicationStatusCode) {
        if(applicationStatusCode == null) {
            return GENERIC_ERROR;
        } else {
            byte var2 = -1;
            switch(applicationStatusCode.hashCode()) {
                case -2049694008:
                    if(applicationStatusCode.equals("LIC-01")) {
                        var2 = 2;
                    }
                    break;
                case -2049693978:
                    if(applicationStatusCode.equals("LIC-10")) {
                        var2 = 3;
                    }
                    break;
                case -2049693977:
                    if(applicationStatusCode.equals("LIC-11")) {
                        var2 = 4;
                    }
                    break;
                case -2049692729:
                    if(applicationStatusCode.equals("LIC-XX")) {
                        var2 = 5;
                    }
                    break;
                case -1995504055:
                    if(applicationStatusCode.equals("NEX-41")) {
                        var2 = 6;
                    }
                    break;
                case 71440457:
                    if(applicationStatusCode.equals("AUTH-04")) {
                        var2 = 1;
                    }
                    break;
                case 71440485:
                    if(applicationStatusCode.equals("AUTH-11")) {
                        var2 = 0;
                    }
            }

            switch(var2) {
                case 0:
                    return AUTH_ERROR;
                case 1:
                    return UNVERIFIED_ERROR;
                case 2:
                case 3:
                case 4:
                case 5:
                    return GENERATION_ERROR;
                case 6:
                    return ACCOUNT_ERROR;
                default:
                    return GENERIC_ERROR;
            }
        }
    }

    public static String getNexusRESTUrl() {
        String property = ParameterService.getParameterValue("account_server_url");
        return property == null?"https://nexus.rapidminer.com":property;
    }

    public static final <T> T parseJacksonString(String jsonString, Class<T> entityClass) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonString, entityClass);
    }

    public static final String encodeString(String string) {
        try {
            return URLEncoder.encode(string, ENCODING.name());
        } catch (UnsupportedEncodingException var2) {
            throw new RuntimeException(ENCODING.name() + " encoding is not supported!");
        }
    }

    static {
        ENCODING = StandardCharsets.UTF_8;
        ISO_DATE_FORMATTER = new ThreadLocal() {
            protected DateFormat initialValue() {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mmZ", Locale.UK);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                return format;
            }
        };
        GENERIC_ERROR = I18N.getGUILabel("nexus.generic_error", new Object[0]);
        GENERATION_ERROR = I18N.getGUILabel("nexus.generation_error", new Object[0]);
        AUTH_ERROR = I18N.getGUILabel("nexus.auth_error", new Object[0]);
        UNVERIFIED_ERROR = I18N.getGUILabel("nexus.unverified_error", new Object[0]);
        ACCOUNT_ERROR = I18N.getGUILabel("nexus.account_error", new Object[0]);
    }
}
