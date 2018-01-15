package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2017/12/27.
 */

public class NextScreenRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;

    public NextScreenRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public NextScreenRequest call() throws Exception {
        readerDataHolder.getReader().getReaderHelper().nextScreen();
        readerDataHolder.getReaderViewHelper().updatePageView(readerDataHolder,getReaderUserDataInfo(),getReaderViewInfo());
        return this;
    }
}
