package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.deployment.client.wsimport.ObjectFactory;
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.client.wsimport.UpdateServiceException_Exception;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

@WebService(
        name = "UpdateService",
        targetNamespace = "http://ws.update.deployment.rapid_i.com/"
)
@XmlSeeAlso({ObjectFactory.class})
public interface UpdateService {
    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getLicenseText",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetLicenseText"
    )
    @ResponseWrapper(
            localName = "getLicenseTextResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetLicenseTextResponse"
    )
    String getLicenseText(@WebParam(
            name = "licenseName",
            targetNamespace = ""
    ) String var1);

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getDownloadURL",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetDownloadURL"
    )
    @ResponseWrapper(
            localName = "getDownloadURLResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetDownloadURLResponse"
    )
    String getDownloadURL(@WebParam(
            name = "packageId",
            targetNamespace = ""
    ) String var1, @WebParam(
            name = "version",
            targetNamespace = ""
    ) String var2, @WebParam(
            name = "targetPlatform",
            targetNamespace = ""
    ) String var3) throws UpdateServiceException_Exception;

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getExtensions",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetExtensions"
    )
    @ResponseWrapper(
            localName = "getExtensionsResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetExtensionsResponse"
    )
    List<String> getExtensions(@WebParam(
            name = "basePackage",
            targetNamespace = ""
    ) String var1);

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getMirrors",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetMirrors"
    )
    @ResponseWrapper(
            localName = "getMirrorsResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetMirrorsResponse"
    )
    List<String> getMirrors();

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getRapidMinerExtensionForOperatorPrefix",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetRapidMinerExtensionForOperatorPrefix"
    )
    @ResponseWrapper(
            localName = "getRapidMinerExtensionForOperatorPrefixResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetRapidMinerExtensionForOperatorPrefixResponse"
    )
    String getRapidMinerExtensionForOperatorPrefix(@WebParam(
            name = "forPrefix",
            targetNamespace = ""
    ) String var1);

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getMessageOfTheDay",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetMessageOfTheDay"
    )
    @ResponseWrapper(
            localName = "getMessageOfTheDayResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetMessageOfTheDayResponse"
    )
    String getMessageOfTheDay();

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getTopRated",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetTopRated"
    )
    @ResponseWrapper(
            localName = "getTopRatedResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetTopRatedResponse"
    )
    List<String> getTopRated();

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getAvailableVersions",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetAvailableVersions"
    )
    @ResponseWrapper(
            localName = "getAvailableVersionsResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetAvailableVersionsResponse"
    )
    List<String> getAvailableVersions(@WebParam(
            name = "packageId",
            targetNamespace = ""
    ) String var1, @WebParam(
            name = "targetPlatform",
            targetNamespace = ""
    ) String var2);

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getTopDownloads",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetTopDownloads"
    )
    @ResponseWrapper(
            localName = "getTopDownloadsResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetTopDownloadsResponse"
    )
    List<String> getTopDownloads();

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getPackageInfo",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetPackageInfo"
    )
    @ResponseWrapper(
            localName = "getPackageInfoResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetPackageInfoResponse"
    )
    PackageDescriptor getPackageInfo(@WebParam(
            name = "packageId",
            targetNamespace = ""
    ) String var1, @WebParam(
            name = "version",
            targetNamespace = ""
    ) String var2, @WebParam(
            name = "targetPlatform",
            targetNamespace = ""
    ) String var3) throws UpdateServiceException_Exception;

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getLicenseTextHtml",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetLicenseTextHtml"
    )
    @ResponseWrapper(
            localName = "getLicenseTextHtmlResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetLicenseTextHtmlResponse"
    )
    String getLicenseTextHtml(@WebParam(
            name = "licenseName",
            targetNamespace = ""
    ) String var1);

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "searchFor",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.SearchFor"
    )
    @ResponseWrapper(
            localName = "searchForResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.SearchForResponse"
    )
    List<String> searchFor(@WebParam(
            name = "searchString",
            targetNamespace = ""
    ) String var1);

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "anyUpdatesSince",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.AnyUpdatesSince"
    )
    @ResponseWrapper(
            localName = "anyUpdatesSinceResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.AnyUpdatesSinceResponse"
    )
    boolean anyUpdatesSince(@WebParam(
            name = "since",
            targetNamespace = ""
    ) XMLGregorianCalendar var1);

    @WebMethod
    @WebResult(
            targetNamespace = ""
    )
    @RequestWrapper(
            localName = "getLatestVersion",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetLatestVersion"
    )
    @ResponseWrapper(
            localName = "getLatestVersionResponse",
            targetNamespace = "http://ws.update.deployment.rapid_i.com/",
            className = "com.rapidminer.deployment.client.wsimport.GetLatestVersionResponse"
    )
    String getLatestVersion(@WebParam(
            name = "packageId",
            targetNamespace = ""
    ) String var1, @WebParam(
            name = "targetPlatform",
            targetNamespace = ""
    ) String var2, @WebParam(
            name = "rapidMinerStudioVersion",
            targetNamespace = ""
    ) String var3) throws UpdateServiceException_Exception;
}
