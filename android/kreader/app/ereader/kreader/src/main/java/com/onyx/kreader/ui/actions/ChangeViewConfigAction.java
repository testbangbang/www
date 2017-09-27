package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.request.ChangeViewConfigRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by Joy on 2016/5/24.
 */
public class ChangeViewConfigAction extends BaseAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        int width = readerDataHolder.getDisplayWidth();
        if (readerDataHolder.isSideNoting()) {
            width /= 2;
        }
        BaseReaderRequest config = new ChangeViewConfigRequest(width,
                readerDataHolder.getDisplayHeight());
        readerDataHolder.submitRenderRequest(config, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
