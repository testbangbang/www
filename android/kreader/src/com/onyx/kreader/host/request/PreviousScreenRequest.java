package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/16/16.
 */
public class PreviousScreenRequest extends BaseRequest {

    public PreviousScreenRequest() {

    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().prevScreen();
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }
}
