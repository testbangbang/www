package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/21.
 */

public class CleanSelectionRequest extends ReaderBaseRequest {

    public CleanSelectionRequest(Reader reader) {
        super(reader);
    }

    @Override
    public CleanSelectionRequest call() throws Exception {
        getReader().getReaderSelectionHelper().clear();
        updateSetting(getReader());
        return this;
    }
}
