package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;


/**
 * Created by zhuzeng on 12/25/15.
 * remove from memory
 */
public class PageRemoveRequest extends BaseNoteRequest {

    private String documentIndex;
    private volatile int pageIndex;

    public PageRemoveRequest(final String doc, final int value) {
        documentIndex = doc;
        pageIndex = value;
    }

    @Override
    public void execute(final NoteViewHelper parent) throws Exception {
        parent.getNoteDocument().removePage(getContext(), pageIndex);
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }
}
