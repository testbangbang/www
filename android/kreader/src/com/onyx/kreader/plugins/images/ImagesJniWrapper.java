package com.onyx.kreader.plugins.images;

import android.graphics.Bitmap;

/**
 * Created by zhuzeng on 2/18/16.
 */
public class ImagesJniWrapper {

    static{
        System.loadLibrary("onyx_images");
    }

    public native boolean nativeClearBitmap(final Bitmap bitmap);
    public native boolean nativePageSize(final String path, float []size);
}
