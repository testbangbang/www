package com.onyx.android.sdk.scribble.request.navigation;


import android.graphics.*;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

import java.util.List;

/**
 * Created by zengzhu on 4/18/16.
 * load and render shape with scale and offset for specified visible pages.
 */
public class PageListRenderRequest extends BaseNoteRequest {


    public PageListRenderRequest(final String id, final List<PageInfo> pages, final Rect size) {
        setDocUniqueId(id);
        setAbortPendingTasks();
        setViewportSize(size);
        setVisiblePages(pages);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.stop();
        ensureDocumentOpened(parent);
        loadShapeData(parent);
        renderVisiblePages(parent);
        updateShapeDataInfo(parent);
    }

    public void loadShapeData(final NoteViewHelper parent) {
        try {
            parent.getNoteDocument().loadShapePages(getContext(), getVisiblePages());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
