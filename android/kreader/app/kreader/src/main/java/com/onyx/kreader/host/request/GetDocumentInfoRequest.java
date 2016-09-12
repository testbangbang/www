package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class GetDocumentInfoRequest extends BaseReaderRequest {

    public GetDocumentInfoRequest() {
    }

    public void execute(final Reader reader) throws Exception {
        createReaderViewInfo();
        getReaderUserDataInfo().loadDocumentTableOfContent(getContext(), reader);
        getReaderUserDataInfo().loadDocumentAnnotations(getContext(), reader);
        getReaderUserDataInfo().loadDocumentBookmarks(getContext(), reader);
    }
}
