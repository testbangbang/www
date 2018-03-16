package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/8.
 */

public class GotoPageRequest extends ReaderBaseRequest {
    private int page;

    public GotoPageRequest(Reader reader,int page) {
        super(reader);
        this.page = page;
    }

    @Override
    public GotoPageRequest call() throws Exception {
        getReader().getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        if (!getReader().getReaderHelper().getReaderLayoutManager().gotoPage(page)) {
            throw ReaderException.outOfRange();
        }
        getReader().getReaderEpdHelper().increaseRefreshCount();
        getReader().getReaderViewHelper().updatePageView(getReader(),getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(getReader());
        saveReaderOptions(getReader());
        return this;
    }
}
