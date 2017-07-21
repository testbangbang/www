package com.onyx.android.sdk.scribble.asyncrequest.navigation;


import android.graphics.Bitmap;
import android.graphics.Rect;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

import java.util.List;

/**
 * Created by zengzhu on 4/18/16.
 * load and render shape with scale and offset for specified visible pages.
 */
public class PageListRenderRequest extends AsyncBaseNoteRequest {

    private Bitmap renderBitmap;
    private boolean copyBitmap;

    public PageListRenderRequest(final String id, final List<PageInfo> pages, final Rect size, boolean resume, boolean copyBitmap) {
        this.copyBitmap = copyBitmap;
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
        renderBitmap = copyBitmap ? Bitmap.createBitmap(parent.getRenderBitmap()) : parent.getRenderBitmap();
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
