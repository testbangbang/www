package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageGotoFirstRequest extends AsyncBaseNoteRequest {

    public PageGotoFirstRequest() {
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        helper.getNoteDocument().gotoFirst();
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
