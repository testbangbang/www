package com.onyx.kreader.scribble.request;

import com.onyx.kreader.scribble.ShapeViewHelper;
import com.onyx.kreader.scribble.data.ShapeManagerOptions;

/**
 * Created by zhuzeng on 4/18/16.
 */
public class ConfigRequest extends BaseScribbleRequest {

    private String documentMd5;
    private int initDisplayPage;

    public ConfigRequest(final String md5, int page, final ShapeManagerOptions option) {
        documentMd5 = md5;
        initDisplayPage = page;
    }

    public void execute(final ShapeViewHelper parent) throws Exception {

    }

}
