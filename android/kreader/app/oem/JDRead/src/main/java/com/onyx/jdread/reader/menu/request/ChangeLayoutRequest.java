package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ChangeLayoutRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;

    public ChangeLayoutRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public ChangeLayoutRequest call() throws Exception {
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().setCurrentLayout(PageConstants.IMAGE_REFLOW_PAGE, new NavigationArgs());
        readerDataHolder.getReader().getReaderViewHelper().updatePageView(readerDataHolder);
        return this;
    }
}
