package com.onyx.reader.host.request;

import com.onyx.reader.api.ReaderDocument;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class CloseRequest extends BaseRequest {

    public void execute(final Reader reader) throws Exception {
        reader.getDocument().close();
        reader.getReaderHelper().onDocumentClosed();
    }
}
