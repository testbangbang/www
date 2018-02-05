package com.onyx.jdread.reader.event;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class AddNoteEvent {
    private String note;

    public AddNoteEvent(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }
}
