package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by joy on 8/10/16.
 */
public class SaveDocumentOptionsRequest extends BaseReaderRequest {
    @Override
    public void execute(Reader reader) throws Exception {
        saveReaderOptions(reader);
    }
}
