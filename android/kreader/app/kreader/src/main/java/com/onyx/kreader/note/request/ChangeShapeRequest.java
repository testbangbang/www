package com.onyx.kreader.note.request;

import com.onyx.android.sdk.scribble.shape.ShapeFactory;
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
        onShapeChanged(noteManager, newShape);
        updateShapeDataInfo(noteManager);
    }

    private void onShapeChanged(final NoteManager noteManager, int shape) {
        if (ShapeFactory.isDFBShape(shape)) {
            noteManager.getNoteEventProcessorManager().resume();
        } else {
            noteManager.getNoteEventProcessorManager().pause();
        }
    }
}
