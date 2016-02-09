package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class CloseRequest extends BaseRequest {

    public void execute(final Reader reader) throws Exception {
        reader.getDocument().close();
        reader.getReaderHelper().onDocumentClosed();
    }
}
