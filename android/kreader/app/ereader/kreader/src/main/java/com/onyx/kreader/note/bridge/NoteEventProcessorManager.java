package com.onyx.kreader.note.bridge;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.math.OnyxMatrix;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.scribble.utils.MappingConfig;
import com.onyx.kreader.note.NoteManager;
import com.onyx.android.sdk.utils.DeviceUtils;

import java.util.List;

/**
 * Created by zhuzeng on 9/18/16.
 * Receive events from touch event or input event reader and send render command to screen
 */
public class NoteEventProcessorManager {

    private volatile View view;
    private TouchEventProcessor touchEventProcessor;
    private RawEventProcessor rawEventProcessor;
    private NoteManager noteManager;
    private boolean useRawInput = false;
    private boolean singleTouch = false;
    private boolean supportBigPen = false;

    public NoteEventProcessorManager(final NoteManager p) {
        noteManager = p;
    }

    public View getView() {
        return view;
    }

    public void start() {
        if (!useRawInput) {
            return;
        }
        getRawEventProcessor().start();
    }

    public void stop() {
        if (rawEventProcessor != null) {
            rawEventProcessor.quit();
            rawEventProcessor = null;

        }
    }

    public void pause() {
        getRawEventProcessor().pause();
    }

    public void resume() {
        getRawEventProcessor().resume();
    }

    public final NoteManager getNoteManager() {
        return noteManager;
    }

    public void update(final View targetView,
                       final Rect visibleDrawRect,
                       final List<RectF> excludeRect) {
        detectTouchType();
        view = targetView;
        getTouchEventProcessor().update(targetView, visibleDrawRect, excludeRect);
        getRawEventProcessor().update(targetView, visibleDrawRect, excludeRect);
    }

    private void detectTouchType() {
        useRawInput = getNoteManager().getNoteConfig().useRawInput();
        singleTouch = DeviceUtils.detectTouchDeviceCount() <= 1 || getNoteManager().getNoteConfig().isSingleTouch();
        supportBigPen = getNoteManager().getNoteConfig().supportBigPen();
    }

    private boolean useNormalTouchEvent() {
        if (useRawInput) {
            return false;
        }
        return singleTouch;
    }

    private TouchEventProcessor getTouchEventProcessor() {
        if (touchEventProcessor == null) {
            touchEventProcessor = new TouchEventProcessor(noteManager);
        }
        return touchEventProcessor;
    }

    private RawEventProcessor getRawEventProcessor() {
        if (rawEventProcessor == null) {
            rawEventProcessor = new RawEventProcessor(noteManager);
        }
        return rawEventProcessor;
    }

    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (getNoteManager().isInSelection()) {
            return onTouchSelecting(motionEvent);
        } else if (getNoteManager().isEraser() || isEraserPressed(motionEvent)) {
            return onTouchEventErasing(motionEvent);
        } else if (!getNoteManager().isDFBForCurrentShape() || useNormalTouchEvent()) {
            return onTouchEventDrawing(motionEvent);
        }
        return false;
    }

    private boolean isEraserPressed(final MotionEvent motionEvent) {
        if (!supportBigPen) {
            return false;
        }
        int toolType = motionEvent.getToolType(0);
        if (toolType == MotionEvent.TOOL_TYPE_ERASER) {
            return true;
        }
        return false;
    }

    public boolean onTouchEventDrawing(final MotionEvent motionEvent) {
        return getTouchEventProcessor().onTouchEventDrawing(motionEvent);
    }

    public boolean onTouchEventErasing(final MotionEvent motionEvent) {
        return getTouchEventProcessor().onTouchEventErasing(motionEvent);
    }

    public boolean onTouchSelecting(final MotionEvent motionEvent) {
        return getTouchEventProcessor().onTouchEventSelecting(motionEvent);
    }

    public final TouchPoint getEraserPoint() {
        return getTouchEventProcessor().getEraserPoint();
    }

}
