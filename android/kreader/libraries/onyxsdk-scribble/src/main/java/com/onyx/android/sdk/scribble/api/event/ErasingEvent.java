package com.onyx.android.sdk.scribble.api.event;

import android.view.MotionEvent;

import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by solskjaer49 on 2017/8/2 18:13.
 */

public class ErasingEvent {

    public ErasingEvent(TouchPoint touchPoint, boolean showIndicator) {
        this.touchPoint = touchPoint;
        this.showIndicator = showIndicator;
    }

    public TouchPoint getTouchPoint() {
        return touchPoint;
    }

    private TouchPoint touchPoint;

    private boolean showIndicator = false;

    public boolean isShowIndicator() {
        return showIndicator;
    }
}
