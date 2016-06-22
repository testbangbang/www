package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.host.request.ChangeViewConfigRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by Joy on 2016/5/24.
 */
public class ChangeViewConfigAction extends BaseAction {
    @Override
    public void execute(final ReaderActivity readerActivity) {
        final PageInfo pageInfo = readerActivity.getReaderViewInfo().getFirstVisiblePage();
        BaseReaderRequest config = new ChangeViewConfigRequest(readerActivity.getDisplayWidth(),
                readerActivity.getDisplayHeight(),
                pageInfo != null ? pageInfo.getName() : null);
        readerActivity.getReader().submitRequest(readerActivity, config, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                readerActivity.redrawPage();
            }
        });
    }
}
