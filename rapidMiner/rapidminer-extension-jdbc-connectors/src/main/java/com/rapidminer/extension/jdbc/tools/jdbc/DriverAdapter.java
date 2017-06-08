package com.rapidminer.extension.jdbc.tools.jdbc;

/**
 * Created by mk on 3/15/16.
 */
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverAdapter implements Driver {
    private final Driver driver;

    public DriverAdapter(Driver d) {
        this.driver = d;
    }

    public boolean acceptsURL(String u) throws SQLException {
        return this.driver.acceptsURL(u);
    }

    public Connection connect(String u, Properties p) throws SQLException {
        return this.driver.connect(u, p);
    }

    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
        return this.driver.getPropertyInfo(u, p);
    }

    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    public String toString() {
        String result = this.driver.getClass().getSimpleName();
        int index = result.toLowerCase().indexOf("driver");
        if(index >= 0) {
            String newResult = "";
            newResult = newResult + result.substring(0, index);
            newResult = newResult + result.substring(index + "driver".length());
            result = newResult.trim();
        }

        if(result.trim().length() == 0) {
            result = "Unknown Driver";
        }

        return result;
    }

    public String toLongString() {
        return this.driver.getClass().getName();
    }

    public boolean equals(Object o) {
        if(!(o instanceof DriverAdapter)) {
            return false;
        } else {
            DriverAdapter a = (DriverAdapter)o;
            return this.driver.equals(a.driver);
        }
    }

    public int hashCode() {
        return this.driver.hashCode();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.driver.getParentLogger();
    }
}