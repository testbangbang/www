package com.onyx.android.sdk.scribble.asyncrequest.event;

import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by solskjaer49 on 2017/8/2 17:51.
 */

public class RawTouchPointListReceivedEvent {
    public RawTouchPointListReceivedEvent(Shape shape, TouchPointList touchPointList) {
        this.shape = shape;
        this.touchPointList = touchPointList;
    }

    public Shape getShape() {
        return shape;
    }

    public TouchPointList getTouchPointList() {
        return touchPointList;
    }

    private Shape shape;
    private TouchPointList touchPointList;
}
