package com.onyx.android.sdk.scribble.api.event;

import android.view.MotionEvent;

import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by solskjaer49 on 2017/8/2 17:56.
 */

public class DrawingTouchMoveEvent {

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }

    public Shape getShape() {
        return shape;
    }

    public boolean isLast() {
        return last;
    }

    private MotionEvent motionEvent;
    private Shape shape;
    private boolean last;

    public DrawingTouchMoveEvent(MotionEvent motionEvent, Shape shape, boolean last) {
        this.motionEvent = motionEvent;
        this.shape = shape;
        this.last = last;
    }
}
