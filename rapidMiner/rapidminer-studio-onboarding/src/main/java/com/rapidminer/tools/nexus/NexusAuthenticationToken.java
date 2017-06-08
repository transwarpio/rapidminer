package com.rapidminer.tools.nexus;

/**
 * Created by mk on 3/9/16.
 */
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.nexus.NexusUtilities;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.codec.binary.Base64;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class NexusAuthenticationToken {
    private String authenticationToken;
    private String expirationDate;

    public NexusAuthenticationToken() {
    }

    public String getAuthenticationToken() {
        return this.authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    public String getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getExpireDate() {
        Date expDate = null;

        try {
            expDate = ((DateFormat)NexusUtilities.ISO_DATE_FORMATTER.get()).parse(this.expirationDate);
        } catch (ParseException var3) {
            LogService.getRoot().log(Level.WARNING, "com.rapidminer.tools.account.TokenAnswer.date_parse_failure", this.expirationDate);
        }

        return expDate;
    }

    public String getIdentityProviderId() {
        String token = this.getAuthenticationToken();
        if(token != null) {
            JsonNode jwtHeader = this.getJwtHeader(token);
            return jwtHeader == null?null:(jwtHeader.has("sub")?jwtHeader.get("sub").asText():null);
        } else {
            return null;
        }
    }

    public boolean isEmailVerified() {
        String token = this.getAuthenticationToken();
        if(token != null) {
            JsonNode jwtHeader = this.getJwtHeader(token);
            if(jwtHeader == null) {
                return false;
            } else {
                String emailVerified = jwtHeader.has("email_verified")?jwtHeader.get("email_verified").asText():null;
                return Boolean.parseBoolean(emailVerified);
            }
        } else {
            return false;
        }
    }

    private JsonNode getJwtHeader(String token) {
        String encodedPayload = token.substring(token.indexOf(46) + 1);
        encodedPayload = encodedPayload.substring(0, encodedPayload.indexOf(46));

        try {
            String e = new String(Base64.decodeBase64(encodedPayload), StandardCharsets.UTF_8.name());
            JsonNode jwtHeader = (JsonNode)(new ObjectMapper()).readValue(e, JsonNode.class);
            return jwtHeader;
        } catch (IOException var5) {
            return null;
        }
    }
}
