package com.onyx.kreader.ui.actions;

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
        BaseReaderRequest gotoPosition = new GotoLocationRequest(pageName);
        readerDataHolder.submitRequest(gotoPosition);
    }
}
