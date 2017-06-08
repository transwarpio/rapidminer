package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "getExtensionsResponse",
        propOrder = {"_return"}
)
public class GetExtensionsResponse {
    @XmlElement(
            name = "return"
    )
    protected List<String> _return;

    public GetExtensionsResponse() {
    }

    public List<String> getReturn() {
        if(this._return == null) {
            this._return = new ArrayList();
        }

        return this._return;
    }
}
