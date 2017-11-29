package com.onyx.android.sdk.scribble.api.event;

import com.onyx.android.sdk.scribble.data.TouchPointList;

/**
 * Created by solskjaer49 on 2017/8/2 17:51.
 */

public class RawTouchPointListReceivedEvent {
    public RawTouchPointListReceivedEvent(TouchPointList touchPointList) {
        this.touchPointList = touchPointList;
    }

    public TouchPointList getTouchPointList() {
        return touchPointList;
    }

    private TouchPointList touchPointList;
}
