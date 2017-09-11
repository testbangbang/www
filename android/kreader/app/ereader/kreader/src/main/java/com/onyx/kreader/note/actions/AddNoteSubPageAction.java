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

    private String pageName;
    private int subPageIndex;

    public AddNoteSubPageAction(final String pageName, final int subPageIndex) {
        this.pageName = pageName;
        this.subPageIndex = subPageIndex + 1;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        final AddNoteSubPageRequest request = new AddNoteSubPageRequest(pageName, subPageIndex);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(), request, baseCallback);
    }

}
