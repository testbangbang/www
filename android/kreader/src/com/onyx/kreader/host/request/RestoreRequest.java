package com.onyx.kreader.host.request;

import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.options.ReaderOptions;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class RestoreRequest extends BaseRequest {

    private ReaderOptions readerOptions;

    public RestoreRequest(final ReaderOptions options) {
        readerOptions = options;
    }


    public void execute(final Reader reader) throws Exception {
        // save position
        // change layout
        // goto position
    }
}
