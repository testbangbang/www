package com.onyx.kreader.plugins.images;

import android.graphics.Bitmap;

/**
 * Created by zhuzeng on 2/18/16.
 * javah -classpath ./bin/classes:/opt/adt-bundle-linux/sdk/platforms/android-8/android.jar:./com/onyx/kreader/plugins/images/ -jni com.onyx.kreader.plugins.images.ImagesJniWrapper
 */
public class ImagesJniWrapper {

    static{
        System.loadLibrary("onyx_images");
    }

    public native boolean nativeClearBitmap(final Bitmap bitmap);
    public native boolean nativePageSize(final String path, float []size);
    public native boolean nativeDrawImage(final String imagePath, int x, int y, int width, int height, int rotation, final Bitmap bitmap);

    public native boolean nativeCloseImage(final String path);
    public native void nativeCloseAll();
}
