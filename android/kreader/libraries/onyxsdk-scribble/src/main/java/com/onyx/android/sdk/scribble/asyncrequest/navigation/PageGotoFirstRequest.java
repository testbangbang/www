package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageGotoFirstRequest extends AsyncBaseNoteRequest {

    public PageGotoFirstRequest() {
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    public void execute(final AsyncNoteViewHelper helper) throws Exception {
        helper.getNoteDocument().gotoFirst();
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
