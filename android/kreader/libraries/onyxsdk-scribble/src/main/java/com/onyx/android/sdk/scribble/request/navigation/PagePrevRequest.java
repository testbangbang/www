package com.onyx.android.sdk.scribble.request.navigation;

import android.util.Log;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PagePrevRequest extends BaseNoteRequest {

    public PagePrevRequest() {
        setPauseInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.isDFBForCurrentShape());
        helper.getNoteDocument().prevPage();
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
