package com.onyx.android.sdk.scribble.request.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class RedoRequest extends BaseNoteRequest {

    public RedoRequest(boolean resumeDrawing) {
        setResumeInputProcessor(resumeDrawing);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.redo(getContext());
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
