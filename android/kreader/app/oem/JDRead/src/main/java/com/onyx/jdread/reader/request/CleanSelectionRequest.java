package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class CleanSelectionRequest extends ReaderBaseRequest {
    private Reader reader;

    public CleanSelectionRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public CleanSelectionRequest call() throws Exception {
        reader.getReaderSelectionHelper().clear();
        updateSetting(reader);
        return this;
    }
}
