package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.host.request.BackwardRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 6/2/16.
 */
public class BackwardAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        if (!readerDataHolder.getReaderViewInfo().canGoBack) {
            BaseCallback.invoke(callback, null, null);
            return;
        }

        final BackwardRequest backwardRequest = new BackwardRequest();
        readerDataHolder.submitRenderRequest(backwardRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ShowReaderMenuAction.updateReaderMenuState(readerDataHolder);
                BaseCallback.invoke(callback, request, e);
            }
        });
    }


}
