package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageNextRequest extends AsyncBaseNoteRequest {

    public PageNextRequest() {
        setPauseInputProcessor(true);
    }

    // always render page.
    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        helper.getNoteDocument().nextPage();
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }


}
