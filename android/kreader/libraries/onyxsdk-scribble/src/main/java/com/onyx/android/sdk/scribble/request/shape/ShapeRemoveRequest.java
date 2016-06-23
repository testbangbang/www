package com.onyx.android.sdk.scribble.request.shape;

import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;


/**
 * Created by zhuzeng on 12/25/15.
 * remove from memory
 */
public class ShapeRemoveRequest extends BaseNoteRequest {

    private String documentIndex;
    private String pageUniqueId;

    public ShapeRemoveRequest(final String doc, final String pageId) {
        documentIndex = doc;
        pageUniqueId = pageId;
    }

    @Override
    public void execute(final ShapeViewHelper parent) throws Exception {

    }
}
