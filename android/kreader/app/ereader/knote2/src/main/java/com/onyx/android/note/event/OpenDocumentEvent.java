package com.onyx.android.note.event;

/**
 * Created by lxm on 2018/2/28.
 */

public class OpenDocumentEvent extends BaseNoteEvent {

    public OpenDocumentEvent(boolean resumePen) {
        super(resumePen);
    }
}
