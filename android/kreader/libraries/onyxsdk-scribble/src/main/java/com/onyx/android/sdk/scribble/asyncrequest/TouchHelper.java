package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;

/**
 * Created by lxm on 2017/8/15.
 */

public class TouchHelper {
    private static final String TAG = TouchHelper.class.getSimpleName();
    private NoteManager parent;

    private TouchReader touchReader;
    private RawInputReader rawInputReader;

    public TouchHelper(NoteManager parent) {
        this.parent = parent;
    }

    public void onTouch(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return getTouchReader().processTouchEvent(motionEvent);
            }
        });
        getRawInputReader().getRawInputProcessor().setHostView(view);
        setRawInputLimitRect(view);
        getRawInputReader().startRawInputProcessor();
    }

    public TouchReader getTouchReader() {
        if (touchReader == null) {
            touchReader = new TouchReader(parent);
        }
        return touchReader;
    }

    public RawInputReader getRawInputReader() {
        if (rawInputReader == null) {
            rawInputReader = new RawInputReader(parent);
        }
        return rawInputReader;
    }

    public void pauseRawDrawing() {
        getRawInputReader().pauseRawDrawing();
    }

    public void resumeRawDrawing() {
        getRawInputReader().resumeRawDrawing();
    }

    public void quitRawDrawing() {
        getRawInputReader().quitRawDrawing();
    }

    public void quit() {
        pauseRawDrawing();
        quitRawDrawing();
    }

    private void setRawInputLimitRect(View view) {
        Rect softwareLimitRect = new Rect();
        //for software render limit rect
        view.getLocalVisibleRect(softwareLimitRect);
        getRawInputReader().getRawInputProcessor().setLimitRect(softwareLimitRect);
        EpdController.setScreenHandWritingRegionLimit(view,
                Math.min(softwareLimitRect.left, softwareLimitRect.right),
                Math.min(softwareLimitRect.top, softwareLimitRect.bottom),
                Math.max(softwareLimitRect.left, softwareLimitRect.right),
                Math.max(softwareLimitRect.top, softwareLimitRect.bottom));
    }
}
