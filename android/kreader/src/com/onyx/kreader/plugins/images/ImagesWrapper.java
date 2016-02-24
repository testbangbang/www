package com.onyx.kreader.plugins.images;

import android.graphics.Bitmap;

/**
 * Created by joy on 2/23/16.
 */
public interface ImagesWrapper {
    boolean clearBitmap(final Bitmap bitmap);
    boolean pageSize(final String path, float []size);
    boolean drawImage(final String imagePath, int x, int y, int width, int height, int rotation, final Bitmap bitmap);

    boolean closeImage(final String path);
    void closeAll();
}
