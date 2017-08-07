package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class UndoRequest extends AsyncBaseNoteRequest {

    public UndoRequest() {
    }

    @Override
    public void execute(final AsyncNoteViewHelper parent) throws Exception {
        parent.undo(getContext());
        renderCurrentPageInBitmap(parent);
        updateShapeDataInfo(parent);
    }
}
