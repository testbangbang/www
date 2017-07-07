package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.request.navigation.PageNextRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 7/1/16.
 */
public class GotoNextPageAction extends BaseNoteAction {

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        PageNextRequest nextRequest = new PageNextRequest();
        nextRequest.setDrawToView(true);
        noteManager.submitRequest(nextRequest, callback);
    }
}
