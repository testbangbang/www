package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class UndoRequest extends AsyncBaseNoteRequest {

    public UndoRequest() {
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.undo(getContext());
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }
}
