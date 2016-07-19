package com.onyx.kreader.ui.actions;

import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.host.request.DeleteBookmarkRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by ming on 16/7/15.
 */
public class DeleteBookmarkAction extends BaseAction {

    private Bookmark mBookmark;

    public DeleteBookmarkAction(Bookmark bookmark) {
        this.mBookmark = bookmark;
    }

    @Override
    public void execute(ReaderActivity readerActivity) {
        readerActivity.submitRequest(new DeleteBookmarkRequest(mBookmark));
    }
}
