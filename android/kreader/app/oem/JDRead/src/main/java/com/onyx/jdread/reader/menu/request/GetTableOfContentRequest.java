package com.onyx.jdread.reader.menu.request;

import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2018/1/30.
 */

public class GetTableOfContentRequest extends ReaderBaseRequest {
    private Reader reader;

    public GetTableOfContentRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public GetTableOfContentRequest call() throws Exception {
        getReaderUserDataInfo();
        getReaderUserDataInfo().loadDocumentTableOfContent(reader.getReaderHelper().getContext(), reader.getReaderHelper().getDocument());
        return this;
    }
}
