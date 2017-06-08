package com.rapidminer.tools.nexus;

/**
 * Created by mk on 3/9/16.
 */
import java.text.ParseException;
import java.util.Date;

class DownloadedLicense {
    private String userName;
    private String productKey;
    private String productEdition;
    private String startDate;
    private String expirationDate;
    private String licenseKey;

    DownloadedLicense() {
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductKey() {
        return this.productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getProductEdition() {
        return this.productEdition;
    }

    public void setProductEdition(String productEdition) {
        this.productEdition = productEdition;
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getLicenseKey() {
        return this.licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public boolean isExpired() {
        if(this.getExpirationDate() == null) {
            return false;
        } else {
            try {
                Date e = (NexusUtilities.ISO_DATE_FORMATTER.get()).parse(this.getExpirationDate());
                return e.before(new Date());
            } catch (ParseException var2) {
                return false;
            }
        }
    }
}
