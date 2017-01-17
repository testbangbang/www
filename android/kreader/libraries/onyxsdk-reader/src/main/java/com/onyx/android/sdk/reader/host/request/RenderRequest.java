package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 * redraw request
 */
public class RenderRequest extends BaseReaderRequest {

    public RenderRequest() {
        super();
    }

    public void execute(final Reader reader) throws Exception {
        drawVisiblePages(reader);
    }

}
