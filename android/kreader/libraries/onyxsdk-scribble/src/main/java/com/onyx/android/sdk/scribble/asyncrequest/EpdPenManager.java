package com.onyx.android.sdk.scribble.asyncrequest;

import android.view.SurfaceView;

import com.onyx.android.sdk.api.device.epd.EpdController;

/**
 * Created by john on 12/8/2017.
 */

public class EpdPenManager {
    private static final String TAG = EpdPenManager.class.getSimpleName();

    private SurfaceView hostView = null;

    public static final int PEN_STOP = 0;
    public static final int PEN_START = 1;
    public static final int PEN_DRAWING = 2;
    public static final int PEN_PAUSE = 3;
    public static final int PEN_ERASING = 4;

    public EpdPenManager setHostView(final SurfaceView view) {
        hostView = view;
        return this;
    }

    public void startDrawing() {
        EpdController.setScreenHandWritingPenState(hostView, PEN_START);
    }

    public void resumeDrawing() {
        EpdController.setScreenHandWritingPenState(hostView, PEN_DRAWING);
    }

    public void pauseDrawing() {
        EpdController.setScreenHandWritingPenState(hostView, PEN_PAUSE);
    }

    public void enableScreenPost(boolean enable) {
        if (hostView != null) {
            EpdController.enablePost(hostView, enable ? 1 : 0);
        }
    }

    public void quitDrawing() {
        EpdController.setScreenHandWritingPenState(hostView, PEN_STOP);
        hostView = null;
    }

}
