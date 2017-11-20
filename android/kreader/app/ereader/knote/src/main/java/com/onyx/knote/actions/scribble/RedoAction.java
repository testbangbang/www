package com.onyx.knote.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.shape.RedoRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.knote.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 7/19/16.
 */
public class RedoAction extends BaseNoteAction {

    public RedoAction(boolean isResume) {
        this.isResume = isResume;
    }


    public RedoAction() {
        this.isResume = true;
    }

    private boolean isResume = false;


    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        RedoRequest redoRequest = new RedoRequest();
        redoRequest.setResumeInputProcessor(isResume);
        redoRequest.setRender(true);
        noteManager.submitRequest(redoRequest, callback);
    }
}
