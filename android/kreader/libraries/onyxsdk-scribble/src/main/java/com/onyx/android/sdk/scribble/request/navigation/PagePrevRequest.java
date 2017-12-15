package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PagePrevRequest extends BaseNoteRequest {

    private volatile boolean resume;

    public PagePrevRequest(boolean resume) {
        this.resume = resume;
    }

    public PagePrevRequest() {
        setPauseInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState() && resume);
        helper.getNoteDocument().prevPage();
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
