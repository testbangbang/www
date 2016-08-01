package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.request.NextScreenRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class NextScreenAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder) {
        final NextScreenRequest request = new NextScreenRequest();
        readerDataHolder.submitRenderRequest(request);
    }

}
