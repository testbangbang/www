package com.onyx.kreader.plugins.images;

import android.graphics.Bitmap;
import android.graphics.RectF;

/**
 * Created by joy on 2/23/16.
 */
public interface ImagesWrapper {

    class ImageInformation {
        public float width = 0;
        public float height = 0;

    }

    ImageInformation imageInfo(final String path);
    boolean drawImage(final String imagePath, final float scale, int rotation, final RectF displayRect, final RectF positionRect, final RectF visibleRect, final Bitmap bitmap);

    boolean closeImage(final String path);
    void closeAll();
}
