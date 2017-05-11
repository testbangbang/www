package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToPageCropRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/29/16.
 */
public class PageCropAction extends BaseAction {

    private String pageName;

    public PageCropAction(final String name) {
        pageName = name;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final BaseReaderRequest request = new ScaleToPageCropRequest(pageName);
        readerDataHolder.submitRenderRequest(request, callback);
    }
}
