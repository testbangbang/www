package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2017/12/27.
 */

public class NextScreenRequest extends ReaderBaseRequest {
    private Reader reader;

    public NextScreenRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public NextScreenRequest call() throws Exception {
        reader.getReaderHelper().nextScreen();
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(reader);
        return this;
    }
}
