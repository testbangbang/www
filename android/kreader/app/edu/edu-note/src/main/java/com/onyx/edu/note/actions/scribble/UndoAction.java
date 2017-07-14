package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.request.shape.UndoRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class UndoAction extends BaseNoteAction {
    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        UndoRequest undoRequest = new UndoRequest();
        undoRequest.setResumeInputProcessor(true);
        undoRequest.setRender(true);
        noteManager.submitRequest(undoRequest, callback);
    }
}
