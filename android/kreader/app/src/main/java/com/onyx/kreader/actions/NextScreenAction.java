package com.onyx.kreader.actions;

import com.onyx.kreader.host.request.NextScreenRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class NextScreenAction extends BaseAction {

    public void execute(final ReaderActivity readerActivity) {
        final NextScreenRequest request = new NextScreenRequest();
        readerActivity.submitRenderRequest(request);
    }

}
