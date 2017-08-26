package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.EraseTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.ErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.touch.RawInputProcessor;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/8/15.
 */

public class RawInputManager {
    private static final String TAG = RawInputManager.class.getSimpleName();

    private RawInputProcessor rawInputProcessor = null;
    private NoteManager noteManager;
    private boolean useRawInput = true;
    private TouchPointList erasePoints;

    public RawInputManager(NoteManager noteManager) {
        this.noteManager = noteManager;
    }

    public void startRawInputProcessor() {
        if (!isUseRawInput()) {
            return;
        }
        getRawInputProcessor().setRawInputCallback(new RawInputProcessor.RawInputCallback() {
            @Override
            public void onBeginRawData() {
                EventBus.getDefault().post(new BeginRawDataEvent());
            }

            @Override
            public void onRawTouchPointListReceived(final Shape shape, TouchPointList pointList) {
                onNewTouchPointListReceived(pointList);
            }

            @Override
            public void onBeginErasing() {
                EventBus.getDefault().post(new BeginErasingEvent());
            }

            @Override
            public void onEraseTouchPointListReceived(final TouchPointList pointList) {
                EventBus.getDefault().post(new ErasingEvent(null, false));
            }

            @Override
            public void onEndRawData() {
            }

            @Override
            public void onEndErasing() {
                EventBus.getDefault().post(new EraseTouchPointListReceivedEvent(erasePoints));
            }
        });
        startRawDrawing();
    }

    private void onNewTouchPointListReceived(final TouchPointList pointList) {
        if (!isUseRawInput()) {
            return;
        }
        Shape shape = createNewShape(noteManager.inSpanScribbleMode(),
                noteManager.getDocumentHelper().getNoteDrawingArgs().getCurrentShapeType());
        shape.addPoints(pointList);
        noteManager.onNewShape(shape);
        EventBus.getDefault().post(new RawTouchPointListReceivedEvent(shape,pointList));
    }

    private Shape createNewShape(boolean isSpanTextMode, int type) {
        Shape shape = ShapeFactory.createShape(type);
        shape.setStrokeWidth(noteManager.getDocumentHelper().getStrokeWidth());
        shape.setColor(noteManager.getDocumentHelper().getStrokeColor());
        shape.setLayoutType(isSpanTextMode ? ShapeFactory.POSITION_LINE_LAYOUT : ShapeFactory.POSITION_FREE);
        return shape;
    }

    private void startRawDrawing() {
        if (!isUseRawInput()) {
            return;
        }
        getRawInputProcessor().start();
        noteManager.getPenManager().startDrawing();
    }

    public void resumeRawDrawing() {
        if (!isUseRawInput()) {
            return;
        }

        getRawInputProcessor().resume();
        noteManager.getPenManager().resumeDrawing();
    }

    public void pauseRawDrawing() {
        if (!isUseRawInput()) {
            return;
        }

        getRawInputProcessor().pause();
        noteManager.getPenManager().pauseDrawing();
    }

    public void quitRawDrawing() {
        if (!isUseRawInput()) {
            return;
        }
        getRawInputProcessor().quit();
        noteManager.getPenManager().quitDrawing();
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
