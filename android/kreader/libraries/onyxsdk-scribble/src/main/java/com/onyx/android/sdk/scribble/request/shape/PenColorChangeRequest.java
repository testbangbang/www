package com.onyx.android.sdk.scribble.request.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by solskjaer49 on 16/7/5 14:50.
 */

public class PenColorChangeRequest extends BaseNoteRequest {

    private volatile int penColor;

    public PenColorChangeRequest(int color) {
        penColor = color;
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.setStrokeColor(penColor);
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
