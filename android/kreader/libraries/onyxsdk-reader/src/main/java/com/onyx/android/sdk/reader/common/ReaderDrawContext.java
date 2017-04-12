package com.onyx.android.sdk.reader.common;

import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class ReaderDrawContext {

    public boolean asyncDraw;
    public ReaderBitmapReferenceImpl renderingBitmap;
    public float targetGammaCorrection;
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
        copy.targetEmboldenLevel = context.targetEmboldenLevel;
        return copy;
    }

}
