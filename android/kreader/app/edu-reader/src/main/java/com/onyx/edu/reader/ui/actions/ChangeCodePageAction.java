package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.edu.reader.R;
import com.onyx.android.sdk.reader.host.request.ChangeCodePageRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;


/**
 * Created by zhuzeng on 9/22/16.
 */
public class ChangeCodePageAction extends BaseAction {
    private final int codePage;

    public ChangeCodePageAction(final int codePage) {
        this.codePage = codePage;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        showLoadingDialog(readerDataHolder, R.string.code_page);
        readerDataHolder.submitRenderRequest(new ChangeCodePageRequest(codePage), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                hideLoadingDialog();
                BaseCallback.invoke(callback, request, e);
            }
        });
    }


}
