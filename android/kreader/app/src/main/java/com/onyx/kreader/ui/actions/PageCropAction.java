package com.onyx.kreader.ui.actions;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.request.PanRequest;
import com.onyx.kreader.host.request.RenderRequest;
import com.onyx.kreader.host.request.ScaleRequest;
import com.onyx.kreader.host.request.ScaleToPageCrop;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 5/29/16.
 */
public class PageCropAction extends BaseAction {

    private String pageName;

    public PageCropAction(final String name) {
        pageName = name;
    }

    public void execute(final ReaderActivity readerActivity) {
        final BaseRequest request = new ScaleToPageCrop(pageName);
        readerActivity.submitRenderRequest(request);
    }
}
