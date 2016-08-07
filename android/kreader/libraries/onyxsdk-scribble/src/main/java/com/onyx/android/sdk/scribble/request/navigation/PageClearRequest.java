package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 8/7/16.
 */
public class PageClearRequest extends BaseNoteRequest {

    public PageClearRequest() {
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    @Override
    public void execute(final NoteViewHelper parent) throws Exception {
        parent.getNoteDocument().clearPage(getContext(), parent.getNoteDocument().getCurrentPageIndex());
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
