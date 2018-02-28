package com.onyx.android.note.common.base;

import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.rx.RxAction;

/**
 * Created by lxm on 2018/2/25.
 */

public abstract class BaseNoteAction extends RxAction {

    private NoteManager noteManager;

    public BaseNoteAction(NoteManager noteManager) {
        this.noteManager = noteManager;
    }

    public NoteManager getNoteManager() {
        return noteManager;
    }
}
