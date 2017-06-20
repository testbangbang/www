package com.onyx.android.sdk.reader.plugins.djvu;

import android.graphics.Bitmap;
import com.onyx.android.sdk.reader.api.ReaderSelection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joy on 3/2/16.
 */
public class DjvuJniWrapper {
    static {
        System.loadLibrary("neo_djvu");
    }

    private static int sPluginId = -1;

    private synchronized static int nextId() {
        sPluginId++;
        return sPluginId;
    }

    private native int nativeOpenFile(int id, String path);
    private native boolean nativeGotoPage(int id, int pageNum);
    private native boolean nativeGetPageSize(int id, int pageNum, float[] size);
    private native boolean nativeExtractPageText(int id, int pageNum, List<ReaderSelection> textChunks);

    private native boolean nativeDrawPage(int id, int pageNum, Bitmap bitmap, float zoom, int pageW, int pageH,
                                          int patchX, int patchY,
                                          int patchW, int patchH);
    private native void nativeClose(int id);

    private int id;
    private String filePath;
    private int pageCount;
    private HashMap<Integer, List<ReaderSelection>> pageTextChunks = new HashMap<Integer, List<ReaderSelection>>();

    public DjvuJniWrapper() {
        id = nextId();
    }

    public boolean open(String path) {
        cleanup();
        filePath = path;
        pageCount = nativeOpenFile(id, path);
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
        return nativeGotoPage(id, page);
    }

    public boolean getPageSize(int page, float[] size) {
        if (!isValidPage(page)) {
            return false;
        }
        return nativeGetPageSize(id, page, size);
    }

    public boolean extractPageText(int page, List<ReaderSelection> textChunks) {
        if (!isValidPage(page)) {
            return false;
        }
        if (!pageTextChunks.containsKey(page)) {
            if (!nativeExtractPageText(id, page, textChunks)) {
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
        if (!isValidPage(page)) {
            return false;
        }
        return nativeDrawPage(id, page, bitmap, zoom, pageW, pageH, patchX, patchY, patchW, patchH);
    }

    public void close() {
        nativeClose(id);
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
