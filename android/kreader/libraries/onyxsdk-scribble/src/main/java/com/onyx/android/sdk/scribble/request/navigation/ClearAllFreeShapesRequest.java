package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 8/7/16.
 */
public class ClearAllFreeShapesRequest extends BaseNoteRequest {

    public ClearAllFreeShapesRequest(boolean resume) {
        setPauseInputProcessor(true);
        setResumeInputProcessor(resume);
    }

    @Override
    public void execute(final NoteViewHelper parent) throws Exception {
        parent.getNoteDocument().clearFreeShapes(getContext(), parent.getNoteDocument().getCurrentPageIndex());
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
