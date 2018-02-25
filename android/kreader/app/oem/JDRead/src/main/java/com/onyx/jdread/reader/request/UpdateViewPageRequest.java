package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.ReaderViewHelper;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class UpdateViewPageRequest extends ReaderBaseRequest {
    private Reader reader;

    public UpdateViewPageRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public UpdateViewPageRequest call() throws Exception {
        reader.getReaderSelectionHelper().clear();
        updatePageView();
        updateSetting(reader);
        return this;
    }

    public void updatePageView() {
        reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(),getReaderViewInfo(),null);
    }
}
