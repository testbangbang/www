package com.onyx.jdread.reader.event;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class AddNoteEvent {
    public String newNote;
    public String srcNote;
    public int srcNoteState;

    public AddNoteEvent(String newNote, String srcNote, int srcNoteState) {
        this.newNote = newNote;
        this.srcNote = srcNote;
        this.srcNoteState = srcNoteState;
    }
}
