package com.onyx.android.sdk.scribble.asyncrequest.navigation;


import android.graphics.Bitmap;
import android.graphics.Rect;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;

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
        setVisiblePages(pages);
        setPauseInputProcessor(true);
        setResumeInputProcessor(resume);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        loadShapeData(noteManager);
        noteManager.getRendererHelper().renderVisiblePagesInBitmap(noteManager, this);
        updateShapeDataInfo(noteManager);
        renderBitmap = copyBitmap ? Bitmap.createBitmap(noteManager.getRenderBitmap()) : noteManager.getRenderBitmap();
    }

    public void loadShapeData(final NoteManager parent) {
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
