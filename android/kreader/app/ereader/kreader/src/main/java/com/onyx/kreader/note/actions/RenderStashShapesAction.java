package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.request.RenderStashShapesRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 8/30/17.
 */

public class RenderStashShapesAction extends BaseAction {

    private List<PageInfo> pages = new ArrayList<>();

    public RenderStashShapesAction(final List<PageInfo> pages) {
        this.pages.addAll(pages);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(),
                new RenderStashShapesRequest(pages), baseCallback);
    }
}
