package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.UpdateServiceException;
import javax.xml.ws.WebFault;

@WebFault(
        name = "UpdateServiceException",
        targetNamespace = "http://ws.update.deployment.rapid_i.com/"
)
public class UpdateServiceException_Exception extends Exception {
    private UpdateServiceException faultInfo;

    public UpdateServiceException_Exception(String message, UpdateServiceException faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public UpdateServiceException_Exception(String message, UpdateServiceException faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public UpdateServiceException getFaultInfo() {
        return this.faultInfo;
    }
}
