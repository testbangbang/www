package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

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
        reader.getBitmapCache().clear();
        drawVisiblePages(reader);
    }
}
