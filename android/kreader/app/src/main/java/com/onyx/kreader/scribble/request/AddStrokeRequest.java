package com.onyx.kreader.scribble.request;

import com.onyx.kreader.scribble.ScribbleManager;
import com.onyx.kreader.scribble.data.ScribbleOption;

/**
 * Created by zhuzeng on 4/18/16.
 * generate path list from add all points and stroke width.
 * also add to pending list which will be used later.
 */
public class AddStrokeRequest extends BaseScribbleRequest {

    private ScribbleManager scribbleManager;
    private String documentMd5;
    private int initDisplayPage;
    private ScribbleOption scribbleOption;

    public AddStrokeRequest(final String md5, int page, final ScribbleOption option) {
        documentMd5 = md5;
        initDisplayPage = page;
        scribbleOption = option;
    }

    public void execute(final ScribbleManager parent) throws Exception {

    }
}
