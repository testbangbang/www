package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;

/**
 * Created by solskjaer49 on 16/7/5 14:51.
 */

public class PenWidthChangeRequest extends AsyncBaseNoteRequest {
    private volatile float penWidth;

    public PenWidthChangeRequest(float pw) {
        penWidth = pw;
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    @Override
    public void execute(final NoteManager noteManager) throws Exception {
        noteManager.setStrokeWidth(penWidth);
        renderCurrentPageInBitmap(noteManager);
        updateShapeDataInfo(noteManager);
    }
}
