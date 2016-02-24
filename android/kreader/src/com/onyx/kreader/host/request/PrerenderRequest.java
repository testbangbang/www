package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/16/16.
 */
public class PrerenderRequest extends BaseRequest {

    private boolean forward;

    public PrerenderRequest(boolean next) {
        super();
        forward = next;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        if (forward && reader.getReaderLayoutManager().nextScreen()) {
            reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap());
            reader.getReaderLayoutManager().prevScreen();
        } else if (!forward && reader.getReaderLayoutManager().prevScreen()) {
            reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap());
            reader.getReaderLayoutManager().nextScreen();
        }
    }
}
