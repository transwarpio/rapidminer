package com.rapidminer.license.internal;

/**
 * Created by mk on 3/10/16.
 */
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapidminer.license.Constraints;
import com.rapidminer.license.DefaultConstraints;
import com.rapidminer.license.LicenseStatus;
import com.rapidminer.license.LicenseUser;
import com.rapidminer.license.internal.DefaultLicenseUser;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class LicenseContent {
    @JsonProperty
    private final String productId;
    @JsonProperty
    private final List<String> productVersions;
    @JsonProperty
    private final String productEdition;
    @JsonProperty
    private final int precedence;
    @JsonProperty
    private final String licenseKey;
    @JsonProperty
    private final Date startDate;
    @JsonProperty
    private final Date expirationDate;
    @JsonProperty
    private final DefaultLicenseUser licenseUser;
    @JsonProperty
    private final DefaultConstraints constraints;
    @JsonProperty
    private final String licenseID;
    @JsonProperty
    private final String annotations;

    public LicenseContent(String productId, List<String> productVersions, String productEdition, int precedence, String licenseKey, Date startDate, Date expirationDate, LicenseUser licenseUser, DefaultConstraints constraints, String licenseID, String annotations) {
        this.productId = productId;
        this.productVersions = productVersions;
        this.productEdition = productEdition;
        this.precedence = precedence;
        this.licenseKey = licenseKey;
        this.startDate = startDate != null?new Date(startDate.getTime()):null;
        this.expirationDate = expirationDate != null?new Date(expirationDate.getTime()):null;
        this.licenseUser = new DefaultLicenseUser(licenseUser.getName(), licenseUser.getEmail(), licenseUser.getProperties());
        this.constraints = constraints;
        this.licenseID = licenseID;
        this.annotations = annotations;
    }

    LicenseContent() {
        this.productId = null;
        this.productVersions = null;
        this.productEdition = null;
        this.precedence = 0;
        this.licenseKey = null;
        this.startDate = null;
        this.expirationDate = null;
        this.licenseUser = new DefaultLicenseUser();
        this.constraints = new DefaultConstraints();
        this.licenseID = null;
        this.annotations = null;
    }

    public String getAnnotations() {
        return this.annotations;
    }

    public Date getStartDate() {
        return this.startDate != null?new Date(this.startDate.getTime()):null;
    }

    public Date getExpirationDate() {
        return this.expirationDate != null?new Date(this.expirationDate.getTime()):null;
    }

    public String getProductId() {
        return this.productId;
    }

    public List<String> getProductVersions() {
        return this.productVersions;
    }

    public String getProductEdition() {
        return this.productEdition;
    }

    public int getPrecedence() {
        return this.precedence;
    }

    public String getLicenseKey() {
        return this.licenseKey;
    }

    public DefaultLicenseUser getLicenseUser() {
        return this.licenseUser;
    }

    public Constraints getConstraints() {
        return this.constraints;
    }

    public String getLicenseID() {
        return this.licenseID;
    }

    @JsonIgnore
    LicenseStatus isValid(Date now) {
        Date startDate = this.shiftDate(-1, this.getStartDate());
        Date expirationDate = this.shiftDate(1, this.getExpirationDate());
        boolean afterStart = startDate == null || startDate.before(now);
        boolean beforeExpiration = expirationDate == null || expirationDate.after(now);
        return afterStart && beforeExpiration?LicenseStatus.VALID:(startDate != null && startDate.after(now)?LicenseStatus.STARTS_IN_FUTURE:LicenseStatus.EXPIRED);
    }

    private Date shiftDate(int hours, Date date) {
        if(date == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(10, hours);
            return cal.getTime();
        }
    }

    final LicenseContent copy() {
        DefaultLicenseUser licenseUserCopy = this.licenseUser == null?new DefaultLicenseUser():this.licenseUser.copy();
        DefaultConstraints constraintCopy = this.constraints == null?new DefaultConstraints():this.constraints.copy();
        ArrayList productVersionCopy = this.productVersions != null?new ArrayList(this.productVersions):null;
        return new LicenseContent(this.productId, productVersionCopy, this.productEdition, this.precedence, this.licenseKey, this.startDate, this.expirationDate, licenseUserCopy, constraintCopy, this.licenseID, this.annotations);
    }

    public String toString() {
        return "LicenseContent [annotations=" + this.annotations + ", productId=" + this.productId + ", productVersions=" + this.productVersions + ", productEdition=" + this.productEdition + ", precedence=" + this.precedence + ", licenseKey=" + this.licenseKey + ", startDate=" + this.startDate + ", expirationDate=" + this.expirationDate + ", licenseUser=" + this.licenseUser + ", constraints=" + this.constraints + ", licenseID=" + this.licenseID + "]";
    }

    public int hashCode() {
        boolean prime = true;
        byte result = 1;
        int result1 = 31 * result + (this.annotations == null?0:this.annotations.hashCode());
        result1 = 31 * result1 + (this.constraints == null?0:this.constraints.hashCode());
        result1 = 31 * result1 + (this.expirationDate == null?0:this.expirationDate.hashCode());
        result1 = 31 * result1 + (this.licenseID == null?0:this.licenseID.hashCode());
        result1 = 31 * result1 + (this.licenseKey == null?0:this.licenseKey.hashCode());
        result1 = 31 * result1 + (this.licenseUser == null?0:this.licenseUser.hashCode());
        result1 = 31 * result1 + this.precedence;
        result1 = 31 * result1 + (this.productEdition == null?0:this.productEdition.hashCode());
        result1 = 31 * result1 + (this.productId == null?0:this.productId.hashCode());
        result1 = 31 * result1 + (this.productVersions == null?0:this.productVersions.hashCode());
        result1 = 31 * result1 + (this.startDate == null?0:this.startDate.hashCode());
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
            LicenseContent other = (LicenseContent)obj;
            if(this.annotations == null) {
                if(other.annotations != null) {
                    return false;
                }
            } else if(!this.annotations.equals(other.annotations)) {
                return false;
            }

            if(this.constraints == null) {
                if(other.constraints != null) {
                    return false;
                }
            } else if(!this.constraints.equals(other.constraints)) {
                return false;
            }

            if(this.expirationDate == null) {
                if(other.expirationDate != null) {
                    return false;
                }
            } else if(!this.expirationDate.equals(other.expirationDate)) {
                return false;
            }

            if(this.licenseID == null) {
                if(other.licenseID != null) {
                    return false;
                }
            } else if(!this.licenseID.equals(other.licenseID)) {
                return false;
            }

            if(this.licenseKey == null) {
                if(other.licenseKey != null) {
                    return false;
                }
            } else if(!this.licenseKey.equals(other.licenseKey)) {
                return false;
            }

            if(this.licenseUser == null) {
                if(other.licenseUser != null) {
                    return false;
                }
            } else if(!this.licenseUser.equals(other.licenseUser)) {
                return false;
            }

            if(this.precedence != other.precedence) {
                return false;
            } else {
                if(this.productEdition == null) {
                    if(other.productEdition != null) {
                        return false;
                    }
                } else if(!this.productEdition.equals(other.productEdition)) {
                    return false;
                }

                if(this.productId == null) {
                    if(other.productId != null) {
                        return false;
                    }
                } else if(!this.productId.equals(other.productId)) {
                    return false;
                }

                if(this.productVersions == null) {
                    if(other.productVersions != null) {
                        return false;
                    }
                } else if(!this.productVersions.equals(other.productVersions)) {
                    return false;
                }

                if(this.startDate == null) {
                    if(other.startDate != null) {
                        return false;
                    }
                } else if(!this.startDate.equals(other.startDate)) {
                    return false;
                }

                return true;
            }
        }
    }
}
