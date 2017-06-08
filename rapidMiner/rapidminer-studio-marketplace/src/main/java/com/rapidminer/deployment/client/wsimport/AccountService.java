package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.ObjectFactory;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(
        name = "AccountService",
        targetNamespace = "http://ws.update.deployment.rapid_i.com/"
)
@XmlSeeAlso({ObjectFactory.class})
public interface AccountService {
    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getBookmarkedProducts",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetBookmarkedProducts"
    )
    @ResponseWrapper(
            localName = "getBookmarkedProductsResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetBookmarkedProductsResponse"
    )
    List<String> getBookmarkedProducts(@WebParam(
            name = "basePackage",
            targetNamespace = ""
    ) String var1);

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getLicensedProducts",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetLicensedProducts"
    )
    @ResponseWrapper(
            localName = "getLicensedProductsResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetLicensedProductsResponse"
    )
    List<String> getLicensedProducts();
}
