package com.onyx.kreader.host.request;

import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.utils.PagePositionUtils;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class GetPageTextRequest extends BaseReaderRequest {

    private int page;
    private String text;

    public GetPageTextRequest(int page) {
    }

    public void execute(final Reader reader) throws Exception {
        text = reader.getDocument().getPageText(PagePositionUtils.fromPageNumber(page));
    }

    public String getText() {
        return text;
    }
}
