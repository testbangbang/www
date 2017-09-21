package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.request.FlushShapeListRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 8/30/17.
 */

public class RenderStashShapesInBackgroundAction extends BaseAction {

    private List<PageInfo> pages = new ArrayList<>();

    public RenderStashShapesInBackgroundAction(final List<PageInfo> pages) {
        this.pages.addAll(pages);
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        final NoteManager noteManager = readerDataHolder.getNoteManager();
        final List<Shape> stash = noteManager.detachShapeStash();

        final FlushShapeListRequest flushRequest = new FlushShapeListRequest(pages, stash, 0, true, true, false);
        flushRequest.setRenderToScreen(false);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(),
                flushRequest, baseCallback);
    }
}
