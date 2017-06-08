package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "getLatestVersion",
        propOrder = {"packageId", "targetPlatform", "rapidMinerStudioVersion"}
)
public class GetLatestVersion {
    protected String packageId;
    protected String targetPlatform;
    protected String rapidMinerStudioVersion;

    public GetLatestVersion() {
    }

    public String getPackageId() {
        return this.packageId;
    }

    public void setPackageId(String value) {
        this.packageId = value;
    }

    public String getTargetPlatform() {
        return this.targetPlatform;
    }

    public void setTargetPlatform(String value) {
        this.targetPlatform = value;
    }

    public String getRapidMinerStudioVersion() {
        return this.rapidMinerStudioVersion;
    }

    public void setRapidMinerStudioVersion(String value) {
        this.rapidMinerStudioVersion = value;
    }
}
