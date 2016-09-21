package com.onyx.kreader.note.request;

import android.graphics.Rect;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.NoteManager;

import java.util.List;

/**
 * Created by zhuzeng on 9/15/16.
 */
public class ReaderNoteRenderRequest extends ReaderBaseNoteRequest {

    public ReaderNoteRenderRequest(final String id, final List<PageInfo> pages, final Rect size) {
        setDocUniqueId(id);
        setAbortPendingTasks(true);
        setViewportSize(size);
        setVisiblePages(pages);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        updateEventProcessor(noteManager);
        ensureDocumentOpened(noteManager);
        loadShapeData(noteManager);
        updateEventProcessor(noteManager);
        getShapeDataInfo().setContentRendered(renderVisiblePages(noteManager));
        updateShapeDataInfo(noteManager);
    }

    public void loadShapeData(final NoteManager parent) {
        try {
            parent.getNoteDocument().loadPages(getContext(), getVisiblePages());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEventProcessor(final NoteManager noteManager) {
        noteManager.setVisiblePages(getVisiblePages());
    }
}
