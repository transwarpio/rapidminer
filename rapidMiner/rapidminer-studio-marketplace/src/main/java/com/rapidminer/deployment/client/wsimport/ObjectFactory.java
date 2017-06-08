package com.rapidminer.deployment.client.wsimport;

/**
 * Created by mk on 3/10/16.
 */
import com.rapidminer.deployment.client.wsimport.AnyUpdatesSince;
import com.rapidminer.deployment.client.wsimport.AnyUpdatesSinceResponse;
import com.rapidminer.deployment.client.wsimport.GetAvailableVersions;
import com.rapidminer.deployment.client.wsimport.GetAvailableVersionsResponse;
import com.rapidminer.deployment.client.wsimport.GetDownloadURL;
import com.rapidminer.deployment.client.wsimport.GetDownloadURLResponse;
import com.rapidminer.deployment.client.wsimport.GetExtensions;
import com.rapidminer.deployment.client.wsimport.GetExtensionsResponse;
import com.rapidminer.deployment.client.wsimport.GetLatestVersion;
import com.rapidminer.deployment.client.wsimport.GetLatestVersionResponse;
import com.rapidminer.deployment.client.wsimport.GetLicenseText;
import com.rapidminer.deployment.client.wsimport.GetLicenseTextHtml;
import com.rapidminer.deployment.client.wsimport.GetLicenseTextHtmlResponse;
import com.rapidminer.deployment.client.wsimport.GetLicenseTextResponse;
import com.rapidminer.deployment.client.wsimport.GetMessageOfTheDay;
import com.rapidminer.deployment.client.wsimport.GetMessageOfTheDayResponse;
import com.rapidminer.deployment.client.wsimport.GetMirrors;
import com.rapidminer.deployment.client.wsimport.GetMirrorsResponse;
import com.rapidminer.deployment.client.wsimport.GetPackageInfo;
import com.rapidminer.deployment.client.wsimport.GetPackageInfoResponse;
import com.rapidminer.deployment.client.wsimport.GetRapidMinerExtensionForOperatorPrefix;
import com.rapidminer.deployment.client.wsimport.GetRapidMinerExtensionForOperatorPrefixResponse;
import com.rapidminer.deployment.client.wsimport.GetTopDownloads;
import com.rapidminer.deployment.client.wsimport.GetTopDownloadsResponse;
import com.rapidminer.deployment.client.wsimport.GetTopRated;
import com.rapidminer.deployment.client.wsimport.GetTopRatedResponse;
import com.rapidminer.deployment.client.wsimport.PackageDescriptor;
import com.rapidminer.deployment.client.wsimport.SearchFor;
import com.rapidminer.deployment.client.wsimport.SearchForResponse;
import com.rapidminer.deployment.client.wsimport.UpdateServiceException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    private static final QName _GetMirrors_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getMirrors");
    private static final QName _GetTopRated_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getTopRated");
    private static final QName _GetMessageOfTheDayResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getMessageOfTheDayResponse");
    private static final QName _GetPackageInfo_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getPackageInfo");
    private static final QName _GetTopDownloads_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getTopDownloads");
    private static final QName _SearchForResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "searchForResponse");
    private static final QName _UpdateServiceException_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "UpdateServiceException");
    private static final QName _GetExtensionsResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getExtensionsResponse");
    private static final QName _GetExtensions_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getExtensions");
    private static final QName _GetRapidMinerExtensionForOperatorPrefix_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getRapidMinerExtensionForOperatorPrefix");
    private static final QName _AnyUpdatesSinceResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "anyUpdatesSinceResponse");
    private static final QName _GetMessageOfTheDay_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getMessageOfTheDay");
    private static final QName _GetDownloadURL_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getDownloadURL");
    private static final QName _AnyUpdatesSince_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "anyUpdatesSince");
    private static final QName _GetLicenseText_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getLicenseText");
    private static final QName _GetLicenseTextResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getLicenseTextResponse");
    private static final QName _SearchFor_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "searchFor");
    private static final QName _GetAvailableVersionsResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getAvailableVersionsResponse");
    private static final QName _GetDownloadURLResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getDownloadURLResponse");
    private static final QName _GetTopRatedResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getTopRatedResponse");
    private static final QName _GetLicenseTextHtml_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getLicenseTextHtml");
    private static final QName _GetLatestVersionResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getLatestVersionResponse");
    private static final QName _GetRapidMinerExtensionForOperatorPrefixResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getRapidMinerExtensionForOperatorPrefixResponse");
    private static final QName _GetPackageInfoResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getPackageInfoResponse");
    private static final QName _GetMirrorsResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getMirrorsResponse");
    private static final QName _GetAvailableVersions_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getAvailableVersions");
    private static final QName _GetLicenseTextHtmlResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getLicenseTextHtmlResponse");
    private static final QName _GetLatestVersion_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getLatestVersion");
    private static final QName _GetTopDownloadsResponse_QNAME = new QName("http://ws.update.deployment.rapid_i.com/", "getTopDownloadsResponse");

    public ObjectFactory() {
    }

    public GetAvailableVersionsResponse createGetAvailableVersionsResponse() {
        return new GetAvailableVersionsResponse();
    }

    public GetDownloadURLResponse createGetDownloadURLResponse() {
        return new GetDownloadURLResponse();
    }

    public GetTopRatedResponse createGetTopRatedResponse() {
        return new GetTopRatedResponse();
    }

    public GetLicenseTextHtml createGetLicenseTextHtml() {
        return new GetLicenseTextHtml();
    }

    public GetLatestVersionResponse createGetLatestVersionResponse() {
        return new GetLatestVersionResponse();
    }

    public GetRapidMinerExtensionForOperatorPrefixResponse createGetRapidMinerExtensionForOperatorPrefixResponse() {
        return new GetRapidMinerExtensionForOperatorPrefixResponse();
    }

    public GetPackageInfoResponse createGetPackageInfoResponse() {
        return new GetPackageInfoResponse();
    }

    public GetMirrorsResponse createGetMirrorsResponse() {
        return new GetMirrorsResponse();
    }

    public GetAvailableVersions createGetAvailableVersions() {
        return new GetAvailableVersions();
    }

    public GetLicenseTextHtmlResponse createGetLicenseTextHtmlResponse() {
        return new GetLicenseTextHtmlResponse();
    }

    public GetLatestVersion createGetLatestVersion() {
        return new GetLatestVersion();
    }

    public GetTopDownloadsResponse createGetTopDownloadsResponse() {
        return new GetTopDownloadsResponse();
    }

    public GetMessageOfTheDayResponse createGetMessageOfTheDayResponse() {
        return new GetMessageOfTheDayResponse();
    }

    public GetPackageInfo createGetPackageInfo() {
        return new GetPackageInfo();
    }

    public GetTopDownloads createGetTopDownloads() {
        return new GetTopDownloads();
    }

    public GetMirrors createGetMirrors() {
        return new GetMirrors();
    }

    public GetTopRated createGetTopRated() {
        return new GetTopRated();
    }

    public UpdateServiceException createUpdateServiceException() {
        return new UpdateServiceException();
    }

    public SearchForResponse createSearchForResponse() {
        return new SearchForResponse();
    }

    public GetExtensionsResponse createGetExtensionsResponse() {
        return new GetExtensionsResponse();
    }

    public GetExtensions createGetExtensions() {
        return new GetExtensions();
    }

    public AnyUpdatesSinceResponse createAnyUpdatesSinceResponse() {
        return new AnyUpdatesSinceResponse();
    }

    public GetMessageOfTheDay createGetMessageOfTheDay() {
        return new GetMessageOfTheDay();
    }

    public GetRapidMinerExtensionForOperatorPrefix createGetRapidMinerExtensionForOperatorPrefix() {
        return new GetRapidMinerExtensionForOperatorPrefix();
    }

    public GetDownloadURL createGetDownloadURL() {
        return new GetDownloadURL();
    }

    public AnyUpdatesSince createAnyUpdatesSince() {
        return new AnyUpdatesSince();
    }

    public GetLicenseText createGetLicenseText() {
        return new GetLicenseText();
    }

    public GetLicenseTextResponse createGetLicenseTextResponse() {
        return new GetLicenseTextResponse();
    }

    public SearchFor createSearchFor() {
        return new SearchFor();
    }

    public PackageDescriptor createPackageDescriptor() {
        return new PackageDescriptor();
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getMirrors"
    )
    public JAXBElement<GetMirrors> createGetMirrors(GetMirrors value) {
        return new JAXBElement(_GetMirrors_QNAME, GetMirrors.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getTopRated"
    )
    public JAXBElement<GetTopRated> createGetTopRated(GetTopRated value) {
        return new JAXBElement(_GetTopRated_QNAME, GetTopRated.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getMessageOfTheDayResponse"
    )
    public JAXBElement<GetMessageOfTheDayResponse> createGetMessageOfTheDayResponse(GetMessageOfTheDayResponse value) {
        return new JAXBElement(_GetMessageOfTheDayResponse_QNAME, GetMessageOfTheDayResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getPackageInfo"
    )
    public JAXBElement<GetPackageInfo> createGetPackageInfo(GetPackageInfo value) {
        return new JAXBElement(_GetPackageInfo_QNAME, GetPackageInfo.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getTopDownloads"
    )
    public JAXBElement<GetTopDownloads> createGetTopDownloads(GetTopDownloads value) {
        return new JAXBElement(_GetTopDownloads_QNAME, GetTopDownloads.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "searchForResponse"
    )
    public JAXBElement<SearchForResponse> createSearchForResponse(SearchForResponse value) {
        return new JAXBElement(_SearchForResponse_QNAME, SearchForResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "UpdateServiceException"
    )
    public JAXBElement<UpdateServiceException> createUpdateServiceException(UpdateServiceException value) {
        return new JAXBElement(_UpdateServiceException_QNAME, UpdateServiceException.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getExtensionsResponse"
    )
    public JAXBElement<GetExtensionsResponse> createGetExtensionsResponse(GetExtensionsResponse value) {
        return new JAXBElement(_GetExtensionsResponse_QNAME, GetExtensionsResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getExtensions"
    )
    public JAXBElement<GetExtensions> createGetExtensions(GetExtensions value) {
        return new JAXBElement(_GetExtensions_QNAME, GetExtensions.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getRapidMinerExtensionForOperatorPrefix"
    )
    public JAXBElement<GetRapidMinerExtensionForOperatorPrefix> createGetRapidMinerExtensionForOperatorPrefix(GetRapidMinerExtensionForOperatorPrefix value) {
        return new JAXBElement(_GetRapidMinerExtensionForOperatorPrefix_QNAME, GetRapidMinerExtensionForOperatorPrefix.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "anyUpdatesSinceResponse"
    )
    public JAXBElement<AnyUpdatesSinceResponse> createAnyUpdatesSinceResponse(AnyUpdatesSinceResponse value) {
        return new JAXBElement(_AnyUpdatesSinceResponse_QNAME, AnyUpdatesSinceResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getMessageOfTheDay"
    )
    public JAXBElement<GetMessageOfTheDay> createGetMessageOfTheDay(GetMessageOfTheDay value) {
        return new JAXBElement(_GetMessageOfTheDay_QNAME, GetMessageOfTheDay.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getDownloadURL"
    )
    public JAXBElement<GetDownloadURL> createGetDownloadURL(GetDownloadURL value) {
        return new JAXBElement(_GetDownloadURL_QNAME, GetDownloadURL.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "anyUpdatesSince"
    )
    public JAXBElement<AnyUpdatesSince> createAnyUpdatesSince(AnyUpdatesSince value) {
        return new JAXBElement(_AnyUpdatesSince_QNAME, AnyUpdatesSince.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getLicenseText"
    )
    public JAXBElement<GetLicenseText> createGetLicenseText(GetLicenseText value) {
        return new JAXBElement(_GetLicenseText_QNAME, GetLicenseText.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getLicenseTextResponse"
    )
    public JAXBElement<GetLicenseTextResponse> createGetLicenseTextResponse(GetLicenseTextResponse value) {
        return new JAXBElement(_GetLicenseTextResponse_QNAME, GetLicenseTextResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "searchFor"
    )
    public JAXBElement<SearchFor> createSearchFor(SearchFor value) {
        return new JAXBElement(_SearchFor_QNAME, SearchFor.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getAvailableVersionsResponse"
    )
    public JAXBElement<GetAvailableVersionsResponse> createGetAvailableVersionsResponse(GetAvailableVersionsResponse value) {
        return new JAXBElement(_GetAvailableVersionsResponse_QNAME, GetAvailableVersionsResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getDownloadURLResponse"
    )
    public JAXBElement<GetDownloadURLResponse> createGetDownloadURLResponse(GetDownloadURLResponse value) {
        return new JAXBElement(_GetDownloadURLResponse_QNAME, GetDownloadURLResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getTopRatedResponse"
    )
    public JAXBElement<GetTopRatedResponse> createGetTopRatedResponse(GetTopRatedResponse value) {
        return new JAXBElement(_GetTopRatedResponse_QNAME, GetTopRatedResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getLicenseTextHtml"
    )
    public JAXBElement<GetLicenseTextHtml> createGetLicenseTextHtml(GetLicenseTextHtml value) {
        return new JAXBElement(_GetLicenseTextHtml_QNAME, GetLicenseTextHtml.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getLatestVersionResponse"
    )
    public JAXBElement<GetLatestVersionResponse> createGetLatestVersionResponse(GetLatestVersionResponse value) {
        return new JAXBElement(_GetLatestVersionResponse_QNAME, GetLatestVersionResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getRapidMinerExtensionForOperatorPrefixResponse"
    )
    public JAXBElement<GetRapidMinerExtensionForOperatorPrefixResponse> createGetRapidMinerExtensionForOperatorPrefixResponse(GetRapidMinerExtensionForOperatorPrefixResponse value) {
        return new JAXBElement(_GetRapidMinerExtensionForOperatorPrefixResponse_QNAME, GetRapidMinerExtensionForOperatorPrefixResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getPackageInfoResponse"
    )
    public JAXBElement<GetPackageInfoResponse> createGetPackageInfoResponse(GetPackageInfoResponse value) {
        return new JAXBElement(_GetPackageInfoResponse_QNAME, GetPackageInfoResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getMirrorsResponse"
    )
    public JAXBElement<GetMirrorsResponse> createGetMirrorsResponse(GetMirrorsResponse value) {
        return new JAXBElement(_GetMirrorsResponse_QNAME, GetMirrorsResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getAvailableVersions"
    )
    public JAXBElement<GetAvailableVersions> createGetAvailableVersions(GetAvailableVersions value) {
        return new JAXBElement(_GetAvailableVersions_QNAME, GetAvailableVersions.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getLicenseTextHtmlResponse"
    )
    public JAXBElement<GetLicenseTextHtmlResponse> createGetLicenseTextHtmlResponse(GetLicenseTextHtmlResponse value) {
        return new JAXBElement(_GetLicenseTextHtmlResponse_QNAME, GetLicenseTextHtmlResponse.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getLatestVersion"
    )
    public JAXBElement<GetLatestVersion> createGetLatestVersion(GetLatestVersion value) {
        return new JAXBElement(_GetLatestVersion_QNAME, GetLatestVersion.class, (Class)null, value);
    }

    @XmlElementDecl(
            namespace = "http://ws.update.deployment.rapid_i.com/",
            name = "getTopDownloadsResponse"
    )
    public JAXBElement<GetTopDownloadsResponse> createGetTopDownloadsResponse(GetTopDownloadsResponse value) {
        return new JAXBElement(_GetTopDownloadsResponse_QNAME, GetTopDownloadsResponse.class, (Class)null, value);
    }
}