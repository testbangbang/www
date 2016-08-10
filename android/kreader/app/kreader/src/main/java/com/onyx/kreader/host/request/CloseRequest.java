package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class CloseRequest extends BaseReaderRequest {

    public CloseRequest() {
        setAbortPendingTasks(true);
    }

    public void execute(final Reader reader) throws Exception {
        reader.getDocument().close();
        reader.getReaderHelper().onDocumentClosed();
    }
}
