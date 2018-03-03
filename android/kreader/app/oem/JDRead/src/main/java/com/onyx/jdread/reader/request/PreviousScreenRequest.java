package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2017/12/27.
 */

public class PreviousScreenRequest extends ReaderBaseRequest {

    public PreviousScreenRequest(Reader reader) {
        super(reader);
    }

    @Override
    public PreviousScreenRequest call() throws Exception {
        getReader().getReaderHelper().previousScreen();
        updateSetting(getReader());
        getReader().getReaderViewHelper().updatePageView(getReader(),getReaderUserDataInfo(),getReaderViewInfo());
        saveReaderOptions(getReader());
        return this;
    }

}
