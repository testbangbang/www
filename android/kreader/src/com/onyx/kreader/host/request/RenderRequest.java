package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class RenderRequest extends BaseRequest {

    public RenderRequest() {
        super();
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().drawVisiblePages(reader.getReaderHelper().getRenderBitmap());
    }

}
