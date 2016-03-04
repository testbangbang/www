package com.onyx.kreader.plugins.djvu;

import android.graphics.Bitmap;

/**
 * Created by joy on 3/2/16.
 */
public class DjvuJniWrapper {
    static {
        System.loadLibrary("onyx_djvu");
    }

    private native int nativeOpenFile(String path);
    private native void nativeGotoPage(int pageNum);
    private native void nativeGetPageSize(int pageNum, float[] size);

    private native boolean nativeDrawPage(Bitmap bitmap, float zoom, int pageW, int pageH,
                                          int patchX, int patchY,
                                          int patchW, int patchH);
    private native void nativeClose();

    private String filePath;
    private int pageCount;

    public boolean open(String path) {
        filePath = path;
        pageCount = nativeOpenFile(path);
        return pageCount > 0;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getPageCount() {
        return pageCount;
    }

    public boolean gotoPage(int page) {
        if (page < 0 || page >= pageCount) {
            return false;
        }
        nativeGotoPage(page);
        return true;
    }

    public boolean getPageSize(int page, float[] size) {
        if (page < 0 || page >= pageCount) {
            return false;
        }
        nativeGetPageSize(page, size);
        return true;
    }

    public boolean drawPage(int page, Bitmap bitmap, float zoom, int pageW, int pageH,
                                           int patchX, int patchY,
                                           int patchW, int patchH) {
        if (!gotoPage(page)) {
            return false;
        }
        return nativeDrawPage(bitmap, zoom, pageW, pageH, patchX, patchY, patchW, patchH);
    }

    public void close() {
        nativeClose();
        filePath = null;
        pageCount = 0;
    }

}
