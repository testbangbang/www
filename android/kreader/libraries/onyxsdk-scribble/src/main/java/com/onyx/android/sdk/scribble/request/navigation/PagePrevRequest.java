package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PagePrevRequest extends BaseNoteRequest {

    public void execute(final ShapeViewHelper helper) throws Exception {
        if (!helper.getNoteDocument().prevPage()) {
            return;
        }
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
