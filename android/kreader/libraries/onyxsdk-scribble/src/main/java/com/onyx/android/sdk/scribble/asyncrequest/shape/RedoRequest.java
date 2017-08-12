package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class RedoRequest extends AsyncBaseNoteRequest {

    public RedoRequest() {
    }

    @Override
    public void execute(final NoteManager parent) throws Exception {
        parent.redo(getContext());
        renderCurrentPageInBitmap(parent);
        updateShapeDataInfo(parent);
    }

}
