package com.onyx.android.sdk.scribble.api.event;

import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by solskjaer49 on 2017/8/2 18:13.
 */

public class EndRawErasingEvent {
    private boolean outLimitRegion;
    private TouchPoint point;

    public EndRawErasingEvent(boolean outLimitRegion, TouchPoint point) {
        this.outLimitRegion = outLimitRegion;
        this.point = point;
    }

    public boolean isOutLimitRegion() {
        return outLimitRegion;
    }

    public TouchPoint getPoint() {
        return point;
    }
}
