package com.onyx.kreader.plugins.comic;

/**
 * Created by joy on 3/16/16.
 */
public class UnrarJniWrapper {

    static{
        System.loadLibrary("unrar_jni");
    }

    public native boolean open(String filePath);
    public native boolean isEncrypted();
    public native void setPassword(String password);
    public native String[] getEntries();
    public native byte[] extractEntryData(String entryName);
    public native void close();
}
