package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.data.ShapeManagerOptions;

/**
 * Created by zhuzeng on 4/18/16.
 * generate path list from add all points and stroke width.
 * also add to pending list which will be used later.
 */
public class ShapeAddRequest extends AsyncBaseNoteRequest {

    private String documentMd5;
    private int initDisplayPage;
    private ShapeManagerOptions shapeManagerOptions;

    public ShapeAddRequest(final String md5, int page, final ShapeManagerOptions option) {
        documentMd5 = md5;
        initDisplayPage = page;
        shapeManagerOptions = option;
    }

    @Override
    public void execute(final AsyncNoteViewHelper parent) throws Exception {

    }
}
