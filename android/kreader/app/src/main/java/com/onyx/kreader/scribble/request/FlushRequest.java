package com.onyx.kreader.scribble.request;


import com.onyx.kreader.scribble.ShapeManager;
import com.onyx.kreader.scribble.shape.Shape;

import java.util.List;

/**
 * Created by zhuzeng on 4/25/16.
 * save all pages into database.
 */
public class FlushRequest extends BaseScribbleRequest {

    private String pageUniqueId;
    private List<Shape> shapeList;

    public FlushRequest(final String pageId, final List<Shape> list) {
        pageUniqueId = pageId;
        shapeList = list;
    }

    public void execute(final ShapeManager parent) throws Exception {

    }

}
