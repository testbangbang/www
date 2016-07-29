package com.onyx.kreader.ui.actions;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.host.request.GetTableOfContentRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogTableOfContent;

import java.util.List;

public class GetTableOfContentAction extends BaseAction {

    private DialogTableOfContent.DirectoryTab tab;

    public GetTableOfContentAction(DialogTableOfContent.DirectoryTab tab) {
        this.tab = tab;
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        final GetTableOfContentRequest tocRequest = new GetTableOfContentRequest();
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(),tocRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                showTableOfContentDialog(readerDataHolder,
                        tocRequest.getReaderUserDataInfo().getTableOfContent(),
                        tocRequest.getReaderUserDataInfo().getBookmarks(),
                        tocRequest.getReaderUserDataInfo().getAnnotations());
            }
        });
    }

    private void showTableOfContentDialog(final ReaderDataHolder readerDataHolder,
                                          final ReaderDocumentTableOfContent tableOfContent,
                                          final List<Bookmark> bookmarks,
                                          final List<Annotation> annotations) {
        DialogTableOfContent dlg = new DialogTableOfContent(readerDataHolder, tab, tableOfContent, bookmarks, annotations);
        dlg.show();
    }

}
