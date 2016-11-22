package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zengzhu on 2/22/16.
 */
public class ChangeStyleRequest extends BaseReaderRequest {

    private ReaderTextStyle readerTextStyle;

    public ChangeStyleRequest(final ReaderTextStyle style) {
        readerTextStyle = style;
    }

    public void execute(final Reader reader) throws Exception {
        reader.getReaderLayoutManager().setStyle(readerTextStyle);
        drawVisiblePages(reader);
    }
}
