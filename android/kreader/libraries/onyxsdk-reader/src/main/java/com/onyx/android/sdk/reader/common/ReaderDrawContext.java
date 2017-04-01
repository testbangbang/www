package com.onyx.android.sdk.reader.common;

import com.onyx.android.sdk.reader.cache.ReaderBitmapImpl;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class ReaderDrawContext {

    public boolean asyncDraw;
    public ReaderBitmapImpl renderingBitmap;
    public float targetGammaCorrection;
    public float targetTextGammaCorrection;
    public float targetEmboldenLevel;

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
        copy.targetTextGammaCorrection = context.targetTextGammaCorrection;
        copy.targetEmboldenLevel = context.targetEmboldenLevel;
        return copy;
    }

}
