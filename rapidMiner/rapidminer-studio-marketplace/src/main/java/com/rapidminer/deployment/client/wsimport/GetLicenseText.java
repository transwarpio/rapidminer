package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "getLicenseText",
        propOrder = {"licenseName"}
)
public class GetLicenseText {
    protected String licenseName;

    public GetLicenseText() {
    }

    public String getLicenseName() {
        return this.licenseName;
    }

    public void setLicenseName(String value) {
        this.licenseName = value;
    }
}
