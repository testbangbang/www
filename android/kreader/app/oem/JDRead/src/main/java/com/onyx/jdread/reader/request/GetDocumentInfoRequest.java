package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class GetDocumentInfoRequest extends ReaderBaseRequest {
    private Reader reader;

    public GetDocumentInfoRequest(Reader reader) {
        this.reader = reader;
    }

    @Override
    public GetDocumentInfoRequest call() throws Exception {
        ReaderDocument document = reader.getReaderHelper().getDocument();
        String displayName = reader.getReaderHelper().getPlugin().displayName();
        String md5 = reader.getReaderHelper().getDocumentMd5();

        getReaderUserDataInfo().loadDocumentTableOfContent(JDReadApplication.getInstance().getApplicationContext(), document);
        getReaderUserDataInfo().loadDocumentAnnotations(JDReadApplication.getInstance().getApplicationContext(), displayName, md5);
        getReaderUserDataInfo().loadDocumentBookmarks(JDReadApplication.getInstance().getApplicationContext(), displayName, md5);
        return this;
    }
}
