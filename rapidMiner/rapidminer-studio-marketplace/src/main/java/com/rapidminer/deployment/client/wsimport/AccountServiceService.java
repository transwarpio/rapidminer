package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.AccountService;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(
        name = "AccountServiceService",
        targetNamespace = "http://ws.update.deployment.rapid_i.com/",
        wsdlLocation = "http://anni:8080/UpdateServer/AccountService?wsdl"
)
public class AccountServiceService extends Service {
    private static final URL ACCOUNTSERVICESERVICE_WSDL_LOCATION;
    private static final Logger logger = Logger.getLogger(AccountServiceService.class.getName());

    public AccountServiceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public AccountServiceService() {
        super(ACCOUNTSERVICESERVICE_WSDL_LOCATION, new QName("http://ws.update.deployment.rapid_i.com/", "AccountServiceService"));
    }

    @WebEndpoint(
            name = "AccountServicePort"
    )
    public AccountService getAccountServicePort() {
        return (AccountService)super.getPort(new QName("http://ws.update.deployment.rapid_i.com/", "AccountServicePort"), AccountService.class);
    }

    @WebEndpoint(
            name = "AccountServicePort"
    )
    public AccountService getAccountServicePort(WebServiceFeature... features) {
        return (AccountService)super.getPort(new QName("http://ws.update.deployment.rapid_i.com/", "AccountServicePort"), AccountService.class, features);
    }

    static {
        URL url = null;

        try {
            URL e = AccountServiceService.class.getResource(".");
            url = new URL(e, "http://anni:8080/UpdateServer/AccountService?wsdl");
        } catch (MalformedURLException var2) {
            logger.warning("Failed to create URL for the wsdl Location: \'http://anni:8080/UpdateServer/AccountService?wsdl\', retrying as a local file");
            logger.warning(var2.getMessage());
        }

        ACCOUNTSERVICESERVICE_WSDL_LOCATION = url;
    }
}
