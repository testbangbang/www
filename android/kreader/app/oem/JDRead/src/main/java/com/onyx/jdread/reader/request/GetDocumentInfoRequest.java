package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class GetDocumentInfoRequest extends ReaderBaseRequest {

    public GetDocumentInfoRequest(Reader reader) {
        super(reader);
    }

    @Override
    public GetDocumentInfoRequest call() throws Exception {
        ReaderDocument document = getReader().getReaderHelper().getDocument();
        String displayName = getReader().getReaderHelper().getPlugin().displayName();
        String md5 = getReader().getReaderHelper().getDocumentMd5();

        getReaderUserDataInfo().loadDocumentTableOfContent(getReader().getReaderHelper().getContext(), document);
        getReaderUserDataInfo().loadDocumentAnnotations(getReader().getReaderHelper().getContext(), displayName, md5);
        getReaderUserDataInfo().loadDocumentBookmarks(getReader().getReaderHelper().getContext(), displayName, md5);
        return this;
    }
}
