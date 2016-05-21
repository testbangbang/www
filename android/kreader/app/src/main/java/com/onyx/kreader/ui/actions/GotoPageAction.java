package com.onyx.kreader.ui.actions;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.request.GotoLocationRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class GotoPageAction extends BaseAction {
    private String pageName;

    public GotoPageAction(final String name) {
        pageName = name;
    }

    public void execute(final ReaderActivity readerActivity) {
        BaseRequest gotoPosition = new GotoLocationRequest(pageName);
        readerActivity.submitRenderRequest(gotoPosition);
    }
}
