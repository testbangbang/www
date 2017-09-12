package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.request.StartSideNodeRequest;
import com.onyx.android.sdk.reader.host.request.StopSideNodeRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by Joy on 2016/5/24.
 */
public class StopSideNoteAction extends BaseAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        BaseReaderRequest request = new StopSideNodeRequest(readerDataHolder.getDisplayWidth(),
                readerDataHolder.getDisplayHeight());
        readerDataHolder.submitRenderRequest(request, new BaseCallback() {
            @Override
            public void beforeDone(BaseRequest request, Throwable e) {
                if (e == null) {
                    readerDataHolder.setSideNoting(false);
                }
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
