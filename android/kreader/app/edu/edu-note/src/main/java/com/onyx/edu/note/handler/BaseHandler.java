package com.onyx.edu.note.handler;

import com.onyx.edu.note.NoteManager;

/**
 * Created by solskjaer49 on 2017/5/27 12:30.
 */

public abstract class BaseHandler {
    private static final String TAG = BaseHandler.class.getSimpleName();
    protected NoteManager mNoteManager;

    public BaseHandler(NoteManager mNoteManager) {
        this.mNoteManager = mNoteManager;
    }

    public abstract void onActivate();

    public abstract void onDeactivate();

    public abstract void close();
}
