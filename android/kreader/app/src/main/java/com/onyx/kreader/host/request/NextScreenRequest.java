package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class NextScreenRequest extends BaseRequest {

    public NextScreenRequest() {

    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().nextScreen();
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }
}
