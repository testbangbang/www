package com.onyx.edu.reader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.request.RenderShapeListInBackgroundRequest;
import com.onyx.edu.reader.ui.actions.BaseAction;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

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

        final RenderShapeListInBackgroundRequest request = new RenderShapeListInBackgroundRequest(pages, stash);
        readerDataHolder.getNoteManager().submit(readerDataHolder.getContext(),
                request, baseCallback);
    }
}
