package com.onyx.reader.plugins.pdfium;

import android.graphics.Bitmap;

/**
 * Created by zengzhu on 2/3/16.
 * javah -classpath ./:/opt/adt-bundle-linux/sdk/platforms/android-15/android.jar -jni com.onyx.reader.plugins.pdfium.PdfiumJniWrapper
 * http://cdn01.foxitsoftware.com/pub/foxit/manual/enu/FoxitPDF_SDK20_Guide.pdf
 */
public class PdfiumJniWrapper {

    static{
        System.loadLibrary("onyx_pdfium");
    }

    public native boolean nativeInitLibrary();
    public native boolean nativeDestroyLibrary();
    public native long nativeOpenDocument(final String path, final String password);
    public native boolean nativeCloseDocument();

    public native int nativePageCount();
    public native boolean nativePageSize(int page, float []size);

    public native boolean nativeRenderPage(int page, final Bitmap bitmap);


}
