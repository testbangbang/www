package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zengzhu on 2/22/16.
 */
public class BackwardRequest extends BaseReaderRequest {

    public BackwardRequest() {
    }

    public void execute(final Reader reader) throws Exception {
        reader.getReaderLayoutManager().goBack();
        drawVisiblePages(reader);
    }

}
