package com.onyx.kreader.note.request;

import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.kreader.note.NoteManager;

/**
 * Created by zhuzeng on 10/9/16.
 */

public class RestoreShapeRequest extends ReaderBaseNoteRequest {

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        noteManager.restoreCurrentShapeType();
        noteManager.startRawEventProcessor();
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }

}
