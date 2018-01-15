package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ScaleToPageCropRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ReaderViewInfo readerViewInfo;

    public ScaleToPageCropRequest(ReaderDataHolder readerDataHolder,ReaderViewInfo readerViewInfo) {
        this.readerViewInfo = readerViewInfo;
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public ScaleToPageCropRequest call() throws Exception {
        String pageName = readerViewInfo.getFirstVisiblePage().getName();
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().setCurrentLayout(PageConstants.SINGLE_PAGE, new NavigationArgs());
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().scaleToPageContent(pageName);
        readerDataHolder.getReader().getReaderViewHelper().updatePageView(readerDataHolder,getReaderUserDataInfo(),getReaderViewInfo());
        return this;
    }
}
