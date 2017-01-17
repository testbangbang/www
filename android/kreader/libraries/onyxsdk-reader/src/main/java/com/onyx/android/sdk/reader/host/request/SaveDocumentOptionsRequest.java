package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by joy on 8/10/16.
 */
public class SaveDocumentOptionsRequest extends BaseReaderRequest {
    @Override
    public void execute(Reader reader) throws Exception {
        saveReaderOptions(reader);
    }
}
