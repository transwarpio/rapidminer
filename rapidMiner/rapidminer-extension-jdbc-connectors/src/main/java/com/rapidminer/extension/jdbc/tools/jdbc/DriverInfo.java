package com.rapidminer.extension.jdbc.tools.jdbc;

/**
 * Created by mk on 3/15/16.
 */
import java.sql.Driver;

public class DriverInfo implements Comparable<DriverInfo> {
    private final Driver driver;
    private JDBCProperties properties;

    public DriverInfo(Driver driver, JDBCProperties properties) {
        this.driver = driver;
        this.properties = properties;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public String getShortName() {
        return this.properties != null?this.properties.getName():"Unknown";
    }

    public String getClassName() {
        return this.driver != null?(this.driver instanceof DriverAdapter?((DriverAdapter)this.driver).toLongString():this.driver.getClass().getName()):null;
    }

    public String toString() {
        return this.getShortName() + " (" + this.getClassName() + ")";
    }

    public int compareTo(DriverInfo o) {
        int c = this.getShortName().compareTo(o.getShortName());
        if(c != 0) {
            return c;
        } else {
            String cn1 = this.getClassName();
            String cn2 = o.getClassName();
            return cn1 != null && cn2 != null?cn1.compareTo(cn2):(cn1 == null?1:-1);
        }
    }

    public boolean equals(Object o) {
        if(!(o instanceof DriverInfo)) {
            return false;
        } else {
            DriverInfo a = (DriverInfo)o;
            return !this.getShortName().equals(a.getShortName())?false:a.getDriver() == this.getDriver();
        }
    }

    public int hashCode() {
        return this.getShortName().hashCode();
    }

    public JDBCProperties getProperties() {
        return this.properties;
    }
}
