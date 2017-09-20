package com.onyx.android.sdk.scribble.api.event;

import android.view.MotionEvent;

/**
 * Created by lxm on 2017/8/28.
 */

public class ErasingTouchEvent {

    private MotionEvent motionEvent;

    public ErasingTouchEvent(MotionEvent motionEvent) {
        this.motionEvent = motionEvent;
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }
}
