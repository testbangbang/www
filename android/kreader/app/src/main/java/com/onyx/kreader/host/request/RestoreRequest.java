package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class RestoreRequest extends BaseReaderRequest {

    private BaseOptions baseOptions;

    public RestoreRequest(final BaseOptions options) {
        baseOptions = options;
    }


    public void execute(final Reader reader) throws Exception {
        // saveSnapshot position
        // change layout
        // goto position
    }
}
