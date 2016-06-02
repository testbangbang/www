package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.request.ForwardRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 6/2/16.
 */
public class ForwardAction extends BaseAction {

    public void execute(final ReaderActivity readerActivity) {
        if (!readerActivity.getReaderViewInfo().canGoForward) {
            return;
        }

        final ForwardRequest forwardRequest = new ForwardRequest();
        readerActivity.submitRequest(forwardRequest);
    }

}
