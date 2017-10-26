package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.kreader.note.actions.GetNotePageListAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 16/9/26.
 */
public class GetDocumentInfoChain extends BaseAction {

    private ReaderDocumentTableOfContent tableOfContent;
    private List<Bookmark> bookmarks;
    private List<Annotation> annotations;
    private List<PageInfo> scribblePages;

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        showLoadingDialog(readerDataHolder, R.string.loading);
        final ActionChain actionChain = new ActionChain();
        final GetDocumentInfoAction documentInfoAction = new GetDocumentInfoAction();
        final GetNotePageListAction notePageListAction = new GetNotePageListAction();
        actionChain.addAction(documentInfoAction);
        actionChain.addAction(notePageListAction);
        actionChain.execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                tableOfContent = documentInfoAction.getTableOfContent();
                bookmarks = documentInfoAction.getBookmarks();
                annotations = documentInfoAction.getAnnotations();
                scribblePages = notePageListAction.getScribblePages();
                hideLoadingDialog();
                baseCallback.done(request, e);
            }
        });
    }

    public ReaderDocumentTableOfContent getTableOfContent() {
        return tableOfContent;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public List<PageInfo> getScribblePages() {
        return scribblePages;
    }
}
