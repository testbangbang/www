package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.request.ScaleToPageCropRequest;
import com.onyx.kreader.host.request.ScaleToWidthContentRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/29/16.
 */
public class TogglePageCropAction extends BaseAction {

    private String pageName;

    public TogglePageCropAction(final String name) {
        pageName = name;
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        BaseReaderRequest request = new ScaleToWidthContentRequest(pageName);
        if (readerDataHolder.getReaderViewInfo().scale == PageConstants.SCALE_TO_WIDTH_CONTENT) {
            request = new ScaleToPageCropRequest(pageName);
        }
        readerDataHolder.submitRenderRequest(request);
    }
}
