package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "getPackageInfoResponse",
        propOrder = {"_return"}
)
public class GetPackageInfoResponse {
    @XmlElement(
            name = "return"
    )
    protected PackageDescriptor _return;

    public GetPackageInfoResponse() {
    }

    public PackageDescriptor getReturn() {
        return this._return;
    }

    public void setReturn(PackageDescriptor value) {
        this._return = value;
    }
}
