package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zengzhu on 2/22/16.
 */
public class ForwardRequest extends BaseRequest {

    public ForwardRequest() {
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().goForward();
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap());
    }
}
