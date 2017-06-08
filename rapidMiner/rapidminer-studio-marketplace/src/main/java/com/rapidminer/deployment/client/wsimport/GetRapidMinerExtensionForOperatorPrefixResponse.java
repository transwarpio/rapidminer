package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "getRapidMinerExtensionForOperatorPrefixResponse",
        propOrder = {"_return"}
)
public class GetRapidMinerExtensionForOperatorPrefixResponse {
    @XmlElement(
            name = "return"
    )
    protected String _return;

    public GetRapidMinerExtensionForOperatorPrefixResponse() {
    }

    public String getReturn() {
        return this._return;
    }

    public void setReturn(String value) {
        this._return = value;
    }
}
