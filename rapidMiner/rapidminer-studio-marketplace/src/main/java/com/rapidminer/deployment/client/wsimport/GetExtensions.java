package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "getExtensions",
        propOrder = {"basePackage"}
)
public class GetExtensions {
    protected String basePackage;

    public GetExtensions() {
    }

    public String getBasePackage() {
        return this.basePackage;
    }

    public void setBasePackage(String value) {
        this.basePackage = value;
    }
}
