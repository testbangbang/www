package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class CloseRequest extends BaseReaderRequest {

    public CloseRequest() {
        setAbortPendingTasks(true);
    }

    public void execute(final Reader reader) throws Exception {
        saveReaderOptions(reader);
        reader.getDocument().close();
        reader.getReaderHelper().onDocumentClosed();
    }
}
