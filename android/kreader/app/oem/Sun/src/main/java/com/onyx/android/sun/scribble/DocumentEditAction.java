package com.onyx.android.sun.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteDocumentOpenRequest;

/**
 * Created by zhuzeng on 6/27/16.
 */
public class DocumentEditAction extends BaseNoteAction {
    private volatile String uniqueId;
    private volatile String parentUniqueId;

    public DocumentEditAction(final String id, final String parentId) {
        uniqueId = id;
        parentUniqueId = parentId;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        NoteDocumentOpenRequest openRequest = new NoteDocumentOpenRequest(uniqueId, parentUniqueId, false);
        noteManager.submitRequest(openRequest, callback);
    }
}

