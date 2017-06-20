package com.onyx.android.sdk.reader.plugins.images;

import android.graphics.Bitmap;
import android.graphics.RectF;

/**
 * Created by zhuzeng on 2/18/16.
 * javah -classpath ./bin/classes:/opt/adt-bundle-linux/sdk/platforms/android-8/android.jar:./com/onyx/kreader/plugins/images/ -jni ImagesJniWrapper
 */
public class ImagesJniWrapper implements ImagesWrapper {

    static{
        System.loadLibrary("neo_images");
    }

    public native boolean nativeClearBitmap(final Bitmap bitmap);
    public native boolean nativePageSize(final String path, float []size);
    public native boolean nativeDrawImage(final String imagePath, int x, int y, int width, int height, int rotation, final Bitmap bitmap);

    public native boolean nativeCloseImage(final String path);
    public native void nativeCloseAll();


    public ImageInformation imageInfo(final String path) {
        return null;
    }

    public boolean drawImage(final String imagePath, final float scale, int rotation, final RectF displayRect, final RectF positionRect, final RectF visibleRect, final Bitmap bitmap) {
        return nativeDrawImage(imagePath, (int)displayRect.left, (int)displayRect.top,
                (int)displayRect.width(), (int)displayRect.height(), rotation, bitmap);

}

    public boolean closeImage(final String path) {
        return nativeCloseImage(path);
    }

    public void closeAll() {
        nativeCloseAll();
    }
}
