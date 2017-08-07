package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageGotoLastRequest extends AsyncBaseNoteRequest {

    public PageGotoLastRequest() {
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    public void execute(final AsyncNoteViewHelper helper) throws Exception {
        helper.getNoteDocument().gotoLast();
        renderCurrentPageInBitmap(helper);
        updateShapeDataInfo(helper);
    }

}
