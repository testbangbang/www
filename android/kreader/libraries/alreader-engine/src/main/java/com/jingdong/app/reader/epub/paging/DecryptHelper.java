package com.jingdong.app.reader.epub.paging;

public final class DecryptHelper {

    static {
        System.loadLibrary("jdrdrm");
    }

    public static native int init(String key, String device, String radom);

    public static native int create();

    public static native int decrypt(byte[] encryptedData, int encryptedLength, byte[] decryptedData, int decryLen, int deend);

    public static native void close();

    public static int initDecryptLibrary(String key, String device, String radom) {
        if (key != null && key.length() > 0) {
            return init(key, device, radom);
        }
        create();
        return 0;
    }
}
