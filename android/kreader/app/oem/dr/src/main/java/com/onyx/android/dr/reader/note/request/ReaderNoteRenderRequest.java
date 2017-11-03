package com.onyx.android.dr.reader.note.request;

import android.graphics.Rect;

import com.onyx.android.dr.reader.note.NoteManager;
import com.onyx.android.sdk.data.PageInfo;

import java.util.List;

/**
 * Created by zhuzeng on 9/15/16.
 */
public class ReaderNoteRenderRequest extends ReaderBaseNoteRequest {

    public ReaderNoteRenderRequest(final String id, final List<PageInfo> pages, final Rect size, boolean abortPending) {
        setDocUniqueId(id);
        setAbortPendingTasks(abortPending);
        setViewportSize(size);
        setVisiblePages(pages);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        updateEventProcessor(noteManager);
        ensureDocumentOpened(noteManager);
        loadShapeData(noteManager);
        updateEventProcessor(noteManager);
        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
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
