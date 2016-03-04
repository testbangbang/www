package com.onyx.kreader.plugins.djvu;

import android.graphics.Bitmap;

import com.onyx.kreader.api.ReaderSelection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private native boolean nativeExtractPageText(int pageNum, List<ReaderSelection> textChunks);

    private native boolean nativeDrawPage(Bitmap bitmap, float zoom, int pageW, int pageH,
                                          int patchX, int patchY,
                                          int patchW, int patchH);
    private native void nativeClose();

    private String filePath;
    private int pageCount;
    private HashMap<Integer, List<ReaderSelection>> pageTextChunks = new HashMap<Integer, List<ReaderSelection>>();

    public boolean open(String path) {
        cleanup();
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
        if (!isValidPage(page)) {
            return false;
        }
        nativeGotoPage(page);
        return true;
    }

    public boolean getPageSize(int page, float[] size) {
        if (!isValidPage(page)) {
            return false;
        }
        nativeGetPageSize(page, size);
        return true;
    }

    public boolean extractPageText(int page, List<ReaderSelection> textChunks) {
        if (!isValidPage(page)) {
            return false;
        }
        if (!pageTextChunks.containsKey(page)) {
            if (!nativeExtractPageText(page, textChunks)) {
                return false;
            }
            pageTextChunks.put(page, new ArrayList<ReaderSelection>(textChunks));
        }
        textChunks.addAll(pageTextChunks.get(page));
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
        cleanup();
    }

    public boolean searchInPage(int page, final String pattern, boolean caseSensitive, boolean matchWholeWord, final List<ReaderSelection> result) {
        if (!isValidPage(page)) {
            return false;
        }

        ArrayList<ReaderSelection> textChunks = new ArrayList<ReaderSelection>();
        if (!extractPageText(page, textChunks)) {
            return false;
        }

        // TODO word-based search first, we can improve it latter to work for word phrases and sentences
        for (ReaderSelection chunk : textChunks) {
            boolean matched = false;
            if (matchWholeWord) {
                if (caseSensitive) {
                    matched = chunk.getText().equals(pattern);
                } else {
                    matched = chunk.getText().equalsIgnoreCase(pattern);
                }
            } else {
                if (caseSensitive) {
                    matched = chunk.getText().indexOf(pattern) > 0;
                } else {
                    matched = chunk.getText().toLowerCase().indexOf(pattern.toLowerCase()) > 0;
                }
            }
            if (matched) {
                result.add(chunk);
            }
        }

        return true;
    }

    private boolean isValidPage(int page) {
        return page >= 0 && page < pageCount;
    }

    private void cleanup() {
        filePath = null;
        pageCount = 0;
        pageTextChunks.clear();
    }

}
