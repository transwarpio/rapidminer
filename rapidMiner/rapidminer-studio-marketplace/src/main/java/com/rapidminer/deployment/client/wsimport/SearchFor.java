package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "searchFor",
        propOrder = {"searchString"}
)
public class SearchFor {
    protected String searchString;

    public SearchFor() {
    }

    public String getSearchString() {
        return this.searchString;
    }

    public void setSearchString(String value) {
        this.searchString = value;
    }
}
