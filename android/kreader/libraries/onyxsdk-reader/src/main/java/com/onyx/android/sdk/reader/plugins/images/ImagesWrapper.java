package com.onyx.android.sdk.reader.plugins.images;

import android.graphics.Bitmap;
import android.graphics.RectF;
import com.onyx.android.sdk.data.Size;

/**
 * Created by joy on 2/23/16.
 */
public interface ImagesWrapper {

    class ImageInformation {
        public Size size;

        public ImageInformation() {
            size = new Size();
        }

        public Size getSize() {
            return size;
        }

        public int getWidth() {
            return size.width;
        }

        public int getHeight() {
            return size.height;
        }
    }


    ImageInformation imageInfo(final String path);
    boolean drawImage(final String imagePath, final float scale, int rotation, final RectF displayRect, final RectF positionRect, final RectF visibleRect, final Bitmap bitmap);

    boolean closeImage(final String path);
    void closeAll();
}
