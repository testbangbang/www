package com.onyx.knote.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.shape.UndoRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.knote.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class UndoAction extends BaseNoteAction {

    public UndoAction(boolean isResume) {
        this.isResume = isResume;
    }


    public UndoAction() {
        this.isResume = true;
    }

    private boolean isResume = false;


    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        UndoRequest undoRequest = new UndoRequest();
        undoRequest.setResumeInputProcessor(isResume);
        undoRequest.setRender(true);
        noteManager.submitRequest(undoRequest, callback);
    }
}
