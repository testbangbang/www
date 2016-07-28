package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
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
        execute(readerActivity, null);
    }

    @Override
    public void execute(ReaderActivity readerActivity, BaseCallback baseCallback) {
        BaseReaderRequest gotoPosition = new GotoLocationRequest(pageName);
        readerActivity.submitRequest(gotoPosition, baseCallback);
    }
}
