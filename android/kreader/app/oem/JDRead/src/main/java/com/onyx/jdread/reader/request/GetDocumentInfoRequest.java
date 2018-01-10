package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class GetDocumentInfoRequest extends ReaderBaseRequest {
    private ReaderUserDataInfo readerUserDataInfo;
    private ReaderDataHolder readerDataHolder;

    public GetDocumentInfoRequest(ReaderUserDataInfo readerUserDataInfo, ReaderDataHolder readerDataHolder) {
        this.readerUserDataInfo = readerUserDataInfo;
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public GetDocumentInfoRequest call() throws Exception {
        ReaderDocument document = readerDataHolder.getReader().getReaderHelper().getDocument();
        String displayName = readerDataHolder.getReader().getReaderHelper().getPlugin().displayName();
        String md5 = readerDataHolder.getReader().getReaderHelper().getDocumentMd5();

        readerUserDataInfo.loadDocumentTableOfContent(JDReadApplication.getInstance().getApplicationContext(), document);
        readerUserDataInfo.loadDocumentAnnotations(JDReadApplication.getInstance().getApplicationContext(), displayName, md5);
        readerUserDataInfo.loadDocumentBookmarks(JDReadApplication.getInstance().getApplicationContext(), displayName, md5);
        return this;
    }

    public ReaderUserDataInfo getResult(){
        return readerUserDataInfo;
    }
}
