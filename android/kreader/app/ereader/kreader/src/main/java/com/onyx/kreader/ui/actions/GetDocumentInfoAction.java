package com.onyx.kreader.ui.actions;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.host.request.GetDocumentInfoRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;

public class GetDocumentInfoAction extends BaseAction {

    private ReaderDocumentTableOfContent tableOfContent;
    private List<Bookmark> bookmarks;
    private List<Annotation> annotations;

    public ReaderDocumentTableOfContent getTableOfContent() {
        return tableOfContent;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final GetDocumentInfoRequest getDocumentInfoRequest = new GetDocumentInfoRequest();
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), getDocumentInfoRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                tableOfContent = getDocumentInfoRequest.getReaderUserDataInfo().getTableOfContent();
                bookmarks = getDocumentInfoRequest.getReaderUserDataInfo().getBookmarks();
                annotations = getDocumentInfoRequest.getReaderUserDataInfo().getAnnotations();
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
