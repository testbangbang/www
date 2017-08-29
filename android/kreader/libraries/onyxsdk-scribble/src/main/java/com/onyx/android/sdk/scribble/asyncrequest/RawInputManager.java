package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawErasePointsReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.TouchErasePointsReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.touch.RawInputProcessor;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/8/15.
 */

public class RawInputManager {
    private static final String TAG = RawInputManager.class.getSimpleName();

    private RawInputProcessor rawInputProcessor = null;
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
        getRawInputProcessor().setRawInputCallback(new RawInputProcessor.RawInputCallback() {
            @Override
            public void onBeginRawData() {
                eventBus.post(new BeginRawDataEvent());
            }

            @Override
            public void onRawTouchPointListReceived(final Shape shape, TouchPointList pointList) {
                onNewTouchPointListReceived(pointList);
            }

            @Override
            public void onBeginErasing() {
                eventBus.post(new BeginErasingEvent());
            }

            @Override
            public void onEraseTouchPointListReceived(final TouchPointList pointList) {
                eventBus.post(new ErasingEvent(null, false));
            }

            @Override
            public void onEndRawData() {
            }

            @Override
            public void onEndErasing() {
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
        getRawInputProcessor().start();
    }

    public void resumeRawDrawing() {
        if (!isUseRawInput()) {
            return;
        }

        getRawInputProcessor().resume();
    }

    public void pauseRawDrawing() {
        if (!isUseRawInput()) {
            return;
        }

        getRawInputProcessor().pause();
    }

    public void quitRawDrawing() {
        if (!isUseRawInput()) {
            return;
        }
        getRawInputProcessor().quit();
    }

    public boolean isUseRawInput() {
        return useRawInput;
    }

    public RawInputManager setUseRawInput(boolean use) {
        useRawInput = use;
        return this;
    }

    public RawInputManager setHostView(final View view) {
        getRawInputProcessor().setHostView(view);
        return this;
    }

    public RawInputManager setLimitRect(final View view) {
        Rect limitRect = new Rect();
        view.getLocalVisibleRect(limitRect);
        getRawInputProcessor().setLimitRect(new RectF(limitRect));
        EpdController.setScreenHandWritingRegionLimit(view,
                limitRect.left, limitRect.top, limitRect.right, limitRect.bottom);
        return this;
    }

    private RawInputProcessor getRawInputProcessor() {
        if (rawInputProcessor == null) {
            rawInputProcessor = new RawInputProcessor();
        }
        return rawInputProcessor;
    }

}
