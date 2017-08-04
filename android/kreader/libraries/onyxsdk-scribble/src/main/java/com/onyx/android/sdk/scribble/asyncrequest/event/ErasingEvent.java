package com.onyx.android.sdk.scribble.asyncrequest.event;

import android.view.MotionEvent;

/**
 * Created by solskjaer49 on 2017/8/2 18:13.
 */

public class ErasingEvent {
    public ErasingEvent(MotionEvent motionEvent) {
        this.motionEvent = motionEvent;
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }

    private MotionEvent motionEvent;

}
