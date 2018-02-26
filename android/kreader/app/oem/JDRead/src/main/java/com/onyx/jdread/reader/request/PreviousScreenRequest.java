package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2017/12/27.
 */

public class PreviousScreenRequest extends ReaderBaseRequest {
    private Reader reader;

    public PreviousScreenRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public PreviousScreenRequest call() throws Exception {
        reader.getReaderHelper().previousScreen();
        updateSetting(reader);
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        return this;
    }

}
