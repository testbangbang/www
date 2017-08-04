package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteDocumentRemoveRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 6/27/16.
 */
public class DocumentDiscardAction extends BaseNoteAction {

    private volatile String uniqueId;

    public DocumentDiscardAction(final String id) {
        uniqueId = id;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        final NoteDocumentRemoveRequest removeRequest = new NoteDocumentRemoveRequest(uniqueId);
        noteManager.submitRequest(removeRequest, callback);
    }
}
