package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

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

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.setStrokeColor(penColor);
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
