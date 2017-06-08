package com.rapidminer.deployment.update.client;

/**
 * Created by mk on 3/10/16.
 */
public class ChecksumException extends Exception {
    private static final long serialVersionUID = 1L;

    public ChecksumException() {
        super("Checksum not matched");
    }
}