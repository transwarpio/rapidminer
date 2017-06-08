package com.rapidminer.tools.nexus;

/**
 * Created by mk on 3/9/16.
 */
import com.rapidminer.tools.nexus.NexusError;
import java.io.IOException;

public class NexusCommunicationException extends IOException {
    private static final long serialVersionUID = -1942315210781247915L;
    private int statusCode;
    private String applicationStatusCode;

    public NexusCommunicationException(NexusError error) {
        super(error.getErrorMessage());
        this.statusCode = error.getStatusCode();
        this.applicationStatusCode = error.getApplicationStatusCode();
    }

    public NexusCommunicationException(int statusCode) {
        super("Unknown error");
        this.statusCode = statusCode;
        this.applicationStatusCode = null;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getApplicationStatusCode() {
        return this.applicationStatusCode;
    }
}
