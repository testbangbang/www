package com.onyx.kreader.ui.actions;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.host.request.GetTableOfContentRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

public class GetTableOfContentAction extends BaseAction{

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        showLoadingDialog(readerDataHolder, R.string.loading);
        final GetTableOfContentRequest tocRequest = new GetTableOfContentRequest();
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), tocRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                callback.done(request, e);
            }
        });
    }
}
