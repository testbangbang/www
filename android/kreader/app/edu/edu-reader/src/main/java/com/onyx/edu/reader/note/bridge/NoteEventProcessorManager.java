package com.onyx.edu.reader.note.bridge;

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
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.android.sdk.utils.DeviceUtils;

import java.util.List;

/**
 * Created by zhuzeng on 9/18/16.
 * Receive events from touch event or input event reader and send renderNoteShapes command to screen
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
                       final DeviceConfig noteConfig,
                       final MappingConfig mappingConfig,
                       final Rect visibleDrawRect,
                       final List<RectF> excludeRect,
                       int orientation) {
        detectTouchType();
        view = targetView;
        getTouchEventProcessor().update(targetView, getViewToEpdMatrix(mappingConfig, orientation), visibleDrawRect, excludeRect);
        getRawEventProcessor().update(getTouchToScreenMatrix(noteConfig, orientation),
                getScreenToViewMatrix(noteConfig, mappingConfig, orientation),
                visibleDrawRect, excludeRect);
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

    private OnyxMatrix getViewToEpdMatrix(final MappingConfig mappingConfig, int orientation) {
        final MappingConfig.MappingEntry entry = mappingConfig.getEntry(orientation);
        OnyxMatrix viewMatrix = new OnyxMatrix();
        if (entry != null) {
            viewMatrix.postRotate(entry.orientation);
            viewMatrix.postTranslate(entry.tx, entry.ty);
        }
        return viewMatrix;
    }

    private Matrix getTouchToScreenMatrix(final DeviceConfig noteConfig, int orientation) {
        final Matrix screenMatrix = new Matrix();
        screenMatrix.preScale(noteConfig.getEpdWidth() / getTouchWidth(noteConfig),
                noteConfig.getEpdHeight() / getTouchHeight(noteConfig));
        return screenMatrix;
    }

    public boolean inScribbleRect(TouchPoint point) {
        return getTouchEventProcessor().inScribbleRect(point);
    }

    private Matrix getScreenToViewMatrix(final DeviceConfig noteConfig, final MappingConfig mappingConfig, int orientation) {
        if (!noteConfig.useRawInput()) {
            return null;
        }

        int viewPosition[] = {0, 0};
        view.getLocationOnScreen(viewPosition);
        final Matrix viewMatrix = new Matrix();
        final MappingConfig.MappingEntry entry = mappingConfig.getEntry(orientation);
        viewMatrix.postRotate(entry.epd);
        viewMatrix.postTranslate(entry.etx - viewPosition[0], entry.ety - viewPosition[1]);
        return viewMatrix;
    }

    private float getTouchWidth(final DeviceConfig noteConfig) {
        float value = Device.currentDevice().getTouchWidth();
        if (value <= 0) {
            return noteConfig.getTouchWidth();
        }
        return value;
    }

    private float getTouchHeight(final DeviceConfig noteConfig) {
        float value = Device.currentDevice().getTouchHeight();
        if (value <= 0) {
            return noteConfig.getTouchHeight();
        }
        return value;
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
