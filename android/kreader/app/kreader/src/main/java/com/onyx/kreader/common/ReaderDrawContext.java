package com.onyx.kreader.common;

import com.onyx.kreader.cache.ReaderBitmapImpl;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class ReaderDrawContext {

    public boolean asyncDraw;
    public ReaderBitmapImpl renderingBitmap;
    public float targetGammaCorrection;

    public ReaderDrawContext() {
        asyncDraw = true;
    }

}
