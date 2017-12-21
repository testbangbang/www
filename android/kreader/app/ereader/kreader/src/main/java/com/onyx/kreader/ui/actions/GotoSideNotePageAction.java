package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by Joy on 2016/5/24.
 */
public class GotoSideNotePageAction extends BaseAction {

    private int page;

    public GotoSideNotePageAction(int page) {
        this.page = page;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.gotoSideNotePage(page);
        readerDataHolder.redrawPage(callback);
    }
}
