package com.rapidminer.extension.jdbc.tools.jdbc;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.tools.I18N;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Tools;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;

public class JDBCProperties {
    private String name;
    private String defaultPort;
    private String urlPrefix;
    private String dbNameSeperator;
    private String[] drivers;
    private String driverJarFile;
    private boolean userDefined;

    private JDBCProperties() {
        this(false);
    }

    public JDBCProperties(boolean userDefined) {
        this.setName("unknown");
        this.setDefaultPort("port");
        this.setUrlPrefix("urlprefix://");
        this.dbNameSeperator = "/";
        this.userDefined = userDefined;
    }

    public JDBCProperties(Element driverElement, boolean userDefined) throws Exception {
        this.userDefined = userDefined;
        Attr nameAttr = driverElement.getAttributeNode("name");
        Attr driversAttr = driverElement.getAttributeNode("drivers");
        Attr portAttr = driverElement.getAttributeNode("defaultport");
        Attr urlAttr = driverElement.getAttributeNode("urlprefix");
        Attr dbNameAttr = driverElement.getAttributeNode("dbnameseparator");
        if(dbNameAttr == null) {
            dbNameAttr = driverElement.getAttributeNode("dbnameseperator");
        }

        Attr driverJarAttr = driverElement.getAttributeNode("driver_jar");
        if(nameAttr == null) {
            throw new Exception("Missing name for <driver> tag");
        } else {
            this.setName(nameAttr.getValue());
            if(portAttr == null) {
                throw new Exception("Missing defaultport for <driver> tag for driver \'" + this.getName() + "\'");
            } else if(urlAttr == null) {
                throw new Exception("Missing urlprefix for <driver> tag for driver \'" + this.getName() + "\'");
            } else {
                if(driversAttr == null) {
                    LogService.getRoot().log(Level.WARNING, "com.rapidminer.tools.jdbc.JDBCProperties.missing_database_driver_class", this.getName());
                }

                this.setDefaultPort(portAttr.getValue());
                this.setUrlPrefix(urlAttr.getValue());
                this.dbNameSeperator = dbNameAttr != null?dbNameAttr.getValue():"/";
                if(driversAttr != null) {
                    String value = driversAttr.getValue();
                    this.setDriverClasses(value);
                } else {
                    this.drivers = new String[0];
                }

                if(driverJarAttr != null) {
                    this.setDriverJarFile(driverJarAttr.getValue());
                } else {
                    this.setDriverJarFile((String)null);
                }

            }
        }
    }

    public void setDriverClasses(String value) {
        if(value == null) {
            this.drivers = new String[0];
        } else {
            this.drivers = value.split("\\s*,\\s*");
        }

    }

    public void merge(JDBCProperties other) {
        if(other.getDefaultPort() != null) {
            this.setDefaultPort(other.getDefaultPort());
        }

        if(other.getUrlPrefix() != null) {
            this.setUrlPrefix(other.getUrlPrefix());
        }

        if(other.dbNameSeperator != null) {
            this.dbNameSeperator = other.dbNameSeperator;
        }

        this.userDefined = this.userDefined || other.userDefined;
        if(other.getDriverJarFile() != null) {
            if(this.getDriverJarFile() == null) {
                this.setDriverJarFile(other.getDriverJarFile());
            } else {
                this.setDriverJarFile(other.getDriverJarFile() + "," + this.getDriverJarFile());
            }
        }

        if(other.drivers != null) {
            if(this.drivers == null) {
                this.drivers = other.drivers;
            } else {
                HashSet merged = new HashSet();
                merged.addAll(Arrays.asList(this.drivers));
                merged.addAll(Arrays.asList(other.drivers));
                this.drivers = (String[])merged.toArray(new String[merged.size()]);
            }
        }

    }

    public String getDbNameSeperator() {
        return this.dbNameSeperator;
    }

    public String getDefaultPort() {
        return this.defaultPort;
    }

    public String getName() {
        return this.name;
    }

    public String getUrlPrefix() {
        return this.urlPrefix;
    }

    public static JDBCProperties createDefaultJDBCProperties() {
        return new JDBCProperties();
    }

    public void registerDrivers() {
        String[] var1 = this.drivers;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String driverName = var1[var3];

            try {
                ClassLoader e;
                if(this.getDriverJarFile() == null) {
                    e = this.getClass().getClassLoader();
                } else {
                    String[] jarNames = this.getDriverJarFile().split(",");
                    final URL[] urls = new URL[jarNames.length];

                    for(int i = 0; i < jarNames.length; ++i) {
                        File jarFile = new File(jarNames[i]);
                        if(!jarFile.exists()) {
                            LogService.getRoot().log(Level.WARNING, "com.rapidminer.tools.jdbc.JDBCProperties.driver_jar_file_does_not_exist", new Object[]{jarFile.getAbsolutePath(), this.getName()});
                        }

                        urls[i] = jarFile.toURI().toURL();
                    }

                    e = (ClassLoader)AccessController.doPrivileged(new PrivilegedExceptionAction() {
                        public ClassLoader run() throws Exception {
                            return new URLClassLoader(urls);
                        }
                    });
                }

                if(this.getDriverJarFile() == null) {
                    Class.forName(driverName, true, e);
                } else {
                    DriverManager.registerDriver(new DriverAdapter((Driver)Class.forName(driverName, true, e).newInstance()));
                }

                if(this.getDriverJarFile() != null) {
                    LogService.getRoot().log(Level.CONFIG, "com.rapidminer.tools.jdbc.JDBCProperties.loaded_jdbc_driver_from_driverjarfile", new Object[]{driverName, this.getDriverJarFile()});
                } else {
                    LogService.getRoot().log(Level.CONFIG, "com.rapidminer.tools.jdbc.JDBCProperties.loaded_jdbc_driver", driverName);
                }
            } catch (ClassNotFoundException var10) {
                if(this.getDriverJarFile() != null) {
                    LogService.getRoot().log(Level.INFO, "com.rapidminer.tools.jdbc.JDBCProperties.jdbc_driver_not_found", new Object[]{driverName, this.getDriverJarFile()});
                } else {
                    LogService.getRoot().log(Level.INFO, "com.rapidminer.tools.jdbc.JDBCProperties.jdbc_driver_not_found_not_installed", driverName);
                }
            } catch (Exception var11) {
                LogService.getRoot().log(Level.WARNING, I18N.getMessage(LogService.getRoot().getResourceBundle(), "com.rapidminer.tools.jdbc.JDBCProperties.jdbc_driver_not_registered", new Object[]{driverName, var11}), var11);
            }
        }

    }

    public String[] getDriverClasses() {
        return this.drivers;
    }

    public String getDriverJarFile() {
        return this.driverJarFile;
    }

    public boolean isUserDefined() {
        return this.userDefined;
    }

    public String toString() {
        return this.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefaultPort(String defaultPort) {
        this.defaultPort = defaultPort;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public void setDriverJarFile(String driverJarFile) {
        this.driverJarFile = driverJarFile;
    }

    public void setDbNameSeperator(String dbNameSeperator) {
        this.dbNameSeperator = dbNameSeperator;
    }

    public Element getXML(Document doc) {
        Element element = doc.createElement("driver");
        element.setAttribute("name", this.getName());
        element.setAttribute("drivers", Tools.toString(this.drivers, ","));
        element.setAttribute("driver_jar", this.getDriverJarFile());
        element.setAttribute("defaultport", this.getDefaultPort());
        element.setAttribute("urlprefix", this.getUrlPrefix());
        element.setAttribute("dbnameseparator", this.getDbNameSeperator());
        return element;
    }
}
