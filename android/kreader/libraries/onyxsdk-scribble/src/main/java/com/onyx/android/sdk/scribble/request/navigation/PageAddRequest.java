package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 7/1/16.
 */
public class PageAddRequest extends BaseNoteRequest {

    private volatile int pageIndex;
    private volatile int maxPageCount = 0;
    private volatile boolean outOfMaxPageCount = false;

    public PageAddRequest(final int value) {
        setPauseInputProcessor(true);
        pageIndex = value;
    }

    public PageAddRequest(int pageIndex, int maxPageCount) {
        this.pageIndex = pageIndex;
        this.maxPageCount = maxPageCount;
    }

    @Override
    public void execute(final NoteViewHelper parent) throws Exception {
        setResumeInputProcessor(parent.useDFBForCurrentState());
        int pageCount = parent.getNoteDocument().getPageCount();
        if (maxPageCount > 0 && pageCount >= maxPageCount) {
            outOfMaxPageCount = true;
        }else {
            if (pageIndex < 0) {
                pageIndex = parent.getNoteDocument().getCurrentPageIndex() + 1;
            }
            parent.getNoteDocument().createBlankPage(getContext(), pageIndex);
            renderCurrentPage(parent);
        }
        updateShapeDataInfo(parent);
    }

    public boolean isOutOfMaxPageCount() {
        return outOfMaxPageCount;
    }
}
