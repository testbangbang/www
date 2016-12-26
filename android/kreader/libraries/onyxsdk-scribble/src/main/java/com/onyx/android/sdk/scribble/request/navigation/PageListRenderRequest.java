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

    private Bitmap renderBitmap;

    public PageListRenderRequest(final String id, final List<PageInfo> pages, final Rect size, boolean resume) {
        setDocUniqueId(id);
        setAbortPendingTasks(true);
        setViewportSize(size);
        setVisiblePages(pages);
        setPauseInputProcessor(true);
        setResumeInputProcessor(resume);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        ensureDocumentOpened(parent);
        loadShapeData(parent);
        renderVisiblePages(parent);
        updateShapeDataInfo(parent);
        renderBitmap = parent.getRenderBitmap();
    }

    public void loadShapeData(final NoteViewHelper parent) {
        try {
            parent.getNoteDocument().loadShapePages(getContext(), getVisiblePages());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getRenderBitmap() {
        return renderBitmap;
    }
}
