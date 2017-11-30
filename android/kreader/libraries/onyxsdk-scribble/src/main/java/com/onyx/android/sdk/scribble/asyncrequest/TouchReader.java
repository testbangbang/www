package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/8/15.
 */

public class TouchReader {

    public interface TouchInputCallback {
        void onErasingTouchEvent(MotionEvent event);
        void onDrawingTouchEvent(MotionEvent event);
    }

    private TouchInputCallback callback;
    private List<Rect> limitRectList = new ArrayList();
    private boolean inUserErasing = false;
    private boolean renderByFramework = false;
    private boolean useRawInput = false;
    private boolean supportBigPen = false;
    private boolean enableFingerErasing = false;
    private boolean singleTouch = false;

    public TouchReader() {
    }

    public void setTouchInputCallback(TouchInputCallback callback) {
        this.callback = callback;
    }

    public TouchReader setLimitRect(Rect softwareLimitRect) {
        limitRectList = new ArrayList<>();
        limitRectList.add(softwareLimitRect);
        return this;
    }

    public TouchReader setLimitRect(List<Rect> softwareLimitRectList) {
        limitRectList = new ArrayList<>();
        limitRectList.addAll(softwareLimitRectList);
        return this;
    }

    public boolean isInUserErasing() {
        return inUserErasing;
    }

    public boolean isRenderByFramework() {
        return renderByFramework;
    }

    public void setInUserErasing(boolean inUserErasing) {
        this.inUserErasing = inUserErasing;
    }

    public void setRenderByFramework(boolean renderByFramework) {
        this.renderByFramework = renderByFramework;
    }

    public void processTouchEvent(final MotionEvent motionEvent) {
        if (!checkTouchPoint(new TouchPoint(motionEvent))) {
            return;
        }
        if (motionEvent.getPointerCount() > 1) {
            return;
        }
        int toolType = motionEvent.getToolType(0);
        if (isFingerTouch(toolType) && !isSingleTouch()) {
            return;
        }

        if ((supportBigPen() && toolType == MotionEvent.TOOL_TYPE_ERASER) || isInUserErasing()) {
            if (isFingerTouch(toolType)) {
                if (isEnableFingerErasing()) {
                    if (callback != null) {
                        callback.onErasingTouchEvent(motionEvent);
                    }
                    return;
                }
                return;
            }
            if (callback != null) {
                callback.onErasingTouchEvent(motionEvent);
            }
            return;
        }
        if (!(isUseRawInput() && isRenderByFramework())) {
            if (callback != null) {
                callback.onDrawingTouchEvent(motionEvent);
            }
        }
    }

    public boolean checkTouchPoint(final TouchPoint touchPoint) {
        for (Rect r : limitRectList) {
            if (r.contains((int) touchPoint.x, (int) touchPoint.y)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkTouchPointList(final TouchPointList touchPointList) {
        if (touchPointList == null || touchPointList.size() == 0) {
            return false;
        }
        List<TouchPoint> touchPoints = touchPointList.getPoints();
        for (TouchPoint touchPoint : touchPoints) {
            if (!checkTouchPoint(touchPoint)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkShapesOutOfRange(List<Shape> shapes) {
        if (shapes == null || shapes.size() == 0) {
            return false;
        }
        for (Shape shape : shapes) {
            TouchPointList pointList = shape.getPoints();
            if (!checkTouchPointList(pointList)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFingerTouch(int toolType) {
        return toolType == MotionEvent.TOOL_TYPE_FINGER ||
                toolType == MotionEvent.TOOL_TYPE_UNKNOWN;
    }

    private boolean isSingleTouch() {
        return singleTouch;
    }

    private boolean supportBigPen() {
        return supportBigPen;
    }

    private boolean isEnableFingerErasing() {
        return enableFingerErasing;
    }

    private boolean isUseRawInput() {
        return useRawInput;
    }

    public TouchReader setUseRawInput(boolean use) {
        useRawInput = use;
        return this;
    }

    public TouchReader setSupportBigPen(boolean support) {
        supportBigPen = support;
        return this;
    }

    public TouchReader setSingleTouch(boolean single) {
        singleTouch = single;
        return this;
    }


}
