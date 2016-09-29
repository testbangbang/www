package com.onyx.kreader.note.request;

import com.onyx.kreader.note.NoteManager;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class ChangeShapeRequest extends ReaderBaseNoteRequest {

    private volatile int newShape;

    public ChangeShapeRequest(int shape) {
        setAbortPendingTasks(true);
        newShape = shape;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        noteManager.setCurrentShapeType(newShape);
        updateShapeDataInfo(noteManager);
    }
}
