package com.onyx.kreader.ui.events;

import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by zhuzeng on 9/29/16.
 */

public class ShapeErasingEvent {

    private boolean finished;
    private TouchPointList touchPointList;
    public ShapeErasingEvent(boolean f, final TouchPointList list) {
        finished = f;
        touchPointList = list;
    }

    public boolean isFinished() {
        return finished;
    }

    public final TouchPointList getTouchPointList() {
        return touchPointList;
    }
}
