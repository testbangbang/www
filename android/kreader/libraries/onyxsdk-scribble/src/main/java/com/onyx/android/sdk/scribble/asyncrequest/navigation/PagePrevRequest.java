package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PagePrevRequest extends AsyncBaseNoteRequest {

    public PagePrevRequest() {
        setPauseInputProcessor(true);
    }

    public void execute(final AsyncNoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        helper.getNoteDocument().prevPage();
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
