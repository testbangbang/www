package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.R;
import com.onyx.kreader.host.request.NextScreenRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class NextScreenAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder) {
        final NextScreenRequest request = new NextScreenRequest();
        readerDataHolder.submitRenderRequest(request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                super.progress(request, info);
                showLoadingDialog(readerDataHolder, R.string.loading);
            }
        });
    }

}
