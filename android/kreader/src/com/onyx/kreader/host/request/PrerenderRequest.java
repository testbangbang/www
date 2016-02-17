package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/16/16.
 */
public class PrerenderRequest extends BaseRequest {

    public PrerenderRequest() {
        super();
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        if (reader.getReaderLayoutManager().nextScreen()) {
            reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
            reader.getReaderLayoutManager().prevScreen();
        }
    }
}
