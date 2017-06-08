package com.rapidminer.tools.nexus;

/**
 * Created by mk on 3/9/16.
 */
public class EmailVerificationStatus {
    private String email;
    private boolean emailVerified;

    public EmailVerificationStatus() {
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return this.emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
