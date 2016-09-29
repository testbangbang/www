package com.onyx.kreader.note.bridge;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.math.OnyxMatrix;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.utils.DeviceUtils;

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

    public NoteEventProcessorManager(final NoteManager p) {
        noteManager = p;
    }

    public View getView() {
        return view;
    }

    public void start() {
        getRawEventProcessor().start();
    }

    public void stop() {
        if (rawEventProcessor != null) {
            rawEventProcessor.quit();
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

    public void update(final View targetView, final DeviceConfig noteConfig, final Rect visibleDrawRect) {
        detectTouchType();
        view = targetView;
        OnyxMatrix viewMatrix = new OnyxMatrix();
        viewMatrix.postRotate(noteConfig.getViewPostOrientation());
        viewMatrix.postTranslate(noteConfig.getViewPostTx(), noteConfig.getViewPostTy());
        getTouchEventProcessor().update(targetView, getViewToEpdMatrix(noteConfig), visibleDrawRect);
        getRawEventProcessor().update(getTouchToScreenMatrix(noteConfig), getScreenToViewMatrix(noteConfig), visibleDrawRect);
    }

    private void detectTouchType() {
        useRawInput = getNoteManager().getNoteConfig().useRawInput();
        singleTouch = DeviceUtils.detectTouchDeviceCount() <= 1;
    }

    private boolean useNormalTouchEvent() {
        if (useRawInput) {
            return false;
        }
        return singleTouch;
    }

    private OnyxMatrix getViewToEpdMatrix(final DeviceConfig noteConfig) {
        OnyxMatrix viewMatrix = new OnyxMatrix();
        viewMatrix.postRotate(noteConfig.getViewPostOrientation());
        viewMatrix.postTranslate(noteConfig.getViewPostTx(), noteConfig.getViewPostTy());
        return viewMatrix;
    }

    private Matrix getTouchToScreenMatrix(final DeviceConfig noteConfig) {
        final Matrix screenMatrix = new Matrix();
        screenMatrix.preScale(noteConfig.getEpdWidth() / getTouchWidth(noteConfig),
                noteConfig.getEpdHeight() / getTouchHeight(noteConfig));
        return screenMatrix;
    }

    private Matrix getScreenToViewMatrix(final DeviceConfig noteConfig) {
        if (!noteConfig.useRawInput()) {
            return null;
        }

        int viewPosition[] = {0, 0};
        view.getLocationOnScreen(viewPosition);
        final Matrix viewMatrix = new Matrix();
        viewMatrix.postRotate(noteConfig.getEpdPostOrientation());
        viewMatrix.postTranslate(noteConfig.getEpdPostTx() - viewPosition[0], noteConfig.getEpdPostTy() - viewPosition[1]);
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
        if (!getNoteManager().isDFBForCurrentShape() || useNormalTouchEvent()) {
            if (getNoteManager().isEraser()) {
                return onTouchEventErasing(motionEvent);
            }
            return onTouchEventDrawing(motionEvent);
        }
        return false;
    }

    public boolean onTouchEventDrawing(final MotionEvent motionEvent) {
        return getTouchEventProcessor().onTouchEventDrawing(motionEvent);
    }

    public boolean onTouchEventErasing(final MotionEvent motionEvent) {
        return getTouchEventProcessor().onTouchEventErasing(motionEvent);
    }

    public final TouchPoint getEraserPoint() {
        return getTouchEventProcessor().getEraserPoint();
    }

}
