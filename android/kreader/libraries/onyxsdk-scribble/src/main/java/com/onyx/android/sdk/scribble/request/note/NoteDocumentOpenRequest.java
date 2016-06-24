package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteDocumentOpenRequest extends BaseNoteRequest {

    private volatile String documentUniqueId;

    public NoteDocumentOpenRequest(final String id) {
        documentUniqueId = id;
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.getNoteDocument().open(getContext(), documentUniqueId);
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
