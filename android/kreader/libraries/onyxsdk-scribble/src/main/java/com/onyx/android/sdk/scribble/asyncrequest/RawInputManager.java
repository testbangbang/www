package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawErasePointsReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.touch.RawInputReader;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/8/15.
 */

public class RawInputManager {
    private static final String TAG = RawInputManager.class.getSimpleName();

    private RawInputReader rawInputReader = null;
    private boolean useRawInput = true;
    private TouchPointList erasePoints;
    private EventBus eventBus;

    public RawInputManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void startRawInputProcessor() {
        if (!isUseRawInput()) {
            return;
        }
        getRawInputReader().setRawInputCallback(new RawInputReader.RawInputCallback() {
            @Override
            public void onBeginRawData(boolean shortcut, TouchPoint point) {
                eventBus.post(new BeginRawDataEvent());
            }

            @Override
            public void onRawTouchPointMoveReceived(TouchPoint point) {

            }

            @Override
            public void onRawTouchPointListReceived(TouchPointList pointList) {
                onNewTouchPointListReceived(pointList);
            }

            @Override
            public void onBeginErasing(boolean shortcut, TouchPoint point) {
                eventBus.post(new BeginErasingEvent());
            }

            @Override
            public void onEraseTouchPointMoveReceived(TouchPoint point) {

            }

            @Override
            public void onEraseTouchPointListReceived(final TouchPointList pointList) {
                eventBus.post(new ErasingEvent(null, false));
            }

            @Override
            public void onEndRawData(final boolean releaseOutLimitRegion, TouchPoint point) {
            }

            @Override
            public void onEndErasing(final boolean releaseOutLimitRegion, TouchPoint point) {
                eventBus.post(new RawErasePointsReceivedEvent(erasePoints));
            }
        });
        startRawDrawing();
    }

    private void onNewTouchPointListReceived(final TouchPointList pointList) {
        if (!isUseRawInput()) {
            return;
        }
        eventBus.post(new RawTouchPointListReceivedEvent(pointList));
    }

    private void startRawDrawing() {
        if (!isUseRawInput()) {
            return;
        }
        getRawInputReader().start();
    }

    public void resumeRawDrawing() {
        if (!isUseRawInput()) {
            return;
        }

        getRawInputReader().resume();
    }

    public void pauseRawDrawing() {
        if (!isUseRawInput()) {
            return;
        }

        getRawInputReader().pause();
    }

    public void quitRawDrawing() {
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
        getRawInputReader().setLimitRect(new RectF(limitRect));
        EpdController.setScreenHandWritingRegionLimit(view,
                limitRect.left, limitRect.top, limitRect.right, limitRect.bottom);
        return this;
    }

    public RawInputManager setCustomLimitRect(final View view, Rect rect) {
        getRawInputReader().setLimitRect(new RectF(rect.left, rect.top, rect.right, rect.bottom));
        EpdController.setScreenHandWritingRegionLimit(view,
                rect.left, rect.top, rect.right, rect.bottom);
        return this;
    }

    private RawInputReader getRawInputReader() {
        if (rawInputReader == null) {
            rawInputReader = new RawInputReader();
        }
        return rawInputReader;
    }

}
