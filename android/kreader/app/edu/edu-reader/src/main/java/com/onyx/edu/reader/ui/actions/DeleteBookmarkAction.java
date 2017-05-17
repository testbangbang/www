package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.host.request.DeleteBookmarkRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 16/7/15.
 */
public class DeleteBookmarkAction extends BaseAction {

    private Bookmark mBookmark;

    public DeleteBookmarkAction(Bookmark bookmark) {
        this.mBookmark = bookmark;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.submitRenderRequest(new DeleteBookmarkRequest(mBookmark), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }
}
