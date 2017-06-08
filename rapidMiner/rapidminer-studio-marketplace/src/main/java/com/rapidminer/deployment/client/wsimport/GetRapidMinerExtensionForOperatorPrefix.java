package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "getRapidMinerExtensionForOperatorPrefix",
        propOrder = {"forPrefix"}
)
public class GetRapidMinerExtensionForOperatorPrefix {
    protected String forPrefix;

    public GetRapidMinerExtensionForOperatorPrefix() {
    }

    public String getForPrefix() {
        return this.forPrefix;
    }

    public void setForPrefix(String value) {
        this.forPrefix = value;
    }
}