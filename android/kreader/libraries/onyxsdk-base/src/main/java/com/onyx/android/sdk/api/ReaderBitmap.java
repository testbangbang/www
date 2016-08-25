package com.onyx.android.sdk.api;

import android.graphics.Bitmap;

/**
 * Created by zhuzeng on 10/3/15.
 * Defined in host and used by plugin.
 */
public interface ReaderBitmap {

    Bitmap getBitmap();

    float gammaCorrection();

}
