package com.rapidminer.license.utils;

/**
 * Created by mk on 3/10/16.
 */
public final class LicenseUtilities {
    private LicenseUtilities() {
        throw new RuntimeException("Cannot instantiate utility class");
    }

    public static final String numberToHex(Number n, int chars) {
        return String.format("%0" + chars + "x", new Object[]{n});
    }
}
