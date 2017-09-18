package com.onyx.android.sdk.scribble.shape;

import android.graphics.PointF;

/**
 * Created by solskjaer49 on 2017/9/18 18:20.
 * TODO:shape which dose not support DFB, etc. line/triangle,circle,rectangle
 */

public class NonEPDShape extends BaseShape {
    @Override
    public void onRotate(final float angle, PointF pointF) {
        setOrientation((getOrientation() + angle) % 360);
    }
}
