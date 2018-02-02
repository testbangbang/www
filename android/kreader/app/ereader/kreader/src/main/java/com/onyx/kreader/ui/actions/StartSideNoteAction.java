package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.android.sdk.reader.host.request.StartSideNodeRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by Joy on 2016/5/24.
 */
public class StartSideNoteAction extends BaseAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final StartSideNodeRequest sideNodeRequest = new StartSideNodeRequest(readerDataHolder.getDisplayWidth(),
                readerDataHolder.getDisplayHeight());
        readerDataHolder.submitRenderRequest(sideNodeRequest, new BaseCallback() {

            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
