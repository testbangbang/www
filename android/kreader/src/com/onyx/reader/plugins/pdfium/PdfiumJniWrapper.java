package com.onyx.reader.plugins.pdfium;

/**
 * Created by zengzhu on 2/3/16.
 * javah -classpath ./:/opt/adt-bundle-linux/sdk/platforms/android-8/android.jar -jni com.onyx.reader.plugins.pdfium.PdfiumJniWrapper
 */
public class PdfiumJniWrapper {

    static{
        System.loadLibrary("onyx_pdfium");
    }

    public native boolean nativeInitLibrary();
    public native long nativeOpenDocument(final String path, final String password);

}
