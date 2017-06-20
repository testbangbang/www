package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageGoToTargetIndexRequest extends BaseNoteRequest {
    private int targetIndex;
    private boolean resume;

    public PageGoToTargetIndexRequest(int index,boolean r) {
        targetIndex = index;
        setPauseInputProcessor(true);
        resume = r;
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(resume && helper.useDFBForCurrentState());
        helper.getNoteDocument().gotoPage(targetIndex);
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
