package com.onyx.kreader.common;

import com.onyx.kreader.cache.ReaderBitmapImpl;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class ReaderDrawContext {

    public boolean asyncDraw;
    public ReaderBitmapImpl renderingBitmap;
    public float targetGammaCorrection;

    private ReaderDrawContext() {
        asyncDraw = true;
    }

    public static ReaderDrawContext create(boolean asyncDraw) {
        ReaderDrawContext context = new ReaderDrawContext();
        context.asyncDraw = asyncDraw;
        return context;
    }

    public static ReaderDrawContext copy(ReaderDrawContext context) {
        ReaderDrawContext copy = new ReaderDrawContext();
        copy.asyncDraw = context.asyncDraw;
        copy.renderingBitmap = context.renderingBitmap;
        copy.targetGammaCorrection = context.targetGammaCorrection;
        return copy;
    }

}
