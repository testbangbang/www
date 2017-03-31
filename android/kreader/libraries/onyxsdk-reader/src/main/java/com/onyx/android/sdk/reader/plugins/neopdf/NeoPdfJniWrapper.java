package com.onyx.android.sdk.reader.plugins.neopdf;

import android.graphics.Bitmap;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.api.ReaderSentence;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.reader.api.ReaderTextSplitter;
import com.onyx.android.sdk.reader.host.impl.ReaderTextSplitterImpl;

import java.util.List;

/**
 * Created by zengzhu on 2/3/16.
 * javah -classpath ./bin/classes:/opt/adt-bundle-linux/sdk/platforms/android-8/android.jar:./com/onyx/kreader/plugins/pdfium/ -jni com.onyx.kreader.plugins.pdfium.NeoPdfJniWrapper
 * http://cdn01.foxitsoftware.com/pub/foxit/manual/enu/FoxitPDF_SDK20_Guide.pdf
 * https://src.chromium.org/svn/trunk/src/pdf/pdfium/
 */
public class NeoPdfJniWrapper {

    public static final Class TAG = NeoPdfJniWrapper.class;

    static{
        System.loadLibrary("neo_pdf");
    }

    static public long NO_ERROR = 0;
    static public long ERROR_UNKNOWN = 1;
    static public long ERROR_FILE_NOT_FOUND = 2;
    static public long ERROR_FILE_INVALID = 3;
    static public long ERROR_PASSWORD_INVALID = 4;
    static public long ERROR_SECURITY = 5;
    static public long ERROR_PAGE_NOT_FOUND = 6;

    private static int sPluginId = -1;

    private synchronized static int nextId() {
        sPluginId++;
        return sPluginId;
    }

    public native boolean nativeInitLibrary();
    public native boolean nativeDestroyLibrary();

    private native long nativeOpenDocument(int id, final String path, final String password);
    private native boolean nativeCloseDocument(int id);

    private native int nativeMetadata(int id, final String tag, byte [] data);

    private native int nativePageCount(int id);
    private native boolean nativePageSize(int id, int page, float []size);

    private native boolean nativeRenderPage(int id, int page, int x, int y, int width, int height, int rotation, float gamma, final Bitmap bitmap);

    private native int nativeHitTest(int id, int page, int x, int y, int width, int height, int rotation, int startX, int startY, int endX, int endY, final ReaderTextSplitter splitter, final boolean selectingWord, final NeoPdfSelection selection);

    private native int nativeSelection(int id, int page, int x, int y, int width, int height, int rotation, int startCharIndex, int endCharIndex, final NeoPdfSelection selection);

    private native int nativeSearchInPage(int id, int page, int x, int y, int width, int height, int rotation, final byte [] buffer, boolean caseSensitive, boolean matchWholeWord, int contextLength, final List<ReaderSelection> list);

    private native boolean nativeIsTextPage(int id, int page);

    private native byte [] nativeGetPageText(int id, int page);

    private native ReaderSentence nativeGetSentence(int id, int page, int sentenceStartIndex, final ReaderTextSplitter splitter);

    private native boolean nativeGetTableOfContent(int id, ReaderDocumentTableOfContentEntry root);

    private native boolean nativeGetPageLinks(int id, int page, final List<ReaderSelection> list);

    private int id;
    private String filePath = null;

    public NeoPdfJniWrapper() {
        id = nextId();
    }

    public String getFilePath() {
        return filePath;
    }

    public long openDocument(final String path, final String password) {
        filePath = path;
        return nativeOpenDocument(id, path, password);
    }

    public boolean closeDocument() {
        boolean succ = nativeCloseDocument(id);
        filePath = null;
        return succ;
    }

    public String metadataString(final String tag) {
        byte [] data  = new byte[4096];
        int size = nativeMetadata(id, tag, data);
        return StringUtils.utf16le(data).trim();
    }

    public int pageCount() {
        return nativePageCount(id);
    }
    public boolean pageSize(int page, float []size) {
        return nativePageSize(id, page, size);
    }

    /**
     * // scale can be calculated from: xScale = widthInBitmap / naturalWidth; yScale = heightInBitmap / naturalHeight;
     * // position is xInBitmap, yInBitmap
     * @param page page index
     * @param xInBitmap content x position in bitmap
     * @param yInBitmap content x position in bitmap
     * @param widthInBitmap content rendering width
     * @param heightInBitmap content rendering height
     * @param bitmap target bitmap
     * @return
     */
    public boolean drawPage(int page, int xInBitmap, int yInBitmap, int widthInBitmap, int heightInBitmap, int rotation, float gamma, final Bitmap bitmap) {
        // 150 is reader's default, set it as max gamma as we want get best text gamma effect on PL107 devices
        final int MAX_GAMMA = 150;
        float value = MAX_GAMMA - gamma;
        if (value <= 0) {
            value = 1;
        }
        value = (MAX_GAMMA / 2) / value;
        return nativeRenderPage(id, page, xInBitmap, yInBitmap, widthInBitmap, heightInBitmap, rotation, value, bitmap);
    }

    public int hitTest(int page, int x, int y, int width, int height, int rotation, int startX, int startY, int endX, int endY, final boolean selectingWord, final NeoPdfSelection selection) {
        return nativeHitTest(id, page, x, y, width, height, rotation, startX, startY, endX, endY, ReaderTextSplitterImpl.sharedInstance(), selectingWord, selection);
    }

    public int selection(int page, int x, int y, int width, int height, int rotation, int startCharIndex, int endCharIndex, final NeoPdfSelection selection) {
        return nativeSelection(id, page, x, y, width, height, rotation, startCharIndex, endCharIndex, selection);
    }

    public void searchInPage(int page, int x, int y, int width, int height, int rotation, final String text, boolean caseSensitive, boolean matchWholeWord, int contextLength, final List<ReaderSelection> list) {
        Debug.d(TAG, "searching in page: " + page + ", " + text);
        nativeSearchInPage(id, page, x, y, width, height, rotation, StringUtils.utf16leBuffer(text), caseSensitive, matchWholeWord, contextLength, list);
        Debug.d(TAG, "searched results: " + list.size());
    }

    public boolean isTextPage(int page) {
        return nativeIsTextPage(id, page);
    }

    public String getPageText(int page) {
        byte [] data = nativeGetPageText(id, page);
        if (data == null) {
            return null;
        }
        return StringUtils.utf16le(data);
    }

    public ReaderSentence getSentence(int page, int sentenceStartIndex) {
        return nativeGetSentence(id, page, sentenceStartIndex, ReaderTextSplitterImpl.sharedInstance());
    }

    public boolean getTableOfContent(ReaderDocumentTableOfContentEntry root) {
        return nativeGetTableOfContent(id, root);
    }

    public boolean getPageLinks(int page, final List<ReaderSelection> list) {
        return nativeGetPageLinks(id, page, list);
    }

}
