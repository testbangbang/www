package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2018/1/8.
 */

public class GotoPageRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private int page;

    public GotoPageRequest(ReaderDataHolder readerDataHolder,int page) {
        this.readerDataHolder = readerDataHolder;
        this.page = page;
    }

    @Override
    public GotoPageRequest call() throws Exception {
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        if (!readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().gotoPage(page)) {
            throw ReaderException.outOfRange();
        }
        readerDataHolder.getReaderViewHelper().updatePageView(readerDataHolder,getReaderUserDataInfo(),getReaderViewInfo());
        return this;
    }
}
