package com.onyx.reader.host.request;

import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ScaleToWidthRequest extends BaseRequest {

    public ScaleToWidthRequest() {
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().scaleToWidth();
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }
}
