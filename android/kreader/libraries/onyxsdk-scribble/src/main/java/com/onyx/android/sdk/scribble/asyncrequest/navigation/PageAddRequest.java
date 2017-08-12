package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;

/**
 * Created by zhuzeng on 7/1/16.
 */
public class PageAddRequest extends AsyncBaseNoteRequest {

    private volatile int pageIndex;

    public PageAddRequest(final int value) {
        setPauseInputProcessor(true);
        pageIndex = value;
    }

    @Override
    public void execute(final NoteManager parent) throws Exception {
        setResumeInputProcessor(parent.useDFBForCurrentState());
        if (pageIndex < 0) {
            pageIndex = parent.getNoteDocument().getCurrentPageIndex() + 1;
        }
        parent.getNoteDocument().createBlankPage(getContext(), pageIndex);
        renderCurrentPageInBitmap(parent);
        updateShapeDataInfo(parent);
    }
}
