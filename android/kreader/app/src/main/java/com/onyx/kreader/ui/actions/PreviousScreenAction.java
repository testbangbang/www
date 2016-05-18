package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.request.PreviousScreenRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class PreviousScreenAction extends BaseAction {

    public void execute(final ReaderActivity readerActivity) {
        final PreviousScreenRequest request = new PreviousScreenRequest();
        readerActivity.submitRenderRequest(request);
    }

}
