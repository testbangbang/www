package com.onyx.android.sdk.scribble.asyncrequest;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginErasingEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.BeginRawDataEvent;
import com.onyx.android.sdk.scribble.asyncrequest.event.RawTouchPointListReceivedEvent;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.touch.RawInputProcessor;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/8/15.
 */

public class RawInputReader {

    private RawInputProcessor rawInputProcessor = null;
    private NoteManager noteManager;

    public RawInputReader(NoteManager noteManager) {
        this.noteManager = noteManager;
    }

    public void startRawInputProcessor() {
        if (!useRawInput()) {
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
            }

            @Override
            public void onEndRawData() {
            }

            @Override
            public void onEndErasing() {
            }
        });
        startRawDrawing();
    }

    private void onNewTouchPointListReceived(final TouchPointList pointList) {
        if (!useRawInput()) {
            return;
        }
        Shape shape = createNewShape(noteManager.inSpanScribbleMode(), noteManager.getDocumentHelper().getNoteDrawingArgs().getCurrentShapeType());
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
        if (!useRawInput()) {
            return;
        }
        getRawInputProcessor().start();
        noteManager.getPenManager().startDrawing();
    }

    public void resumeRawDrawing() {
        noteManager.getDocumentHelper().setPenState(NoteDrawingArgs.PenState.PEN_SCREEN_DRAWING);
        if (!useRawInput()) {
            return;
        }

        getRawInputProcessor().resume();
        noteManager.getPenManager().resumeDrawing();
    }

    public void pauseRawDrawing() {
        if (!useRawInput()) {
            return;
        }

        getRawInputProcessor().pause();
        noteManager.getPenManager().pauseDrawing();
    }

    public void quitRawDrawing() {
        if (!useRawInput()) {
            return;
        }
        getRawInputProcessor().quit();
        noteManager.getPenManager().quitDrawing();
    }

    private DeviceConfig getDeviceConfig() {
        return noteManager.getDeviceConfig();
    }

    private boolean useRawInput() {
        return getDeviceConfig() != null && getDeviceConfig().useRawInput();
    }

    public final RawInputProcessor getRawInputProcessor() {
        if (rawInputProcessor == null) {
            rawInputProcessor = new RawInputProcessor();
        }
        return rawInputProcessor;
    }

}
