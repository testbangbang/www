package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.request.shape.RedoRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 7/19/16.
 */
public class RedoAction extends BaseNoteAction {
    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        RedoRequest redoRequest = new RedoRequest();
        redoRequest.setResumeInputProcessor(true);
        redoRequest.setRender(true);
        noteManager.submitRequest(redoRequest, callback);
    }
}
