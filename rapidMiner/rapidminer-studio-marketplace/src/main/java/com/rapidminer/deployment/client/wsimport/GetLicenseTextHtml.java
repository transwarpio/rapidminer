package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "getLicenseTextHtml",
        propOrder = {"licenseName"}
)
public class GetLicenseTextHtml {
    protected String licenseName;

    public GetLicenseTextHtml() {
    }

    public String getLicenseName() {
        return this.licenseName;
    }

    public void setLicenseName(String value) {
        this.licenseName = value;
    }
}
