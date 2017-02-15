package com.onyx.android.sdk.reader.api;

import android.graphics.Bitmap;
import android.graphics.RectF;

/**
 * Created by joy on 2/13/17.
 */

public interface ReaderImage {
    /**
     * image region on page
     * @return
     */
    RectF getRectangle();

    /**
     * source bitmap
     * @return
     */
    Bitmap getBitmap();
}
