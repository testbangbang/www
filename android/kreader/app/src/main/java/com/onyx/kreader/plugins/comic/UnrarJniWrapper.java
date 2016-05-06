package com.onyx.kreader.plugins.comic;

import com.onyx.kreader.utils.StringUtils;

/**
 * Created by joy on 3/16/16.
 */
public class UnrarJniWrapper {

    static{
        System.loadLibrary("unrar_jni");
    }

    private native boolean open(String filePath);
    private native void close();

    public native boolean isEncrypted();
    public native void setPassword(String password);
    public native String[] getEntries();
    public native byte[] extractEntryData(String entryName);

    private String filePath = null;

    @Override
    public int hashCode() {
        if (StringUtils.isNotBlank(filePath)) {
            return filePath.hashCode();
        }
        return super.hashCode();
    }

    public boolean openRAR(String path) {
        if (open(path)) {
            filePath = path;
            return true;
        }
        return false;
    }

    public void closeRAR() {
        close();
        filePath = null;
    }
}
