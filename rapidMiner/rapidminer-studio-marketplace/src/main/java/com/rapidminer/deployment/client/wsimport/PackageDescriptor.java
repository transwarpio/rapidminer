package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/9/16.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "packageDescriptor",
        propOrder = {"commitHash", "creationTime", "dependencies", "description", "icon", "latestCompatibleRapidMinerVersion", "licenseName", "longDescription", "minimalCompatibleRapidMinerVersion", "name", "owner", "packageId", "packageTypeName", "platformName", "restricted", "size", "version"}
)
public class PackageDescriptor {
    protected String commitHash;
    @XmlSchemaType(
            name = "dateTime"
    )
    protected XMLGregorianCalendar creationTime;
    protected String dependencies;
    protected String description;
    protected byte[] icon;
    protected String latestCompatibleRapidMinerVersion;
    protected String licenseName;
    protected String longDescription;
    protected String minimalCompatibleRapidMinerVersion;
    protected String name;
    protected String owner;
    protected String packageId;
    protected String packageTypeName;
    protected String platformName;
    protected boolean restricted;
    protected int size;
    protected String version;

    public PackageDescriptor() {
    }

    public String getCommitHash() {
        return this.commitHash;
    }

    public void setCommitHash(String value) {
        this.commitHash = value;
    }

    public XMLGregorianCalendar getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(XMLGregorianCalendar value) {
        this.creationTime = value;
    }

    public String getDependencies() {
        return this.dependencies;
    }

    public void setDependencies(String value) {
        this.dependencies = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public byte[] getIcon() {
        return this.icon;
    }

    public void setIcon(byte[] value) {
        this.icon = value;
    }

    public String getLatestCompatibleRapidMinerVersion() {
        return this.latestCompatibleRapidMinerVersion;
    }

    public void setLatestCompatibleRapidMinerVersion(String value) {
        this.latestCompatibleRapidMinerVersion = value;
    }

    public String getLicenseName() {
        return this.licenseName;
    }

    public void setLicenseName(String value) {
        this.licenseName = value;
    }

    public String getLongDescription() {
        return this.longDescription;
    }

    public void setLongDescription(String value) {
        this.longDescription = value;
    }

    public String getMinimalCompatibleRapidMinerVersion() {
        return this.minimalCompatibleRapidMinerVersion;
    }

    public void setMinimalCompatibleRapidMinerVersion(String value) {
        this.minimalCompatibleRapidMinerVersion = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String value) {
        this.owner = value;
    }

    public String getPackageId() {
        return this.packageId;
    }

    public void setPackageId(String value) {
        this.packageId = value;
    }

    public String getPackageTypeName() {
        return this.packageTypeName;
    }

    public void setPackageTypeName(String value) {
        this.packageTypeName = value;
    }

    public String getPlatformName() {
        return this.platformName;
    }

    public void setPlatformName(String value) {
        this.platformName = value;
    }

    public boolean isRestricted() {
        return this.restricted;
    }

    public void setRestricted(boolean value) {
        this.restricted = value;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int value) {
        this.size = value;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String value) {
        this.version = value;
    }
}
