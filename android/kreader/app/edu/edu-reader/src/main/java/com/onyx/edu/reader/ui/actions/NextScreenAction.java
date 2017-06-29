package com.onyx.edu.reader.ui.actions;

import android.widget.Toast;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.R;
import com.onyx.android.sdk.reader.host.request.NextScreenRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.PageChangedEvent;

/**
 * Created by zhuzeng on 5/17/16.
 */
public class NextScreenAction extends BaseAction {

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        if (!readerDataHolder.getReaderViewInfo().canNextScreen) {
            Toast.makeText(readerDataHolder.getContext(), readerDataHolder.getContext().getString(R.string.max_page_toast), Toast.LENGTH_SHORT).show();
            return;
        }
        final PageChangedEvent pageChangedEvent = readerDataHolder.beforePageChange();
        final NextScreenRequest request = new NextScreenRequest();
        readerDataHolder.submitRenderRequest(request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerDataHolder.afterPageChange(pageChangedEvent);
                hideLoadingDialog();
                BaseCallback.invoke(callback, request, e);
            }

            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
                super.progress(request, info);
                showLoadingDialog(readerDataHolder, R.string.loading);
            }
        });
    }

}