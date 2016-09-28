package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageGoToTargetIndexRequest extends BaseNoteRequest {
    private int targetIndex;

    public PageGoToTargetIndexRequest(int index) {
        targetIndex = index;
        setPauseInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        helper.getNoteDocument().gotoPage(targetIndex);
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
