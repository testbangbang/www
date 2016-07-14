package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageNextRequest extends BaseNoteRequest {

    public PageNextRequest() {
        setPauseInputProcessor(true);
    }

    // always render page.
    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.isDFBForCurrentShape());
        helper.getNoteDocument().nextPage();
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }


}
