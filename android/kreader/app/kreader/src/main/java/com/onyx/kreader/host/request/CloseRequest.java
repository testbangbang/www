package com.onyx.kreader.host.request;

import android.util.Log;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class CloseRequest extends BaseReaderRequest {

    public CloseRequest() {
        setAbortPendingTasks();
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        // force save document options before document is closed
        saveReaderOptions(reader);
        reader.getDocument().close();
        reader.getReaderHelper().onDocumentClosed();
    }
}
