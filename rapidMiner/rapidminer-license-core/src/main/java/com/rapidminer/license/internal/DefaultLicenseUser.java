package com.rapidminer.license.internal;

/**
 * Created by mk on 3/10/16.
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapidminer.license.LicenseUser;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultLicenseUser implements LicenseUser {
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String email;
    @JsonProperty
    private Map<String, String> properties;

    protected DefaultLicenseUser() {
        this.name = null;
        this.email = null;
        this.properties = new HashMap();
    }

    public DefaultLicenseUser(String name, String email) {
        this.name = name;
        this.email = email;
        this.properties = new HashMap();
    }

    public DefaultLicenseUser(String name, String email, Map<String, String> properties) {
        this.name = name;
        this.email = email;
        this.properties = new HashMap(properties);
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public DefaultLicenseUser putProperty(String key, String value) {
        this.getNotNullProperties().put(key, value);
        return this;
    }

    public String getProperty(String key) {
        return (String)this.getNotNullProperties().get(key);
    }

    public DefaultLicenseUser copy() {
        return new DefaultLicenseUser(this.name, this.email, new HashMap(this.getNotNullProperties()));
    }

    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (this.email == null?0:this.email.hashCode());
        result1 = 31 * result1 + (this.name == null?0:this.name.hashCode());
        result1 = 31 * result1 + (this.properties == null?0:this.properties.hashCode());
        return result1;
    }

    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        } else if(obj == null) {
            return false;
        } else if(this.getClass() != obj.getClass()) {
            return false;
        } else {
            DefaultLicenseUser other = (DefaultLicenseUser)obj;
            if(this.email == null) {
                if(other.email != null) {
                    return false;
                }
            } else if(!this.email.equals(other.email)) {
                return false;
            }

            if(this.name == null) {
                if(other.name != null) {
                    return false;
                }
            } else if(!this.name.equals(other.name)) {
                return false;
            }

            if(this.properties == null) {
                if(other.properties != null) {
                    return false;
                }
            } else if(!this.properties.equals(other.properties)) {
                return false;
            }

            return true;
        }
    }

    private Map<String, String> getNotNullProperties() {
        if(this.properties == null) {
            this.properties = new HashMap();
        }

        return this.properties;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.getNotNullProperties());
    }
}
