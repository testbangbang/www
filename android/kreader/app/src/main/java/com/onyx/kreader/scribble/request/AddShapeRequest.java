package com.onyx.kreader.scribble.request;

import com.onyx.kreader.scribble.ShapeViewHelper;
import com.onyx.kreader.scribble.data.ShapeManagerOptions;

/**
 * Created by zhuzeng on 4/18/16.
 * generate path list from add all points and stroke width.
 * also add to pending list which will be used later.
 */
public class AddShapeRequest extends BaseScribbleRequest {

    private String documentMd5;
    private int initDisplayPage;
    private ShapeManagerOptions shapeManagerOptions;

    public AddShapeRequest(final String md5, int page, final ShapeManagerOptions option) {
        documentMd5 = md5;
        initDisplayPage = page;
        shapeManagerOptions = option;
    }

    public void execute(final ShapeViewHelper parent) throws Exception {

    }
}
