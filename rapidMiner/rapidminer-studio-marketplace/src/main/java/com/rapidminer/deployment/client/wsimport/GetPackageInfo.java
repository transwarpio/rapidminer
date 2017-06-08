package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "getPackageInfo",
        propOrder = {"packageId", "version", "targetPlatform"}
)
public class GetPackageInfo {
    protected String packageId;
    protected String version;
    protected String targetPlatform;

    public GetPackageInfo() {
    }

    public String getPackageId() {
        return this.packageId;
    }

    public void setPackageId(String value) {
        this.packageId = value;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String value) {
        this.version = value;
    }

    public String getTargetPlatform() {
        return this.targetPlatform;
    }

    public void setTargetPlatform(String value) {
        this.targetPlatform = value;
    }
}
