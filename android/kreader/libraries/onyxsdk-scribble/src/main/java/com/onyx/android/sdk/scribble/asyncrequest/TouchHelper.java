package com.onyx.android.sdk.scribble.asyncrequest;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lxm on 2017/8/15.
 */

public class TouchHelper {

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
}
