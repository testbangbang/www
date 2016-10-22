package com.onyx.kreader.utils;

/**
 * Created by joy on 10/11/16.
 */
public class PdfWriterUtils {

    static {
        System.loadLibrary("onyx_pdfwriter");
    }

    public static native boolean openExistingDocument(String path);
    public static native boolean createNewDocument();
    public static native boolean writeHighlight(int page, String note, float[] quadPoints);
    public static native boolean writePolyLine(int page, float[] boundingRect, int color, float strokeWidth, float[] vertices);
    public static native boolean saveAs(String path, boolean savePagesWithAnnotation);
    public static native void close();
}
