package com.onyx.android.sdk.scribble.request;

import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.data.ShapeManagerOptions;

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
