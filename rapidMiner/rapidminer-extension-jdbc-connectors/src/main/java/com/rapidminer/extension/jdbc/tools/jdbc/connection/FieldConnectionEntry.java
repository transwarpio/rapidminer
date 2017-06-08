package com.rapidminer.extension.jdbc.tools.jdbc.connection;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.DatabaseService;
import com.rapidminer.extension.jdbc.tools.jdbc.JDBCProperties;
import com.rapidminer.io.process.XMLTools;
import com.rapidminer.tools.XMLException;
import com.rapidminer.tools.cipher.CipherException;
import com.rapidminer.tools.cipher.CipherTools;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.security.Key;
import java.util.Iterator;
import java.util.Properties;

public class FieldConnectionEntry extends ConnectionEntry {
    static final String XML_TAG_NAME = "field-entry";
    private String host;
    private String port;
    private String database;
    private String property;
    private Properties connectionProperties;
    private String repository = null;

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setConnectionProperties(Properties connectionProperties) {
        Properties newProps = new Properties();
        Iterator var3 = connectionProperties.keySet().iterator();

        while(var3.hasNext()) {
            Object key = var3.next();
            newProps.put(key, connectionProperties.get(key));
        }

        this.connectionProperties = newProps;
    }

    public Properties getConnectionProperties() {
        Properties newProps = new Properties();
        Iterator var2 = this.connectionProperties.keySet().iterator();

        while(var2.hasNext()) {
            Object key = var2.next();
            newProps.put(key, this.connectionProperties.get(key));
        }

        return newProps;
    }

    public char[] getPassword() {
        return this.password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public FieldConnectionEntry() {
    }

    public FieldConnectionEntry(String name, JDBCProperties properties, String host, String port, String database, String property, String user, char[] password) {
        super(name, properties);
        this.host = host;
        this.port = port;
        this.database = database;
        this.property = property;
        this.user = user;
        this.password = password;
        this.connectionProperties = new Properties();
    }

    public String getURL() {
        return createURL(this.properties, this.host, this.port, this.database, this.property);
    }

    public static String createURL(JDBCProperties properties, String host, String port, String database, String property) {
        StringBuffer urlBuffer = new StringBuffer();
        if(properties != null) {
            urlBuffer.append(properties.getUrlPrefix());
        } else {
            urlBuffer.append("unknown:prefix://");
        }

        if(host != null && !"".equals(host)) {
            urlBuffer.append(host);
            if(port != null && !"".equals(port)) {
                urlBuffer.append(":" + port);
            }

            if(database != null && !"".equals(database)) {
                if(properties != null) {
                    urlBuffer.append(properties.getDbNameSeperator());
                } else {
                    urlBuffer.append("/");
                }

                urlBuffer.append(database);
            }

            if (property != null && !"".equals(property)) {
                urlBuffer.append(property);
            }

        }

        return urlBuffer.toString();
    }

    public String getHost() {
        return this.host;
    }

    public boolean equals(Object object) {
        if(object instanceof FieldConnectionEntry) {
            FieldConnectionEntry entry = (FieldConnectionEntry)object;
            boolean equals = true;
            equals &= this.name.equals(entry.name);
            equals &= this.host.equals(entry.host);
            equals &= this.port.equals(entry.port);
            equals &= this.database.equals(entry.database);
            equals &= (this.property != null && this.property.equals(entry.property));
            equals &= this.user.equals(entry.user);
            equals &= this.password.length == entry.password.length;
            if(equals) {
                for(int i = 0; i < this.password.length; ++i) {
                    equals &= this.password[i] == entry.password[i];
                }
            }

            return equals;
        } else {
            return false;
        }
    }

    public Element toXML(Document doc, Key key, String replacementForLocalhost) throws CipherException {
        Element element = doc.createElement("field-entry");
        XMLTools.setTagContents(element, "name", this.name);
        if(this.properties != null) {
            XMLTools.setTagContents(element, "system", this.properties.getName());
        }

        String host = this.host;
        if(replacementForLocalhost != null) {
            host = host.replace("localhost", replacementForLocalhost);
        }

        XMLTools.setTagContents(element, "host", host);
        XMLTools.setTagContents(element, "port", this.port);
        XMLTools.setTagContents(element, "database", this.database);
        XMLTools.setTagContents(element, "property", this.property);
        XMLTools.setTagContents(element, "user", this.user);
        XMLTools.setTagContents(element, "password", CipherTools.encrypt(new String(this.password), key));
        Element propertiesElement = doc.createElement("properties");
        element.appendChild(propertiesElement);
        Iterator var7 = this.connectionProperties.keySet().iterator();

        while(var7.hasNext()) {
            Object propKey = var7.next();
            Element singlePropElement = doc.createElement(String.valueOf(propKey));
            singlePropElement.setTextContent(String.valueOf(this.connectionProperties.get(propKey)));
            propertiesElement.appendChild(singlePropElement);
        }

        return element;
    }

    public FieldConnectionEntry(Element element, Key key) throws CipherException {
        this.name = XMLTools.getTagContents(element, "name");
        this.host = XMLTools.getTagContents(element, "host");
        this.port = XMLTools.getTagContents(element, "port");
        this.database = XMLTools.getTagContents(element, "database");
        this.property = XMLTools.getTagContents(element, "property");
        this.user = XMLTools.getTagContents(element, "user");
        this.password = CipherTools.decrypt(XMLTools.getTagContents(element, "password"), key).toCharArray();
        String system = XMLTools.getTagContents(element, "system");
        if(system != null) {
            this.properties = DatabaseService.getJDBCProperties(system);
        }

        try {
            Element e = XMLTools.getChildElement(element, "properties", true);
            Properties props = new Properties();
            Iterator var6 = XMLTools.getChildElements(e).iterator();

            while(var6.hasNext()) {
                Element singlePropElement = (Element)var6.next();
                props.put(singlePropElement.getTagName(), singlePropElement.getTextContent());
            }

            this.connectionProperties = props;
        } catch (XMLException var8) {
            this.connectionProperties = new Properties();
        }

    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getRepository() {
        return this.repository;
    }

    public boolean isReadOnly() {
        return this.repository != null;
    }
}
