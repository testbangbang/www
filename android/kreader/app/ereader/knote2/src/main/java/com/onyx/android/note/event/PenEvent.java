package com.onyx.android.note.event;

/**
 * Created by lxm on 2018/2/28.
 */

public class PenEvent {

    private boolean resumeDrawingRender;

    public PenEvent(boolean resumeDrawingRender) {
        this.resumeDrawingRender = resumeDrawingRender;
    }

    public boolean isResumeDrawingRender() {
        return resumeDrawingRender;
    }

    public static PenEvent pauseDrawingRender() {
        return new PenEvent(false);
    }

    public static PenEvent resumeDrawingRender() {
        return new PenEvent(true);
    }

}
