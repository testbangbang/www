package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;

/**
 * Created by lxm on 2017/8/15.
 */

public class TouchHelper {
    private static final String TAG = TouchHelper.class.getSimpleName();
    private NoteManager noteManager;

    private EpdPenManager epdPenManager;
    private TouchReader touchReader;
    private RawInputManager rawInputManager;

    public TouchHelper(NoteManager parent) {
        this.noteManager = parent;
    }

    public EpdPenManager getEpdPenManager() {
        if (epdPenManager == null) {
            epdPenManager = new EpdPenManager();
        }
        return epdPenManager;
    }

    public void setup(View view) {
        setupTouchReader(view);
        setupRawInputManager(view);
        setupEpdPenManager(view);
    }

    private void setupEpdPenManager(final View view) {
        getEpdPenManager().setHostView(view);
        getEpdPenManager().startDrawing();
    }

    private void setupTouchReader(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return getTouchReader().processTouchEvent(motionEvent);
            }
        });

        Rect limitRect = new Rect();
        view.getLocalVisibleRect(limitRect);

        final DeviceConfig deviceConfig = getDeviceConfig();
        getTouchReader()
                .setLimitRect(limitRect)
                .useBigPen(deviceConfig.supportBigPen())
                .useRawInput(deviceConfig.useRawInput())
                .setSingleTouch(deviceConfig.isSingleTouch())
                .enableFingerErasing(deviceConfig.isEnableFingerErasing());
    }

    private void setupRawInputManager(final View view) {
        getRawInputManager()
                .setHostView(view)
                .setUseRawInput(getDeviceConfig().useRawInput())
                .setLimitRect(view)
                .startRawInputProcessor();
    }

    public TouchReader getTouchReader() {
        if (touchReader == null) {
            touchReader = new TouchReader(noteManager);
        }
        return touchReader;
    }

    public RawInputManager getRawInputManager() {
        if (rawInputManager == null) {
            rawInputManager = new RawInputManager(noteManager);
        }
        return rawInputManager;
    }

    public void pauseRawDrawing() {
        getRawInputManager().pauseRawDrawing();
        getEpdPenManager().pauseDrawing();
    }

    public void resumeRawDrawing() {
        getRawInputManager().resumeRawDrawing();
        getEpdPenManager().resumeDrawing();
    }

    public void quitRawDrawing() {
        getRawInputManager().quitRawDrawing();
        getEpdPenManager().quitDrawing();
    }

    public void quit() {
        pauseRawDrawing();
        quitRawDrawing();
    }

    private DeviceConfig getDeviceConfig() {
        return noteManager.getDeviceConfig();
    }
}
