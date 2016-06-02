package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.request.BackwardRequest;
import com.onyx.kreader.host.request.ForwardRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 6/2/16.
 */
public class BackwardAction extends BaseAction {

    public void execute(final ReaderActivity readerActivity) {
        if (!readerActivity.getReaderViewInfo().canGoBack) {
            return;
        }

        final BackwardRequest backwardRequest = new BackwardRequest();
        readerActivity.submitRequest(backwardRequest);
    }

}
