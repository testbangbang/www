package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.ReaderSelectionHelper;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;

/**
 * Created by huxiaomao on 2018/2/8.
 */

public class DrawSelectResultRequest extends ReaderBaseRequest {
    private Reader reader;

    public DrawSelectResultRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public DrawSelectResultRequest call() throws Exception {
        ReaderSelectionHelper readerSelectionHelper = reader.getReaderSelectionHelper();
        LayoutProviderUtils.updateReaderViewInfo(reader, getReaderViewInfo(), reader.getReaderHelper().getReaderLayoutManager());
        reader.getReaderEpdHelper().setUseDefaultUpdate(true);
        reader.getReaderViewHelper().renderAll(reader,
                reader.getReaderHelper().getCurrentPageBitmap().getBitmap(), getReaderUserDataInfo(), getReaderViewInfo(),
                readerSelectionHelper);
        reader.getReaderEpdHelper().setUseDefaultUpdate(false);
        updateSetting(reader);
        return this;
    }
}
