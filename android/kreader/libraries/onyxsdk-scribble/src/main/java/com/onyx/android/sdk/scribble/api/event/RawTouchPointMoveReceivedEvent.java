package com.onyx.android.sdk.scribble.api.event;

import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by solskjaer49 on 2017/8/2 17:51.
 */

public class RawTouchPointMoveReceivedEvent {
    private TouchPoint touchPoint;

    public RawTouchPointMoveReceivedEvent(TouchPoint touchPoint) {
        this.touchPoint = touchPoint;
    }

    public TouchPoint getTouchPoint() {
        return touchPoint;
    }
}
