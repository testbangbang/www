package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PagePrevRequest extends AsyncBaseNoteRequest {

    public PagePrevRequest() {
        setPauseInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        helper.getNoteDocument().prevPage();
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
