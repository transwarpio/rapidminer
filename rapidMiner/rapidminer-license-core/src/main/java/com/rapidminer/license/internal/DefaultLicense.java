package com.rapidminer.license.internal;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.license.Constraints;
import com.rapidminer.license.License;
import com.rapidminer.license.LicenseStatus;

import java.util.*;

public final class DefaultLicense implements License {
    private static final DefaultLicenseUser EMPTY_LICENSE_USER = new DefaultLicenseUser((String)null, (String)null);
    private LicenseStatus status;
    private final LicenseContent content;

    public DefaultLicense(LicenseStatus status, LicenseContent license) {
        this.status = status;
        this.content = license;
    }

    public String getAnnotations() {
        return this.content.getAnnotations();
    }

    public LicenseStatus getStatus() {
        return this.status;
    }

    private LicenseContent getLicenseContent() {
        return this.content;
    }

    void setStatus(LicenseStatus status) {
        this.status = status;
    }

    public DefaultLicense copy() {
        return new DefaultLicense(this.status, this.content.copy());
    }

    public LicenseStatus validate(Date now) {
        LicenseStatus currentStatus = this.getLicenseContent().isValid(now);
        if(currentStatus != this.getStatus()) {
            this.setStatus(currentStatus);
        }

        return currentStatus;
    }

    public int getPrecedence() {
        return this.getLicenseContent().getPrecedence();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("License [");
        if(this.status != null) {
            builder.append("status=");
            builder.append(this.status);
            builder.append(", ");
        }

        if(this.content != null) {
            builder.append("content=");
            builder.append(this.content);
        }

        builder.append("]");
        return builder.toString();
    }

    public DefaultLicenseUser getLicenseUser() {
        DefaultLicenseUser licenseUser = this.getLicenseContent().getLicenseUser();
        return licenseUser != null?licenseUser:EMPTY_LICENSE_USER;
    }

    public Date getExpirationDate() {
        return this.getLicenseContent().getExpirationDate();
    }

    public Date getStartDate() {
        return this.getLicenseContent().getStartDate();
    }

    public String getProductEdition() {
        return this.getLicenseContent().getProductEdition();
    }

    public Constraints getConstraints() {
        return this.getLicenseContent().getConstraints();
    }

    public String getProductId() {
        return this.getLicenseContent().getProductId();
    }

    public String getLicenseID() {
        return this.getLicenseContent().getLicenseID();
    }

    public Set<String> getVersions() {
        List productVersions = this.getLicenseContent().getProductVersions();
        return productVersions != null?Collections.unmodifiableSet(new HashSet(productVersions)):Collections.emptySet();
    }

    public boolean isStarterLicense() {
        return "starter".equals(this.getProductEdition());
    }

    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (this.content == null?0:this.content.hashCode());
        result1 = 31 * result1 + (this.status == null?0:this.status.hashCode());
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
            DefaultLicense other = (DefaultLicense)obj;
            if(this.content == null) {
                if(other.content != null) {
                    return false;
                }
            } else if(!this.content.equals(other.content)) {
                return false;
            }

            return this.status == other.status;
        }
    }

    public int compareTo(License lic) {
        return Integer.compare(this.getPrecedence(), lic.getPrecedence());
    }
}
