package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2017/12/27.
 */

public class PreviousScreenRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;

    public PreviousScreenRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public PreviousScreenRequest call() throws Exception {
        readerDataHolder.getReader().getReaderHelper().previousScreen();
        readerDataHolder.getReaderViewHelper().updatePageView(readerDataHolder,getReaderUserDataInfo(),getReaderViewInfo());
        return this;
    }
}
