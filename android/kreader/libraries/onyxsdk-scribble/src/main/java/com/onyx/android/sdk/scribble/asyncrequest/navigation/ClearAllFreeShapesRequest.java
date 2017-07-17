package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by zhuzeng on 8/7/16.
 */
public class ClearAllFreeShapesRequest extends AsyncBaseNoteRequest {

    public ClearAllFreeShapesRequest() {
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    @Override
    public void execute(final NoteViewHelper parent) throws Exception {
        parent.getNoteDocument().clearFreeShapes(getContext(), parent.getNoteDocument().getCurrentPageIndex());
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
