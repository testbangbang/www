package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageGotoFirstRequest extends BaseNoteRequest {

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
