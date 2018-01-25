package com.onyx.android.sdk.pen;

import android.graphics.Rect;
import android.view.View;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;

import java.util.List;

/**
 * Created by lxm on 2017/8/15.
 */

class RawInputManager {
    private RawInputCallback callback;
    private RawInputReader rawInputReader = null;
    private boolean useRawInput = true;

    public RawInputManager() {
    }

    public void setRawInputCallback(RawInputCallback callback) {
        this.callback = callback;
    }

    public void startRawInputReader() {
        if (!isUseRawInput()) {
            return;
        }
        getRawInputReader().setRawInputCallback(new RawInputCallback() {
            @Override
            public void onBeginRawDrawing(boolean shortcut, TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onBeginRawDrawing(shortcut, point);
                }
            }

            @Override
            public void onEndRawDrawing(final boolean outLimitRegion, TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onEndRawDrawing(outLimitRegion, point);
                }
            }

            @Override
            public void onRawDrawingTouchPointMoveReceived(TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onRawDrawingTouchPointMoveReceived(point);
                }
            }

            @Override
            public void onRawDrawingTouchPointListReceived(TouchPointList pointList) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onRawDrawingTouchPointListReceived(pointList);
                }
            }

            @Override
            public void onBeginRawErasing(boolean shortcut, TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onBeginRawErasing(shortcut, point);
                }
            }

            @Override
            public void onEndRawErasing(final boolean outLimitRegion, TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onEndRawErasing(outLimitRegion, point);
                }
            }

            @Override
            public void onRawErasingTouchPointMoveReceived(TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onRawErasingTouchPointMoveReceived(point);
                }
            }

            @Override
            public void onRawErasingTouchPointListReceived(final TouchPointList pointList) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onRawErasingTouchPointListReceived(pointList);
                }
            }
        });

        getRawInputReader().start();
    }

    public void resumeRawInputReader() {
        if (!isUseRawInput()) {
            return;
        }
        getRawInputReader().resume();
    }

    public void pauseRawInputReader() {
        if (!isUseRawInput()) {
            return;
        }
        getRawInputReader().pause();
    }

    public void quitRawInputReader() {
        if (!isUseRawInput()) {
            return;
        }
        getRawInputReader().quit();
    }

    public boolean isUseRawInput() {
        return useRawInput;
    }

    public RawInputManager setUseRawInput(boolean use) {
        useRawInput = use;
        return this;
    }

    public View getHostView() {
        return getRawInputReader().getHostView();
    }

    public RawInputManager setHostView(final View view) {
        getRawInputReader().setHostView(view);
        Rect limitRect = new Rect();
        view.getLocalVisibleRect(limitRect);
        getRawInputReader().setLimitRect(limitRect);
        return this;
    }

    public RawInputManager setLimitRect(Rect limitRect, List<Rect> excludeRectList) {
        getRawInputReader().setLimitRect(limitRect);
        getRawInputReader().setExcludeRect(excludeRectList);
        return this;
    }

    public RawInputManager setLimitRect(List<Rect> limitRect, List<Rect> excludeRectList) {
        getRawInputReader().setLimitRect(limitRect);
        getRawInputReader().setExcludeRect(excludeRectList);
        return this;
    }

    private RawInputReader getRawInputReader() {
        if (rawInputReader == null) {
            rawInputReader = new RawInputReader();
        }
        return rawInputReader;
    }

    public void setStrokeWidth(float w) {
        getRawInputReader().setStrokeWidth(w);
    }
}
