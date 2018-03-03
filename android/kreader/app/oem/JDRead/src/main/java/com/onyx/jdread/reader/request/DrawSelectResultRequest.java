package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.ReaderSelectionHelper;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;

/**
 * Created by huxiaomao on 2018/2/8.
 */

public class DrawSelectResultRequest extends ReaderBaseRequest {

    public DrawSelectResultRequest(Reader reader) {
        super(reader);
    }

    @Override
    public DrawSelectResultRequest call() throws Exception {
        ReaderSelectionHelper readerSelectionHelper = getReader().getReaderSelectionHelper();
        LayoutProviderUtils.updateReaderViewInfo(getReader(), getReaderViewInfo(), getReader().getReaderHelper().getReaderLayoutManager());
        getReader().getReaderEpdHelper().setUseDefaultUpdate(true);
        getReader().getReaderViewHelper().renderAll(getReader(),
                getReader().getReaderHelper().getCurrentPageBitmap().getBitmap(), getReaderUserDataInfo(), getReaderViewInfo(),
                readerSelectionHelper);
        getReader().getReaderEpdHelper().setUseDefaultUpdate(false);
        updateSetting(getReader());
        return this;
    }
}
