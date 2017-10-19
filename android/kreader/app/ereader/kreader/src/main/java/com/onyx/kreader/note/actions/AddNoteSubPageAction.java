package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.request.AddNoteSubPageRequest;
import com.onyx.kreader.note.request.ClearPageRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.ShapeRenderFinishEvent;

/**
 * Created by zhuzeng on 9/28/16.
 */

public class AddNoteSubPageAction extends BaseAction {

    private PageInfo pageInfo;
    private int subPageIndex;

    public AddNoteSubPageAction(final PageInfo pageInfo, final int subPageIndex) {
        this.pageInfo = pageInfo;
        this.subPageIndex = subPageIndex;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final AddNoteSubPageRequest request = new AddNoteSubPageRequest(pageInfo, subPageIndex);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), request, baseCallback);
    }

}
