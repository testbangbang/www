package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.request.PreviousScreenRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class PreviousScreenAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder) {
        final PreviousScreenRequest request = new PreviousScreenRequest();
        readerDataHolder.submitRequest(request);
    }

}
