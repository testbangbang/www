package com.onyx.android.note.event.menu;

import com.onyx.android.note.event.BaseNoteEvent;

/**
 * Created by lxm on 2018/2/28.
 */

public class UndoRedoEvent extends BaseNoteEvent {

    public UndoRedoEvent(boolean resumePen) {
        super(resumePen);
    }

}
