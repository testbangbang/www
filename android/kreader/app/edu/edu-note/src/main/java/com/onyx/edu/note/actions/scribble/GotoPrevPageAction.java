package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.request.navigation.PagePrevRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 7/1/16.
 */
public class GotoPrevPageAction extends BaseNoteAction {

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        PagePrevRequest prevRequest = new PagePrevRequest();
        prevRequest.setDrawToView(true);
        noteManager.submitRequest(prevRequest, callback);
    }
}
