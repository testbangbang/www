package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class RedoRequest extends AsyncBaseNoteRequest {

    public RedoRequest() {
    }

    @Override
    public void execute(final AsyncNoteViewHelper parent) throws Exception {
        parent.redo(getContext());
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
