package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/8.
 */

public class GotoPageRequest extends ReaderBaseRequest {
    private Reader reader;
    private int page;

    public GotoPageRequest(Reader reader,int page) {
        this.reader = reader;
        this.page = page;
    }

    @Override
    public GotoPageRequest call() throws Exception {
        reader.getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        if (!reader.getReaderHelper().getReaderLayoutManager().gotoPage(page)) {
            throw ReaderException.outOfRange();
        }
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(reader);
        return this;
    }
}
