package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ScaleToPageCropRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;

    public ScaleToPageCropRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public ScaleToPageCropRequest call() throws Exception {
        String pageName = readerDataHolder.getReader().getReaderViewHelper().getReaderViewInfo().getFirstVisiblePage().getName();
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().setCurrentLayout(PageConstants.SINGLE_PAGE, new NavigationArgs());
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().scaleToPageContent(pageName);
        readerDataHolder.getReader().getReaderViewHelper().updatePageView(readerDataHolder);
        return this;
    }
}
