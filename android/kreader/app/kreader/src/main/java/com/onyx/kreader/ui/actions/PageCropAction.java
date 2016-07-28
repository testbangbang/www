package com.onyx.kreader.ui.actions;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.request.ScaleToPageCropRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/29/16.
 */
public class PageCropAction extends BaseAction {

    private String pageName;

    public PageCropAction(final String name) {
        pageName = name;
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        final BaseReaderRequest request = new ScaleToPageCropRequest(pageName);
        readerDataHolder.submitRequest(request);
    }
}
