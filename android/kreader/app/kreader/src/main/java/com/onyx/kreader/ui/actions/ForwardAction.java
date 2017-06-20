package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.host.request.ForwardRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 6/2/16.
 */
public class ForwardAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        if (!readerDataHolder.getReaderViewInfo().canGoForward) {
            BaseCallback.invoke(callback, null, null);
            return;
        }

        final ForwardRequest forwardRequest = new ForwardRequest();
        readerDataHolder.submitRenderRequest(forwardRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ShowReaderMenuAction.updateReaderMenuState(readerDataHolder);
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

}
