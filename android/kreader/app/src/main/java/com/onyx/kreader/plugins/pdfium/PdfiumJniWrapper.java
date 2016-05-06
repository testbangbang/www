package com.onyx.kreader.plugins.pdfium;

import android.graphics.Bitmap;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.utils.StringUtils;

import java.util.List;

/**
 * Created by zengzhu on 2/3/16.
 * javah -classpath ./bin/classes:/opt/adt-bundle-linux/sdk/platforms/android-8/android.jar:./com/onyx/kreader/plugins/pdfium/ -jni com.onyx.kreader.plugins.pdfium.PdfiumJniWrapper
 * http://cdn01.foxitsoftware.com/pub/foxit/manual/enu/FoxitPDF_SDK20_Guide.pdf
 * https://src.chromium.org/svn/trunk/src/pdf/pdfium/
 */
public class PdfiumJniWrapper {

    static{
        System.loadLibrary("onyx_pdfium");
    }

    static public long NO_ERROR = 0;
    static public long ERROR_UNKNOWN = 1;
    static public long ERROR_FILE_NOT_FOUND = 2;
    static public long ERROR_FILE_INVALID = 3;
    static public long ERROR_PASSWORD_INVALID = 4;
    static public long ERROR_SECURITY = 5;
    static public long ERROR_PAGE_NOT_FOUND = 6;

    public native boolean nativeInitLibrary();
    public native boolean nativeDestroyLibrary();
    public native long nativeOpenDocument(final String path, final String password);
    public native boolean nativeCloseDocument();

    public native int nativeMetadata(final String tag, byte [] data);

    public native int nativePageCount();
    public native boolean nativePageSize(int page, float []size);

    private native boolean nativeRenderPage(int page, int x, int y, int width, int height, int rotation, final Bitmap bitmap);

    public native int nativeHitTest(int page, int x, int y, int width, int height, int rotation, int startX, int startY, int endX, int endY, final PdfiumSelection selection);

    public native int nativeSelection(int page, int x, int y, int width, int height, int rotation, int startCharIndex, int endCharIndex, final PdfiumSelection selection);

    public native int nativeSearchInPage(int page, int x, int y, int width, int height, int rotation, final byte [] buffer, boolean caseSensitive, boolean matchWholeWord, final List<ReaderSelection> list);

    public native byte [] nativeGetPageText(int page);

    private String filePath = null;

    public String getFilePath() {
        return filePath;
    }

    @Override
    public int hashCode() {
        if (StringUtils.isNotBlank(filePath)) {
            return filePath.hashCode();
        }
        return super.hashCode();
    }

    public boolean openDocument(final String path, final String password) {
        filePath = path;
        return nativeOpenDocument(path, password) == 0;
    }

    public void closeDocument() {
        nativeCloseDocument();
        filePath = null;
    }

    public String metadataString(final String tag) {
        byte [] data  = new byte[4096];
        int size = nativeMetadata(tag, data);
        return StringUtils.utf16le(data);
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
    public boolean drawPage(int page, int xInBitmap, int yInBitmap, int widthInBitmap, int heightInBitmap, int rotation, final Bitmap bitmap) {
        return nativeRenderPage(page, xInBitmap, yInBitmap, widthInBitmap, heightInBitmap,  rotation, bitmap);
    }

    public void searchInPage(int page, int x, int y, int width, int height, int rotation, final String text, boolean caseSensitive, boolean matchWholeWord, final List<ReaderSelection> list) {
        nativeSearchInPage(page, x, y, width, height, rotation, StringUtils.utf16leBuffer(text), caseSensitive, matchWholeWord, list);
    }

    public String getPageText(int page) {
        byte [] data = nativeGetPageText(page);
        if (data == null) {
            return null;
        }
        return StringUtils.utf16le(data);
    }

}
