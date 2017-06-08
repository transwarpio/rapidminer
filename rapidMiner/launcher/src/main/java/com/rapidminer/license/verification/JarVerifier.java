package com.rapidminer.license.verification;

/**
 * Created by mk on 3/9/16.
 */

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JarVerifier {
    private static final Path LICENSE_LOCATION = System.getProperty("rapidminer.development.license") != null?Paths.get(System.getProperty("rapidminer.development.license"), new String[0]):Paths.get(System.getProperty("user.home"), new String[]{".RapidMiner", "licenses", "development.lic"});
    private static final String LICENSE_PROPERTIES_LOCATION = "com/rapidminer/license/license.properties";
    private static final String LICENSE_HOLDER_NAME = "license_holder_name";
    private static final String LICENSE_HOLDER_EMAIL = "license_holder_email";
    private static final String LICENSE_TYPE = "license_type";
    private static final String LICENSE_EXPIRATION_DATE = "license_expiration_date";
    private static final Logger LOGGER = Logger.getLogger(JarVerifier.class.getCanonicalName());
    private static boolean loggedDevelopmentLicense = false;
    private static final ThreadLocal<DateFormat> ISO_DATE_FORMATTER = new ThreadLocal() {
        protected DateFormat initialValue() {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mmXXX", Locale.UK);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format;
        }
    };
    private static final String KEY_ALGORITHM = "RSA";
    private static final String KEY_B64_ENCODED = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvyoyYgZ0jYHlPOh2mGvvvXl6FS4Xt3FaCsnn1IglbbDYM9eXcWgeD6I/4mM3t6XsAsyzSDLRxagCM869lYknxjff0xMdA5aekqPe0vx4yqR9QK369u3lbGMaNvylwhg5vCTWn2vZanxWScOfVW6yDxEjgEHJvMiMzZkGNklYC3ULBCkHfIrih5hO83k5FileuUWDNO4BrLrawmjo9AmYksPVOMmd4/DtDpnehpLy0hQtjBJsz61hAGVDnPGpvbsW0rjFAjE4fR5+4RwUNo+SsD/44Jc8bui5seVH5vZuTj02XokybGR4BikrqvJZ4rHe4OGowl8uIr9sEN/+0eIJXQIDAQAB";
    private static final String VFS_SCHEME = "vfs";
    private static final String RAPIDMINER_SERVER = "rapidminer-server";

    private JarVerifier() {
        throw new AssertionError("Utility class must not be instantiated.");
    }

    public static void verify(Class... classes) throws GeneralSecurityException {
        if(classes != null && classes.length != 0) {
            Class[] factory = classes;
            int spec = classes.length;

            for(int key = 0; key < spec; ++key) {
                Class archives = factory[key];
                if(archives == null) {
                    throw new IllegalArgumentException("Classes must not contain null values.");
                }
            }

            KeyFactory var114 = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec var115 = new X509EncodedKeySpec(DatatypeConverter.parseBase64Binary("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvyoyYgZ0jYHlPOh2mGvvvXl6FS4Xt3FaCsnn1IglbbDYM9eXcWgeD6I/4mM3t6XsAsyzSDLRxagCM869lYknxjff0xMdA5aekqPe0vx4yqR9QK369u3lbGMaNvylwhg5vCTWn2vZanxWScOfVW6yDxEjgEHJvMiMzZkGNklYC3ULBCkHfIrih5hO83k5FileuUWDNO4BrLrawmjo9AmYksPVOMmd4/DtDpnehpLy0hQtjBJsz61hAGVDnPGpvbsW0rjFAjE4fR5+4RwUNo+SsD/44Jc8bui5seVH5vZuTj02XokybGR4BikrqvJZ4rHe4OGowl8uIr9sEN/+0eIJXQIDAQAB"));
            PublicKey var116 = var114.generatePublic(var115);
            Throwable var125;
            if(Files.exists(LICENSE_LOCATION, new LinkOption[0])) {
                if(!loggedDevelopmentLicense) {
                    LOGGER.log(Level.INFO, "Found development license.");
                }

                try {
                    JarFile var118 = new JarFile(LICENSE_LOCATION.toFile());
                    Throwable var120 = null;

                    try {
                        if(getAndVerifyEntry(var118, "META-INF/MANIFEST.MF", var116) == null) {
                            throw new GeneralSecurityException("License is unsigned (missing manifest)!");
                        }

                        JarEntry var122 = getAndVerifyEntry(var118, "com/rapidminer/license/license.properties", var116);
                        InputStream var124 = var118.getInputStream(var122);
                        var125 = null;

                        try {
                            InputStreamReader var126 = new InputStreamReader(var124, "UTF-8");
                            Throwable var127 = null;

                            try {
                                Properties e1 = new Properties();
                                e1.load(var126);
                                String[] holder = new String[]{"license_holder_name", "license_holder_email", "license_type", "license_expiration_date"};
                                int type = holder.length;

                                for(int expires = 0; expires < type; ++expires) {
                                    String e2 = holder[expires];
                                    if(!e1.containsKey(e2)) {
                                        throw new GeneralSecurityException(String.format("License is missing property \'%s\'!", new Object[]{e2}));
                                    }
                                }

                                String var128 = e1.getProperty("license_holder_name");
                                String var129 = e1.getProperty("license_type");
                                String var130 = e1.getProperty("license_expiration_date");
                                if(!var129.equalsIgnoreCase("Development")) {
                                    throw new GeneralSecurityException(String.format("Invalid license type \'%s\'.", new Object[]{var129}));
                                }

                                try {
                                    Date var131 = ((DateFormat)ISO_DATE_FORMATTER.get()).parse(var130);
                                    if((new Date()).after(var131)) {
                                        throw new GeneralSecurityException("Expired license.");
                                    }
                                } catch (ParseException var102) {
                                    throw new GeneralSecurityException("Failed to parse license expiration date..", var102);
                                }

                                if(!loggedDevelopmentLicense) {
                                    LOGGER.log(Level.INFO, String.format("License of type \'%s\' for \'%s\' is valid.", new Object[]{var129, var128}));
                                    loggedDevelopmentLicense = true;
                                }
                            } catch (Throwable var107) {
                                var127 = var107;
                                throw var107;
                            } finally {
                                if(var126 != null) {
                                    if(var127 != null) {
                                        try {
                                            var126.close();
                                        } catch (Throwable var100) {
                                            var127.addSuppressed(var100);
                                        }
                                    } else {
                                        var126.close();
                                    }
                                }

                            }
                        } catch (Throwable var109) {
                            var125 = var109;
                            throw var109;
                        } finally {
                            if(var124 != null) {
                                if(var125 != null) {
                                    try {
                                        var124.close();
                                    } catch (Throwable var99) {
                                        var125.addSuppressed(var99);
                                    }
                                } else {
                                    var124.close();
                                }
                            }

                        }
                    } catch (Throwable var111) {
                        var120 = var111;
                        throw var111;
                    } finally {
                        if(var118 != null) {
                            if(var120 != null) {
                                try {
                                    var118.close();
                                } catch (Throwable var98) {
                                    var120.addSuppressed(var98);
                                }
                            } else {
                                var118.close();
                            }
                        }

                    }

                } catch (IOException var113) {
                    throw new GeneralSecurityException(var113);
                }
            } else {
                HashSet var117 = new HashSet(classes.length);
                Class[] var5 = classes;
                int location = classes.length;

                for(int e = 0; e < location; ++e) {
                    Class target = var5[e];
                    CodeSource source = target.getProtectionDomain().getCodeSource();
                    URL location1 = source.getLocation();

                    try {
                        var117.add(location1.toURI());
                    } catch (URISyntaxException var104) {
                        throw new GeneralSecurityException("Failed to identify JAR file.", var104);
                    }
                }

                Iterator var119 = var117.iterator();

                while(true) {
                    while(var119.hasNext()) {
                        URI var121 = (URI)var119.next();
                        if("vfs".equals(var121.getScheme()) && var121.getPath().contains("rapidminer-server")) {
                            LOGGER.log(Level.FINE, String.format("RapidMiner Server environment detected. Skipping Jar verification for location \'%s\'.", new Object[]{var121.getPath()}));
                        } else {
                            try {
                                JarFile var123 = new JarFile(new File(var121), true);
                                var125 = null;

                                try {
                                    if(getAndVerifyEntry(var123, "META-INF/MANIFEST.MF", var116) == null) {
                                        throw new GeneralSecurityException(String.format("JAR \'%s\' is unsigned!", new Object[]{var123.getName()}));
                                    }
                                } catch (Throwable var103) {
                                    var125 = var103;
                                    throw var103;
                                } finally {
                                    if(var123 != null) {
                                        if(var125 != null) {
                                            try {
                                                var123.close();
                                            } catch (Throwable var101) {
                                                var125.addSuppressed(var101);
                                            }
                                        } else {
                                            var123.close();
                                        }
                                    }

                                }
                            } catch (IllegalArgumentException | IOException var106) {
                                throw new GeneralSecurityException(String.format("Failed to open JAR file at %s.", new Object[]{var121}), var106);
                            }
                        }
                    }

                    return;
                }
            }
        } else {
            throw new IllegalArgumentException("Classes must not be null or empty.");
        }
    }

    private static JarEntry getAndVerifyEntry(JarFile jar, String name, PublicKey key) throws IOException, GeneralSecurityException {
        JarEntry entry = jar.getJarEntry(name);
        if(entry == null) {
            return null;
        } else {
            byte[] buffer = new byte[1024];
            InputStream certificates = jar.getInputStream(entry);
            Throwable lastException = null;

            try {
                while(true) {
                    if(certificates.read(buffer) > 0) {
                        break;
                    }
                }
            } catch (Throwable var20) {
                lastException = var20;
                throw var20;
            } finally {
                if(certificates != null) {
                    if(lastException != null) {
                        try {
                            certificates.close();
                        } catch (Throwable var18) {
                            lastException.addSuppressed(var18);
                        }
                    } else {
                        certificates.close();
                    }
                }

            }

            Certificate[] var22 = entry.getCertificates();
            if(var22 != null && var22.length != 0) {
                GeneralSecurityException var23 = null;
                boolean verified = false;
                Certificate[] var8 = var22;
                int var9 = var22.length;
                int var10 = 0;

                while(var10 < var9) {
                    Certificate certificate = var8[var10];

                    try {
                        certificate.verify(key);
                        verified = true;
                        break;
                    } catch (GeneralSecurityException var19) {
                        var23 = var19;
                        ++var10;
                    }
                }

                if(!verified) {
                    throw var23;
                } else {
                    return entry;
                }
            } else {
                throw new GeneralSecurityException(String.format("JAR \'%s\' is unsigned!", new Object[]{jar.getName()}));
            }
        }
    }
}
