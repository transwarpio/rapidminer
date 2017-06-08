package com.rapidminer.extension.jdbc.tools.jdbc;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.RapidMiner;
import com.rapidminer.core.license.ProductConstraintManager;
import com.rapidminer.io.process.XMLTools;
import com.rapidminer.license.LicenseConstants;
import com.rapidminer.license.LicenseEvent;
import com.rapidminer.license.LicenseEvent.LicenseEventType;
import com.rapidminer.license.LicenseManagerListener;
import com.rapidminer.license.LicenseManagerRegistry;
import com.rapidminer.license.violation.LicenseConstraintViolation;
import com.rapidminer.tools.*;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class DatabaseService {
    private static final String[] DB_URL_PREFIX_WHITELIST = new String[]{"jdbc:sqlite:", "jdbc:mysql:", "jdbc:mariadb:", "jdbc:postgresql:", "jdbc:ingres:", "jdbc:hsqldb:", "jdbc:ucanaccess://", "jdbc:odbc:", "jdbc:jtds:sybase://"};
    private static final String LOCALHOST_TEST_SUFFIX = "localhost/test";
    private static List<JDBCProperties> jdbcProperties = new ArrayList();

    public DatabaseService() {
    }

    public static void init() {
        registerCommercialDatabaseDrivers();
        LicenseManagerRegistry.INSTANCE.get().registerLicenseManagerListener(new LicenseManagerListener() {
            public <S, C> void handleLicenseEvent(LicenseEvent<S, C> event) {
                if(event.getType() == LicenseEventType.ACTIVE_LICENSE_CHANGED) {
                    DatabaseService.registerCommercialDatabaseDrivers();
                }

            }
        });
        if(RapidMiner.getExecutionMode().canAccessFilesystem()) {
            File globalJDBCFile1 = ParameterService.getGlobalConfigFile("jdbc_properties.xml");
            if(globalJDBCFile1 != null) {
                loadJDBCProperties(globalJDBCFile1, false);
            }

            File userProperties1 = getUserJDBCPropertiesFile();
            if(userProperties1 != null && !userProperties1.exists()) {
                writeDefaultJDBCProperties(userProperties1);
            }
            loadJDBCProperties(userProperties1, true);
        } else {
            LogService.getRoot().log(Level.CONFIG, "com.rapidminer.tools.jdbc.DatabaseService.ignoring_jdbc_properties_xml_file", RapidMiner.getExecutionMode());
        }

    }

    private static void registerCommercialDatabaseDrivers() {
        if(isCommercialDatabaseAllowed()) {
            try {
                InputStream e = DatabaseService.class.getResourceAsStream("/com/rapidminer/extension/resources/jdbc_commercial_properties.xml");
                Throwable var1 = null;

                try {
                    addOrMergeJDBCProperties(e, "resource jdbc_commercial_properties.xml", false);
                } catch (Throwable var11) {
                    var1 = var11;
                    throw var11;
                } finally {
                    if(e != null) {
                        if(var1 != null) {
                            try {
                                e.close();
                            } catch (Throwable var10) {
                                var1.addSuppressed(var10);
                            }
                        } else {
                            e.close();
                        }
                    }

                }
            } catch (IOException var13) {
                LogService.getRoot().log(Level.WARNING, "com.rapidminer.tools.jdbc.DatabaseService.error_loading_commercial_jdbc_properties", var13);
            }
        }

    }

    public static final boolean isCommercialDatabaseAllowed() {
        return LicenseManagerRegistry.INSTANCE.get().isAllowed(ProductConstraintManager.INSTANCE.getProduct(), LicenseConstants.CONNECTORS_CONSTRAINT, "COMMMERCIAL_DATABASES");
    }

    public static final LicenseConstraintViolation<List<String>, String> checkCommercialDatabaseConstraint(String i18nKey) {
        return LicenseManagerRegistry.INSTANCE.get().checkConstraintViolation(ProductConstraintManager.INSTANCE.getProduct(), LicenseConstants.CONNECTORS_CONSTRAINT, "COMMMERCIAL_DATABASES", i18nKey, true);
    }

    public static boolean isDatabaseURLOpenSource(String databaseURL) throws IllegalArgumentException {
        if(databaseURL == null) {
            throw new IllegalArgumentException("databaseURL must not be null!");
        } else {
            String[] var1 = DB_URL_PREFIX_WHITELIST;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                String whitelistedUrlPrefix = var1[var3];
                if(databaseURL.startsWith(whitelistedUrlPrefix)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static void writeDefaultJDBCProperties(File jdbcProperties) {
        InputStream defaultFile = DatabaseService.class.getResourceAsStream("/com/rapidminer/extension/resources/jdbc_properties.xml");
        File midasDriverPath = FileSystemService.getDriverDir();
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(defaultFile);

            if(document != null) {
                NodeList driverTags = document.getDocumentElement().getElementsByTagName("driver");

                for(int i = 0; i < driverTags.getLength(); ++i) {
                    Element currentElement = (Element)driverTags.item(i);
                    String jar = currentElement.getAttribute("driver_jar");
                    File path = new File(midasDriverPath, jar);
                    currentElement.setAttribute("driver_jar", path.getAbsolutePath());
                }
            }
            XMLTools.stream(document, getUserJDBCPropertiesFile(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (defaultFile != null) {
                try {
                    defaultFile.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void loadJDBCProperties(File jdbcProperties, boolean userDefined) {
        addOrMergeJDBCProperties(jdbcProperties, userDefined);
    }

    public static List<JDBCProperties> addOrMergeJDBCProperties(File jdbcProperties, boolean userDefined) {
        try {
            FileInputStream e = new FileInputStream(jdbcProperties);
            Throwable var3 = null;

            List var4;
            try {
                var4 = addOrMergeJDBCProperties(e, jdbcProperties.getAbsolutePath(), userDefined);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if(e != null) {
                    if(var3 != null) {
                        try {
                            e.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var4;
        } catch (IOException var16) {
            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.tools.jdbc.DatabaseService.loading_jdbc_properties_error_from_directory", new Object[0]), var16);
            return Collections.emptyList();
        }
    }

    public static void loadJDBCProperties(InputStream in, String name, boolean userDefined) {
        addOrMergeJDBCProperties(in, name, userDefined);
    }

    public static List<JDBCProperties> addOrMergeJDBCProperties(InputStream in, String name, boolean userDefined) {
        LogService.getRoot().log(Level.CONFIG, "com.rapidminer.tools.jdbc.DatabaseService.loading_jdbc_driver_information", name);
        Document document = null;
        LinkedList propsList = new LinkedList();

        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
        } catch (Exception var10) {
            LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.tools.jdbc.DatabaseService.reading_jdbc_driver_description_file_error", new Object[]{name, var10.getMessage()}), var10);
        }

        if(document != null) {
            if(!document.getDocumentElement().getTagName().toLowerCase().equals("drivers")) {
                LogService.getRoot().log(Level.WARNING, "com.rapidminer.tools.jdbc.DatabaseService.reading_jdbc_driver_description_file_outermost_tag_error", name);
                return Collections.emptyList();
            }

            NodeList driverTags = document.getDocumentElement().getElementsByTagName("driver");

            for(int i = 0; i < driverTags.getLength(); ++i) {
                Element currentElement = (Element)driverTags.item(i);

                try {
                    propsList.add(addDriverInformation(currentElement, userDefined));
                } catch (Exception var11) {
                    Attr currentNameAttr = currentElement.getAttributeNode("name");
                    if(currentNameAttr != null) {
                        LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.tools.jdbc.DatabaseService.registering_jdbc_driver_description_error", new Object[]{currentNameAttr.getValue(), var11}), var11);
                    } else {
                        LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.tools.jdbc.DatabaseService.registering_jdbc_driver_description_error", new Object[]{currentElement, var11}), var11);
                    }
                }
            }
        }

        return propsList;
    }

    public static DriverInfo[] getAllDriverInfos() {
        LinkedList predefinedDriverList = new LinkedList();
        Iterator driverList = getJDBCProperties().iterator();

        boolean accepted;
        DriverInfo predefinedInfo;
        while(driverList.hasNext()) {
            JDBCProperties drivers = (JDBCProperties)driverList.next();
            Enumeration driverArray = getAllDrivers();
            accepted = false;

            while(driverArray.hasMoreElements()) {
                Driver driver = (Driver)driverArray.nextElement();

                try {
                    if(driver.acceptsURL(drivers.getUrlPrefix() + "localhost/test")) {
                        predefinedInfo = new DriverInfo(driver, drivers);
                        predefinedDriverList.add(predefinedInfo);
                        accepted = true;
                        break;
                    }
                } catch (SQLException var7) {
                    ;
                }
            }

            if(!accepted) {
                predefinedDriverList.add(new DriverInfo((Driver)null, drivers));
            }
        }

        LinkedList driverList1 = new LinkedList();
        Enumeration drivers1 = getAllDrivers();

        while(drivers1.hasMoreElements()) {
            Driver driverArray1 = (Driver)drivers1.nextElement();
            accepted = true;
            Iterator driver1 = predefinedDriverList.iterator();

            while(driver1.hasNext()) {
                predefinedInfo = (DriverInfo)driver1.next();
                if(predefinedInfo.getDriver() != null && predefinedInfo.getDriver().equals(driverArray1)) {
                    accepted = false;
                    break;
                }
            }

            if(accepted) {
                driverList1.add(new DriverInfo(driverArray1, (JDBCProperties)null));
            }
        }

        driverList1.addAll(predefinedDriverList);
        Collections.sort(driverList1);
        DriverInfo[] driverArray2 = new DriverInfo[driverList1.size()];
        driverList1.toArray(driverArray2);
        return driverArray2;
    }

    public static JDBCProperties getJDBCProperties(String name) {
        Iterator var1 = jdbcProperties.iterator();

        JDBCProperties properties;
        do {
            if(!var1.hasNext()) {
                return null;
            }

            properties = (JDBCProperties)var1.next();
        } while(!properties.getName().equals(name));

        return properties;
    }

    public static List<JDBCProperties> getJDBCProperties() {
        LinkedList jdbcProps = new LinkedList(jdbcProperties);
        Iterator iterator = jdbcProps.iterator();

        while(iterator.hasNext()) {
            JDBCProperties props = (JDBCProperties)iterator.next();
            if(!isDatabaseURLOpenSource(props.getUrlPrefix()) && !isCommercialDatabaseAllowed()) {
                iterator.remove();
            }
        }

        return jdbcProps;
    }

    public static void addJDBCProperties(JDBCProperties newProps) {
        jdbcProperties.add(newProps);
    }

    public static void removeJDBCProperties(JDBCProperties newProps) {
        jdbcProperties.remove(newProps);
    }

    public static String[] getDBSystemNames() {
        String[] names = new String[jdbcProperties.size()];
        int counter = 0;

        for(Iterator i = jdbcProperties.iterator(); i.hasNext(); names[counter++] = ((JDBCProperties)i.next()).getName()) {
            ;
        }

        return names;
    }

    public static void saveUserDefinedProperties() throws XMLException {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException var4) {
            throw new XMLException("Failed to create document: " + var4, var4);
        }

        Element root = doc.createElement("drivers");
        doc.appendChild(root);
        Iterator var2 = getJDBCProperties().iterator();

        while(var2.hasNext()) {
            JDBCProperties props = (JDBCProperties)var2.next();
            if(props.isUserDefined()) {
                root.appendChild(props.getXML(doc));
            }
        }

        XMLTools.stream(doc, getUserJDBCPropertiesFile(), StandardCharsets.UTF_8);
    }

    private static JDBCProperties addDriverInformation(Element driverElement, boolean userDefined) throws Exception {
        JDBCProperties properties = new JDBCProperties(driverElement, userDefined);
        if(isDatabaseURLOpenSource(properties.getUrlPrefix())) {
            properties.registerDrivers();
        } else if(isCommercialDatabaseAllowed()) {
            properties.registerDrivers();
        } else {
            LogService.getRoot().log(Level.CONFIG, "com.rapidminer.tools.jdbc.DatabaseService.skipping_commercial_db_drivers", properties.getUrlPrefix());
        }

        Iterator var3 = jdbcProperties.iterator();

        JDBCProperties other;
        do {
            if(!var3.hasNext()) {
                jdbcProperties.add(properties);
                return properties;
            }

            other = (JDBCProperties)var3.next();
        } while(!other.getName().equals(properties.getName()));

        LogService.getRoot().log(Level.CONFIG, "com.rapidminer.tools.jdbc.DatabaseService.merging_jdbc_driver_information", other.getName());
        other.merge(properties);
        return other;
    }

    private static Enumeration<Driver> getAllDrivers() {
        return DriverManager.getDrivers();
    }

    private static File getUserJDBCPropertiesFile() {
        return FileSystemService.getUserConfigFile("jdbc_properties.xml");
    }
}
