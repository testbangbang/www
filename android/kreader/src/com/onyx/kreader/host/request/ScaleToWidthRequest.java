package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ScaleToWidthRequest extends BaseRequest {

    private String pageName;

    public ScaleToWidthRequest(final String name) {
        pageName = name;
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().scaleToWidth(pageName);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap());
    }
}
