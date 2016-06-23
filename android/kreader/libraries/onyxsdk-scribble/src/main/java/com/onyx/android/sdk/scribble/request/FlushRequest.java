package com.onyx.android.sdk.scribble.request;


import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;

/**
 * Created by zhuzeng on 4/25/16.
 * save all pages into database.
 */
public class FlushRequest extends BaseNoteRequest {

    private String pageUniqueId;
    private List<Shape> shapeList;

    public FlushRequest(final String pageId, final List<Shape> list) {
        pageUniqueId = pageId;
        shapeList = list;
    }

    public void execute(final ShapeViewHelper parent) throws Exception {

    }

}
