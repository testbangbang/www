package com.onyx.android.note.event;

/**
 * Created by lxm on 2018/2/28.
 */

public class BaseNoteEvent {

    private boolean resumePen;
    private boolean rawRenderEnable;

    public BaseNoteEvent(boolean resumePen) {
        this.resumePen = resumePen;
    }

    public boolean isResumePen() {
        return resumePen;
    }

    public boolean isRawRenderEnable() {
        return rawRenderEnable;
    }

    public BaseNoteEvent setResumePen(boolean resumePen) {
        this.resumePen = resumePen;
        return this;
    }

    public BaseNoteEvent setRawRenderEnable(boolean rawRenderEnable) {
        this.rawRenderEnable = rawRenderEnable;
        return this;
    }
}
