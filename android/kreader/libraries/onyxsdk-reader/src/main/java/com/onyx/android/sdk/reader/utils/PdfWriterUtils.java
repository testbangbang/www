package com.onyx.android.sdk.reader.utils;

/**
 * Created by joy on 10/11/16.
 */
public class PdfWriterUtils {

    static {
        System.loadLibrary("neo_pdf");
    }

    public static native boolean openExistingDocument(String path);
    public static native boolean createNewDocument();
    public static native boolean writeHighlight(int page, String note, float[] quadPoints);
    public static native boolean writeLine(int page, float[] boundingRect, int color, float strokeWidth, float startX, float startY, float endX, float endY);
    public static native boolean writePolyLine(int page, float[] boundingRect, int color, float strokeWidth, float[] vertices);
    public static native boolean writePolygon(int page, float[] boundingRect, int color, float strokeWidth, float[] vertices);
    public static native boolean writeSquare(int page, float[] boundingRect, int color, float strokeWidth);
    public static native boolean writeCircle(int page, float[] rect, int color, float strokeWidth);
    public static native boolean saveAs(String path, boolean savePagesWithAnnotation);
    public static native void close();

    public static native void setDocumentTitle(String path, String title);
}
