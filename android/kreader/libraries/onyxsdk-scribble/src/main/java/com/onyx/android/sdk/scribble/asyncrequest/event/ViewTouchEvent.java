package com.onyx.android.sdk.scribble.asyncrequest.event;

import android.view.MotionEvent;

/**
 * Created by lxm on 2017/8/28.
 */

public class ViewTouchEvent {

    private MotionEvent motionEvent;

    public ViewTouchEvent(MotionEvent motionEvent) {
        this.motionEvent = motionEvent;
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }
}
