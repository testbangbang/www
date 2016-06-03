package com.onyx.kreader.scribble.request;

import com.onyx.kreader.scribble.ScribbleManager;


/**
 * Created by zhuzeng on 12/25/15.
 */
public class RemoveByPositionRequest extends BaseScribbleRequest {

    private String documentIndex;
    private String position;

    public RemoveByPositionRequest(final String doc, final String k) {
        documentIndex = doc;
        position = k;
    }

    @Override
    public void execute(final ScribbleManager parent) throws Exception {

    }
}
