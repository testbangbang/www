package com.onyx.android.sdk.scribble.api.event;

import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by solskjaer49 on 2017/8/2 18:16.
 */

public class RawErasePointMoveReceivedEvent {
    private TouchPoint touchPoint;

    public RawErasePointMoveReceivedEvent(TouchPoint touchPoint) {
        this.touchPoint = touchPoint;
    }

    public TouchPoint getTouchPoint() {
        return touchPoint;
    }
}
