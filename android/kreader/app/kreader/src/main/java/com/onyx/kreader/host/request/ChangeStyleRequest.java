package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.options.ReaderStyle;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zengzhu on 2/22/16.
 */
public class ChangeStyleRequest extends BaseReaderRequest {

    private ReaderStyle readerStyle;

    public ChangeStyleRequest(final ReaderStyle style) {
        readerStyle = style;
    }

    public void execute(final Reader reader) throws Exception {
        prepareRenderBitmap(reader);
        reader.getReaderLayoutManager().setStyle(readerStyle);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }
}
