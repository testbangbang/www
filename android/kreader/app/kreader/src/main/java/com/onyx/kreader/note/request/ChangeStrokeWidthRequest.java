package com.onyx.kreader.note.request;

import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.kreader.note.NoteManager;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class ChangeStrokeWidthRequest extends ReaderBaseNoteRequest {

    private volatile float newWidth;

    public ChangeStrokeWidthRequest(float width) {
        setAbortPendingTasks(true);
        setPauseRawInputProcessor(true);
        newWidth = width;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
        ensureDocumentOpened(noteManager);
        noteManager.setCurrentStrokeWidth(newWidth);
        updateShapeDataInfo(noteManager);
    }

}
