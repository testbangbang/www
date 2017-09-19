package com.onyx.android.sdk.scribble.data;

import android.graphics.RectF;

/**
 * Created by solskjaer49 on 2017/9/15 18:31.
 */

public class SelectedRectF {
    public float getOrientation() {
        return orientation;
    }

    public SelectedRectF setOrientation(float orientation) {
        this.orientation = orientation;
        return this;
    }

    public RectF getRectF() {
        return rectF;
    }

    public SelectedRectF setRectF(RectF rectF) {
        this.rectF = rectF;
        return this;
    }

    public SelectedRectF(float orientation, RectF rectF) {
        this.orientation = orientation;
        this.rectF = rectF;
    }

    private float orientation = 0f;

    public SelectedRectF(RectF rectF) {
        this(0, rectF);
    }

    private RectF rectF = new RectF();
}
