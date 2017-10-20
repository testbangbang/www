package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.request.DeleteNoteSubPageRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 9/28/16.
 */

public class DeleteNoteSubPageAction extends BaseAction {

    private PageInfo pageInfo;
    private int subPageIndex;

    public DeleteNoteSubPageAction(final PageInfo pageInfo, final int subPageIndex) {
        this.pageInfo = pageInfo;
        this.subPageIndex = subPageIndex;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final DeleteNoteSubPageRequest request = new DeleteNoteSubPageRequest(pageInfo, subPageIndex);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), request, baseCallback);
    }

}
