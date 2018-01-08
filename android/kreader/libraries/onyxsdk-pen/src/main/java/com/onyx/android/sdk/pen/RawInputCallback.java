package com.onyx.android.sdk.pen;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;

/**
 * Created by joy on 1/6/18.
 */

public abstract class RawInputCallback {
    // when received pen down or stylus button
    public abstract void onBeginRawDrawing(boolean shortcutDrawing, TouchPoint point);

    public abstract void onEndRawDrawing(boolean outLimitRegion, TouchPoint point);

    // when pen moving.
    public abstract void onRawDrawingTouchPointMoveReceived(TouchPoint point);

    // when pen released.
    public abstract void onRawDrawingTouchPointListReceived(TouchPointList pointList);

    // caller should render the page here.
    public abstract void onBeginRawErasing(boolean shortcutErasing, TouchPoint point);

    public abstract void onEndRawErasing(boolean outLimitRegion, TouchPoint point);

    // when eraser moving
    public abstract void onRawErasingTouchPointMoveReceived(TouchPoint point);

    // caller should do hit test in current page, remove shapes hit-tested.
    public abstract void onRawErasingTouchPointListReceived(TouchPointList pointList);
}
