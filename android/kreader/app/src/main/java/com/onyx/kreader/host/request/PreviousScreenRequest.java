package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/16/16.
 */
public class PreviousScreenRequest extends BaseReaderRequest {

    public PreviousScreenRequest() {
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().prevScreen();
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }
}
