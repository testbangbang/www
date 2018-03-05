package com.onyx.jdread.reader.event;

import com.onyx.android.sdk.data.model.Annotation;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class UpdateNoteEvent {
    public Annotation annotation;
    public String newNote;
    public String srcNote;
    public int srcNoteState;

    public UpdateNoteEvent(Annotation annotation, String newNote, String srcNote, int srcNoteState) {
        this.annotation = annotation;
        this.newNote = newNote;
        this.srcNote = srcNote;
        this.srcNoteState = srcNoteState;
    }
}
