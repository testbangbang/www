package com.onyx.kreader.host.request;

import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class GetTableOfContentRequest extends BaseReaderRequest {

    private ReaderDocumentTableOfContent toc = new ReaderDocumentTableOfContent();

    public GetTableOfContentRequest() {
    }

    public void execute(final Reader reader) throws Exception {
        createReaderViewInfo();
        if (reader.getDocument().readTableOfContent(toc)) {
            getReaderUserDataInfo().setTableOfContent(toc);
        }
        getReaderUserDataInfo().loadAnnotations(getContext(), reader);
        getReaderUserDataInfo().loadBookmarks(getContext(), reader);
    }
}
