package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageRemoveRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 6/30/16.
 */
public class DocumentDeletePageAction extends BaseNoteAction {

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        PageRemoveRequest pageRemoveRequest = new PageRemoveRequest();
        pageRemoveRequest.setRender(true);
        noteManager.submitRequest(pageRemoveRequest, callback);
    }
}
