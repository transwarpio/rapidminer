package com.rapidminer.tools.nexus;

/**
 * Created by mk on 3/9/16.
 */
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class NexusError {
    private int statusCode;
    private String applicationStatusCode;
    private String errorMessage;

    public NexusError() {
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getApplicationStatusCode() {
        return this.applicationStatusCode;
    }

    public void setApplicationStatusCode(String applicationStatusCode) {
        this.applicationStatusCode = applicationStatusCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
