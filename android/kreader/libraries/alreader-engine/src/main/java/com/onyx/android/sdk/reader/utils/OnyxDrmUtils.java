package com.onyx.android.sdk.reader.utils;

/**
 * Created by joy on 8/4/17.
 */

public class OnyxDrmUtils {

    static {
        System.loadLibrary("neo_pdf");
    }

    public static native boolean setup(String deviceId, String drmCertificate, String manifestBase64, String additionalDataBase64);

    /**
     * size of result must be equal or larger than encryptedSize
     *
     * @param encryptedData
     * @param encryptedSize
     * @param result
     * @return
     */
    public static native int decrypt(byte[] encryptedData, int encryptedSize, byte[] result);
}
