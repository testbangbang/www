package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 7/1/16.
 */
public class PageAddRequest extends BaseNoteRequest {

    private volatile int pageIndex;

    public PageAddRequest(final int value) {
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
        pageIndex = value;
    }

    @Override
    public void execute(final NoteViewHelper parent) throws Exception {
        if (pageIndex < 0) {
            pageIndex = parent.getNoteDocument().getCurrentPageIndex();
        }
        parent.getNoteDocument().createBlankPage(getContext(), pageIndex);
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }
}
