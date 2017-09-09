package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;

/**
 * Created by solskjaer49 on 16/7/5 14:50.
 */

public class PenColorChangeRequest extends AsyncBaseNoteRequest {

    private volatile int penColor;

    public PenColorChangeRequest(int color, boolean resume) {
        penColor = color;
        setPauseInputProcessor(true);
        setResumeInputProcessor(resume);
    }

    @Override
    public void execute(final NoteManager parent) throws Exception {
        parent.setStrokeColor(penColor);
        renderCurrentPageInBitmap(parent);
        updateShapeDataInfo(parent);
    }

}
