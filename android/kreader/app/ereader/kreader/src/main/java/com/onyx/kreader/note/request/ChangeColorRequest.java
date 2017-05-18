package com.onyx.kreader.note.request;

import com.onyx.android.sdk.scribble.EPDRenderer;
import com.onyx.kreader.note.NoteManager;

/**
 * Created by zhuzeng on 26/11/2016.
 */

public class ChangeColorRequest extends ReaderBaseNoteRequest {

    private volatile int newColor;

    public ChangeColorRequest(int color) {
        setRender(false);
        setTransfer(false);
        newColor = color;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        noteManager.setCurrentShapeColor(newColor);
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }

}
