package com.onyx.android.sdk.scribble.api.event;

import android.view.MotionEvent;

/**
 * Created by solskjaer49 on 2017/8/2 18:17.
 */

public class BeginShapeSelectEvent {
    public BeginShapeSelectEvent(MotionEvent event) {
        motionEvent = event;
    }

    public MotionEvent getMotionEvent() {
        return motionEvent;
    }

    private MotionEvent motionEvent;

}
