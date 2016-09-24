package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNotePage;

import java.util.List;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class ChangeShapeRequest extends ReaderBaseNoteRequest {

    private volatile int newShape;

    public ChangeShapeRequest(int shape) {
        setAbortPendingTasks(true);
        setPauseRawInputProcessor(true);
        newShape = shape;
        setResumeRawInputProcessor(ShapeFactory.isDFBShape(newShape));
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        noteManager.setCurrentShapeType(newShape);
        updateShapeDataInfo(noteManager);
    }
}
