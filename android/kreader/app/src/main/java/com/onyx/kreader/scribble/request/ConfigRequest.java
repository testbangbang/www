package com.onyx.kreader.scribble.request;

import com.onyx.kreader.scribble.ScribbleManager;
import com.onyx.kreader.scribble.data.ScribbleOption;

/**
 * Created by zhuzeng on 4/18/16.
 */
public class ConfigRequest extends BaseScribbleRequest {

    private String documentMd5;
    private int initDisplayPage;

    public ConfigRequest(final String md5, int page, final ScribbleOption option) {
        documentMd5 = md5;
        initDisplayPage = page;
    }

    public void execute(final ScribbleManager parent) throws Exception {

    }

}
