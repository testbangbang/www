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

    private TouchReader touchReader;
    private RawInputManager rawInputManager;

    public TouchHelper(NoteManager parent) {
        this.noteManager = parent;
    }

    public void setup(View view) {
        setupTouchReader(view);
        setupRawInputManager(view);
    }

    private void setupTouchReader(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return getTouchReader().processTouchEvent(motionEvent);
            }
        });

        Rect softwareLimitRect = new Rect();
        view.getLocalVisibleRect(softwareLimitRect);
        getTouchReader().setLimitRect(softwareLimitRect);
    }

    private void setupRawInputManager(final View view) {
        getRawInputManager().setHostView(view)
                .setUseRawInput(getDeviceConfig().useRawInput());
        getRawInputManager().setLimitRect(view);
        getRawInputManager().startRawInputProcessor();
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
    }

    public void resumeRawDrawing() {
        getRawInputManager().resumeRawDrawing();
    }

    public void quitRawDrawing() {
        getRawInputManager().quitRawDrawing();
    }

    public void quit() {
        pauseRawDrawing();
        quitRawDrawing();
    }

    private void setInputLimitRect(View view) {
        getRawInputManager().setLimitRect(view);
    }

    private DeviceConfig getDeviceConfig() {
        return noteManager.getDeviceConfig();
    }
}
