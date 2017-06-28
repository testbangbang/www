package com.onyx.android.sdk.reader.api;

import android.graphics.RectF;

/**
 * Created by joy on 2/13/17.
 */

public interface ReaderRichMedia {

    enum MediaType { Audio }

    MediaType getMediaType();

    /**
     * return name if exists
     * @return
     */
    String getName();

    /**
     * media region on page
     * @return
     */
    RectF getRectangle();

    /**
     * source data
     * @return
     */
    byte[] getData();
}
