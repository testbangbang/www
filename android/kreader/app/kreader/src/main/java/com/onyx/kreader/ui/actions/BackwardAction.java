package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.host.request.BackwardRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 6/2/16.
 */
public class BackwardAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder) {
        if (!readerDataHolder.getReaderViewInfo().canGoBack) {
            return;
        }

        final BackwardRequest backwardRequest = new BackwardRequest();
        readerDataHolder.submitRenderRequest(backwardRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ShowReaderMenuAction.updateBackwardForwardState(readerDataHolder);
            }
        });
    }


}
