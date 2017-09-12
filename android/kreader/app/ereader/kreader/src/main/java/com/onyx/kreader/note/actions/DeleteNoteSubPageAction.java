package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.note.request.AddNoteSubPageRequest;
import com.onyx.kreader.note.request.DeleteNoteSubPageRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 9/28/16.
 */

public class DeleteNoteSubPageAction extends BaseAction {

    private String pageName;
    private int subPageIndex;

    public DeleteNoteSubPageAction(final String pageName, final int subPageIndex) {
        this.pageName = pageName;
        this.subPageIndex = subPageIndex + 1;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final DeleteNoteSubPageRequest request = new DeleteNoteSubPageRequest(pageName, subPageIndex);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), request, baseCallback);
    }

}
