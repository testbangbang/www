package com.onyx.kreader.ui.actions;


import android.app.Dialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.host.request.GetDocumentInfoRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogTableOfContent;

import java.util.List;

public class GetDocumentInfoAction extends BaseAction {
    private DialogTableOfContent.DirectoryTab tab;

    public GetDocumentInfoAction(DialogTableOfContent.DirectoryTab tab) {
        this.tab = tab;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final GetDocumentInfoRequest tocRequest = new GetDocumentInfoRequest();
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(),tocRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                showTableOfContentDialog(readerDataHolder,
                        tocRequest.getReaderUserDataInfo().getTableOfContent(),
                        tocRequest.getReaderUserDataInfo().getBookmarks(),
                        tocRequest.getReaderUserDataInfo().getAnnotations());
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private void showTableOfContentDialog(final ReaderDataHolder readerDataHolder,
                                          final ReaderDocumentTableOfContent tableOfContent,
                                          final List<Bookmark> bookmarks,
                                          final List<Annotation> annotations) {
        Dialog dialog = new DialogTableOfContent(readerDataHolder, tab, tableOfContent, bookmarks, annotations);
        dialog.show();
        readerDataHolder.addActiveDialog(dialog);
    }

}
