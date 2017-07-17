package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class RedoRequest extends AsyncBaseNoteRequest {

    public RedoRequest() {
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.redo(getContext());
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
