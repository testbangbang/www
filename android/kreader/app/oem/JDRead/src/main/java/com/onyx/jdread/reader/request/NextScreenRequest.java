package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2017/12/27.
 */

public class NextScreenRequest extends ReaderBaseRequest {

    public NextScreenRequest(Reader reader) {
        super(reader);
    }

    @Override
    public NextScreenRequest call() throws Exception {
        getReader().getReaderHelper().nextScreen();
        updateSetting(getReader());
        getReader().getReaderViewHelper().updatePageView(getReader(),getReaderUserDataInfo(),getReaderViewInfo());
        saveReaderOptions(getReader());
        return this;
    }
}
