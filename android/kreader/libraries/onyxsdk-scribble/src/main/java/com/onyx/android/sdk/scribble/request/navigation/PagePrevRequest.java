package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PagePrevRequest extends BaseNoteRequest {

    public void execute(final NoteViewHelper helper) throws Exception {
        if (!helper.getNoteDocument().prevPage()) {
            return;
        }
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
