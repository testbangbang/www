package com.onyx.zip;

import com.neverland.engbook.forpublic.TAL_RESULT;

/**
 * Created by wangxu on 17-6-3.
 */

public class ZipDecryption {

    private String password;

    public ZipDecryption(final String pass) {
        password = pass;
    }

    static {
        System.loadLibrary("onyxzip");
    }

    public native boolean init(String path, String password);
    public native boolean open();
    public native int size();
    public native int seekPos(int offset);
    public native int readContent(byte[] buffer, int bufOffset, int length);
    public native void closeZip();

    public void init(final String fileName) {
        init(fileName, password);
    }

    public int openFile() {
        if (open()) {
            return TAL_RESULT.OK;
        }
        return TAL_RESULT.ERROR;
    }

    public int getSize() {
        return size();
    }

    public int seek(final int pos) {
        return seekPos(pos);
    }

    public int read(byte[] buffer, int bufOffset, int length) {
        return readContent(buffer, bufOffset, length);
    }

    public void close() {
        closeZip();
    }
}

