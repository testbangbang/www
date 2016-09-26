package com.onyx.kreader.ui.actions;

import android.app.Dialog;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.kreader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.note.actions.GetNotePageListAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogTableOfContent;

import java.util.List;

/**
 * Created by ming on 16/9/26.
 */
public class GetDocumentInfoChain extends BaseAction {

    private DialogTableOfContent.DirectoryTab tab;

    public GetDocumentInfoChain(DialogTableOfContent.DirectoryTab tab) {
        this.tab = tab;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        final ActionChain actionChain = new ActionChain();
        final GetDocumentInfoAction documentInfoAction = new GetDocumentInfoAction();
        final GetNotePageListAction notePageListAction = new GetNotePageListAction();
        actionChain.addAction(documentInfoAction);
        actionChain.addAction(notePageListAction);
        actionChain.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                showTableOfContentDialog(readerDataHolder, documentInfoAction.getTableOfContent(),
                        documentInfoAction.getBookmarks(),
                        documentInfoAction.getAnnotations(),
                        notePageListAction.getScribblePages());
            }
        });
    }

    private void showTableOfContentDialog(final ReaderDataHolder readerDataHolder,
                                          final ReaderDocumentTableOfContent tableOfContent,
                                          final List<Bookmark> bookmarks,
                                          final List<Annotation> annotations,
                                          final List<String> scribblePages) {
        Dialog dialog = new DialogTableOfContent(readerDataHolder, tab, tableOfContent, bookmarks, annotations, scribblePages);
        dialog.show();
        readerDataHolder.addActiveDialog(dialog);
    }
}
