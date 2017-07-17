package com.onyx.edu.note.actions.scribble;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteDocumentOpenRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 6/27/16.
 */
public class DocumentCreateAction extends BaseNoteAction {
    private volatile String uniqueId;
    private volatile String parentUniqueId;

    public DocumentCreateAction(final String id, final String parent) {
        uniqueId = id;
        parentUniqueId = parent;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        NoteDocumentOpenRequest createRequest = new NoteDocumentOpenRequest(uniqueId, parentUniqueId, true);
        noteManager.submitRequest(createRequest, callback);
    }
}
