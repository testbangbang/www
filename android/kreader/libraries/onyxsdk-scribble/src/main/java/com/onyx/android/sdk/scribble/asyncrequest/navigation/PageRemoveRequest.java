package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;


/**
 * Created by zhuzeng on 12/25/15.
 * remove from memory
 */
public class PageRemoveRequest extends AsyncBaseNoteRequest {

    public PageRemoveRequest() {
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    @Override
    public void execute(final NoteViewHelper parent) throws Exception {
        parent.getNoteDocument().removePage(getContext(), parent.getNoteDocument().getCurrentPageIndex());
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }
}
