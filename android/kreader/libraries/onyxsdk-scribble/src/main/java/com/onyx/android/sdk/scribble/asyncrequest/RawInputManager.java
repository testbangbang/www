package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.view.View;

import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.touch.RawInputReader;


import java.util.List;

/**
 * Created by lxm on 2017/8/15.
 */

public class RawInputManager {
    private RawInputReader.RawInputCallback callback;
    private RawInputReader rawInputReader = null;
    private boolean useRawInput = true;

    public RawInputManager() {
    }

    public void setRawInputCallback(RawInputReader.RawInputCallback callback) {
        this.callback = callback;
    }

    public void startRawInputReader() {
        if (!isUseRawInput()) {
            return;
        }
        getRawInputReader().setRawInputCallback(new RawInputReader.RawInputCallback() {
            @Override
            public void onBeginRawData(boolean shortcut, TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onBeginRawData(shortcut, point);
                }
            }

            @Override
            public void onEndRawData(final boolean outLimitRegion, TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onEndRawData(outLimitRegion, point);
                }
            }

            @Override
            public void onRawTouchPointMoveReceived(TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onRawTouchPointMoveReceived(point);
                }
            }

            @Override
            public void onRawTouchPointListReceived(TouchPointList pointList) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onRawTouchPointListReceived(pointList);
                }
            }

            @Override
            public void onBeginErasing(boolean shortcut, TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onBeginErasing(shortcut, point);
                }
            }

            @Override
            public void onEndErasing(final boolean outLimitRegion, TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onEndErasing(outLimitRegion, point);
                }
            }

            @Override
            public void onEraseTouchPointMoveReceived(TouchPoint point) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onEraseTouchPointMoveReceived(point);
                }
            }

            @Override
            public void onEraseTouchPointListReceived(final TouchPointList pointList) {
                if (!isUseRawInput()) {
                    return;
                }
                if (callback != null) {
                    callback.onEraseTouchPointListReceived(pointList);
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

    public RawInputManager setHostView(final View view) {
        getRawInputReader().setHostView(view);
        return this;
    }

    public RawInputManager setLimitRect(final View view) {
        Rect limitRect = new Rect();
        view.getLocalVisibleRect(limitRect);
        getRawInputReader().setLimitRect(limitRect);
        return this;
    }

    public RawInputManager setCustomLimitRect(Rect limitRect, List<Rect> excludeRectList) {
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
