package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageNextRequest extends AsyncBaseNoteRequest {

    public PageNextRequest() {
        setPauseInputProcessor(true);
    }

    // always render page.
    public void execute(final AsyncNoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        helper.getNoteDocument().nextPage();
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }


}
