package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.UpdateService;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(
        name = "UpdateServiceService",
        targetNamespace = "http://ws.update.deployment.rapid_i.com/",
        wsdlLocation = "http://localhost:8080/UpdateServer/UpdateServiceService?wsdl"
)
public class UpdateServiceService extends Service {
    private static final URL UPDATESERVICESERVICE_WSDL_LOCATION;
    private static final WebServiceException UPDATESERVICESERVICE_EXCEPTION;
    private static final QName UPDATESERVICESERVICE_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "UpdateServiceService");

    public UpdateServiceService() {
        super(__getWsdlLocation(), UPDATESERVICESERVICE_QNAME);
    }

    public UpdateServiceService(WebServiceFeature... features) {
        super(__getWsdlLocation(), UPDATESERVICESERVICE_QNAME, features);
    }

    public UpdateServiceService(URL wsdlLocation) {
        super(wsdlLocation, UPDATESERVICESERVICE_QNAME);
    }

    public UpdateServiceService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, UPDATESERVICESERVICE_QNAME, features);
    }

    public UpdateServiceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public UpdateServiceService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    @WebEndpoint(
            name = "UpdateServicePort"
    )
    public UpdateService getUpdateServicePort() {
        return (UpdateService)super.getPort(new QName("http://ws.update.deployment.rapid_i.com/", "UpdateServicePort"), UpdateService.class);
    }

    @WebEndpoint(
            name = "UpdateServicePort"
    )
    public UpdateService getUpdateServicePort(WebServiceFeature... features) {
        return (UpdateService)super.getPort(new QName("http://ws.update.deployment.rapid_i.com/", "UpdateServicePort"), UpdateService.class, features);
    }

    private static URL __getWsdlLocation() {
        if(UPDATESERVICESERVICE_EXCEPTION != null) {
            throw UPDATESERVICESERVICE_EXCEPTION;
        } else {
            return UPDATESERVICESERVICE_WSDL_LOCATION;
        }
    }

    static {
        URL url = null;
        WebServiceException e = null;

        try {
            url = new URL("http://localhost:8080/UpdateServer/UpdateServiceService?wsdl");
        } catch (MalformedURLException var3) {
            e = new WebServiceException(var3);
        }

        UPDATESERVICESERVICE_WSDL_LOCATION = url;
        UPDATESERVICESERVICE_EXCEPTION = e;
    }
}
