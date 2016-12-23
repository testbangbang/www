package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.kreader.host.request.ChangeCodePageRequest;
import com.onyx.kreader.host.request.ChangeStyleRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;


/**
 * Created by zhuzeng on 9/22/16.
 */
public class ChangeCodePageAction extends BaseAction {
    private final int codePage;

    public ChangeCodePageAction(final int codePage) {
        this.codePage = codePage;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.submitRenderRequest(new ChangeCodePageRequest(codePage), callback);
    }


}
