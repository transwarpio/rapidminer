package com.rapidminer.extension.jdbc.tools.jdbc.connection;

/**
 * Created by mk on 3/15/16.
 */
import com.rapidminer.extension.jdbc.tools.jdbc.JDBCProperties;
import java.util.Comparator;

public abstract class ConnectionEntry implements Comparable<ConnectionEntry> {
    public static Comparator<ConnectionEntry> COMPARATOR = new Comparator<ConnectionEntry>() {
        public int compare(ConnectionEntry o1, ConnectionEntry o2) {
            return o1.toString().compareTo(o2.toString());
        }
    };
    protected String name;
    protected JDBCProperties properties;
    protected String user;
    protected char[] password;

    public ConnectionEntry() {
        this("", JDBCProperties.createDefaultJDBCProperties());
    }

    public ConnectionEntry(String name, JDBCProperties properties) {
        this.user = null;
        this.password = null;
        this.name = name;
        this.properties = properties;
    }

    public int compareTo(ConnectionEntry o) {
        return this.name.compareTo(o.name);
    }

    public String getName() {
        return this.name;
    }

    public JDBCProperties getProperties() {
        return this.properties;
    }

    public String getUser() {
        return this.user;
    }

    public char[] getPassword() {
        return this.password;
    }

    public abstract String getURL();

    public String toString() {
        return this.name;
    }

    public boolean isReadOnly() {
        return false;
    }

    public String getRepository() {
        return null;
    }
}
