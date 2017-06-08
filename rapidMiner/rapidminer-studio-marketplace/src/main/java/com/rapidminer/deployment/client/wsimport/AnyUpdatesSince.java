package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "anyUpdatesSince",
        propOrder = {"since"}
)
public class AnyUpdatesSince {
    @XmlSchemaType(
            name = "dateTime"
    )
    protected XMLGregorianCalendar since;

    public AnyUpdatesSince() {
    }

    public XMLGregorianCalendar getSince() {
        return this.since;
    }

    public void setSince(XMLGregorianCalendar value) {
        this.since = value;
    }
}