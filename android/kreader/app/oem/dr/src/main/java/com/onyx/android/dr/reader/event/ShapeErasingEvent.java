package com.onyx.android.dr.reader.event;

import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by zhuzeng on 9/29/16.
 */

/**
 * Triggered by touch panel in erasing model.
 */
public class ShapeErasingEvent {

    private boolean start;
    private boolean finished;
    private TouchPointList touchPointList;
    public ShapeErasingEvent(boolean s, boolean f, final TouchPointList list) {
        start = s;
        finished = f;
        touchPointList = list;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isFinished() {
        return finished;
    }

    public final TouchPointList getTouchPointList() {
        return touchPointList;
    }
}
