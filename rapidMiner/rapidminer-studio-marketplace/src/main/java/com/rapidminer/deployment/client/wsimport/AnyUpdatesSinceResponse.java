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
        name = "anyUpdatesSinceResponse",
        propOrder = {"_return"}
)
public class AnyUpdatesSinceResponse {
    @XmlElement(
            name = "return"
    )
    protected boolean _return;

    public AnyUpdatesSinceResponse() {
    }

    public boolean isReturn() {
        return this._return;
    }

    public void setReturn(boolean value) {
        this._return = value;
    }
}
