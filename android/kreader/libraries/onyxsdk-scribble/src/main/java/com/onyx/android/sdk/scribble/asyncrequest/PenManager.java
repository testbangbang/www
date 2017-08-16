package com.onyx.android.sdk.scribble.asyncrequest;

import android.view.SurfaceView;
import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;

/**
 * Created by john on 12/8/2017.
 */

public class PenManager {

    private boolean useRawInput = false;
    private SurfaceView hostView = null;

    public static final int PEN_STOP = 0;
    public static final int PEN_START = 1;
    public static final int PEN_DRAWING = 2;
    public static final int PEN_PAUSE = 3;
    public static final int PEN_ERASING = 4;

    public boolean isUseRawInput() {
        return useRawInput;
    }

    public PenManager setHostView(final SurfaceView view) {
        hostView = view;
        return this;
    }

    public PenManager setUseRawInput(boolean use) {
        useRawInput = use;
        return this;
    }

    public void startDrawing() {
        if (!isUseRawInput()) {
            return;
        }
        EpdController.setScreenHandWritingPenState(hostView, PEN_START);
    }

    public void resumeDrawing() {
        if (!isUseRawInput()) {
            return;
        }

        EpdController.setScreenHandWritingPenState(hostView, PEN_DRAWING);
    }

    public void pauseDrawing() {
        if (!isUseRawInput()) {
            return;
        }
        EpdController.setScreenHandWritingPenState(hostView, PEN_PAUSE);
    }

    public void enableScreenPost(boolean enable) {
        if (hostView != null) {
            EpdController.enablePost(hostView, enable ? 1 : 0);
        }
    }

    public void quitDrawing() {
        EpdController.setScreenHandWritingPenState(hostView, PEN_STOP);
    }

}
