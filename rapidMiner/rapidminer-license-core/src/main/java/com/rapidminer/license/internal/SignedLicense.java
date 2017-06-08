package com.rapidminer.license.internal;

/**
 * Created by mk on 3/10/16.
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;

public final class SignedLicense {
    @JsonProperty
    private final String licenseJSON;
    @JsonProperty
    private final byte[] signature;

    protected SignedLicense() {
        this.licenseJSON = null;
        this.signature = null;
    }

    public SignedLicense(String licenseJSON, byte[] signature) {
        this.licenseJSON = licenseJSON;
        this.signature = Arrays.copyOf(signature, signature.length);
    }

    public String getLicenseJSON() {
        return this.licenseJSON;
    }

    public byte[] getSignature() {
        return Arrays.copyOf(this.signature, this.signature.length);
    }
}
