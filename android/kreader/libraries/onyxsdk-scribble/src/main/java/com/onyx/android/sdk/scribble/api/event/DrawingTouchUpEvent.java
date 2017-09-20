package com.onyx.android.sdk.scribble.api.event;

import android.view.MotionEvent;

import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by solskjaer49 on 2017/8/2 17:58.
 */

public class DrawingTouchUpEvent {
    public DrawingTouchUpEvent(MotionEvent motionEvent, Shape shape) {
        this.motionEvent = motionEvent;
        this.shape = shape;
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }

    public Shape getShape() {
        return shape;
    }

    private MotionEvent motionEvent;
    private Shape shape;
}
