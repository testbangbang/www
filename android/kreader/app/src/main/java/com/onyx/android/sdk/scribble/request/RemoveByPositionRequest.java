package com.onyx.android.sdk.scribble.request;

import com.onyx.android.sdk.scribble.ShapeViewHelper;


/**
 * Created by zhuzeng on 12/25/15.
 * remove from memory
 */
public class RemoveByPositionRequest extends BaseScribbleRequest {

    private String documentIndex;
    private String position;

    public RemoveByPositionRequest(final String doc, final String k) {
        documentIndex = doc;
        position = k;
    }

    @Override
    public void execute(final ShapeViewHelper parent) throws Exception {

    }
}
