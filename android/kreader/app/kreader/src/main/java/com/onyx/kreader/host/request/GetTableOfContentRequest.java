package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class GetTableOfContentRequest extends BaseReaderRequest {

    public GetTableOfContentRequest() {
    }

    public void execute(final Reader reader) throws Exception {
        createReaderViewInfo();
        getReaderUserDataInfo().loadDocumentTableOfContent(getContext(), reader);
    }
}
