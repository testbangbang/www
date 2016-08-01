package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.request.GotoLocationRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class GotoPageAction extends BaseAction {
    private String pageName;

    public GotoPageAction(final String name) {
        pageName = name;
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        execute(readerDataHolder, null);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        BaseReaderRequest gotoPosition = new GotoLocationRequest(pageName);
        readerDataHolder.submitRenderRequest(gotoPosition, baseCallback);
    }
}
