package com.onyx.jdread.reader.menu.request;

import com.onyx.jdread.reader.data.ChangeLayoutParameter;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ChangeLayoutRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ChangeLayoutParameter parameter;

    public ChangeLayoutRequest(ReaderDataHolder readerDataHolder,ChangeLayoutParameter parameter) {
        this.readerDataHolder = readerDataHolder;
        this.parameter = parameter;
    }

    @Override
    public ChangeLayoutRequest call() throws Exception {
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().setCurrentLayout(parameter.getLayout(), parameter.getNavigationArgs());
        readerDataHolder.getReader().getReaderViewHelper().updatePageView(readerDataHolder);
        return this;
    }
}
