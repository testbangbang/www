package com.onyx.kreader.host.request;

import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.common.BaseRequest;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class ScaleToPageRequest extends BaseRequest {

    public ScaleToPageRequest() {
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().scaleToPage();
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }

}
