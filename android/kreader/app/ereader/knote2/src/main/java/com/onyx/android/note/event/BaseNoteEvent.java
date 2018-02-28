package com.onyx.android.note.event;

/**
 * Created by lxm on 2018/2/28.
 */

public class BaseNoteEvent {

    private boolean resumePen;

    public BaseNoteEvent(boolean resumePen) {
        this.resumePen = resumePen;
    }

    public boolean isResumePen() {
        return resumePen;
    }

    public void setResumePen(boolean resumePen) {
        this.resumePen = resumePen;
    }
}
