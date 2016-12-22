package com.onyx.kreader.note.request;

import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.kreader.note.NoteManager;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class ChangeStrokeWidthRequest extends ReaderBaseNoteRequest {

    private volatile float newWidth;
    private volatile boolean switchToDrawing;

    public ChangeStrokeWidthRequest(float width, boolean toDrawing) {
        setAbortPendingTasks(false);
        newWidth = width;
        switchToDrawing = toDrawing;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        noteManager.setCurrentStrokeWidth(newWidth);
        if (switchToDrawing && noteManager.isEraser()) {
            noteManager.setCurrentShapeType(NoteDrawingArgs.defaultShape());
        }
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }

}
