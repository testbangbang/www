package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.onyx.android.sdk.scribble.asyncrequest.event.DrawingTouchEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingTouchEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;

import org.greenrobot.eventbus.EventBus;

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
    private Rect limitRect = new Rect();
    private boolean inUserErasing = false;
    private boolean renderByFramework = false;

    public TouchReader() {
    }

    public void setTouchInputCallback(TouchInputCallback callback) {
        this.callback = callback;
    }

    public TouchReader setLimitRect(Rect softwareLimitRect) {
        this.limitRect = softwareLimitRect;
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
        return limitRect.contains((int) touchPoint.x, (int) touchPoint.y);
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
        return toolType == MotionEvent.TOOL_TYPE_FINGER;
    }

    private boolean isSingleTouch() {
        return getDeviceConfig().isSingleTouch();
    }

    private boolean supportBigPen() {
        return getDeviceConfig().supportBigPen();
    }

    private boolean isEnableFingerErasing() {
        return getDeviceConfig().isEnableFingerErasing();
    }

    private boolean isUseRawInput() {
        return getDeviceConfig().useRawInput();
    }

    private DeviceConfig getDeviceConfig() {
        return ConfigManager.getInstance().getDeviceConfig();
    }

}
