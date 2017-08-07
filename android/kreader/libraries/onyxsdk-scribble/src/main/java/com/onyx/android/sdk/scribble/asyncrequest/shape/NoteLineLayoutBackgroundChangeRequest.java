package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;

/**
 * Created by ming on 16/12/9 14:50.
 */

public class NoteLineLayoutBackgroundChangeRequest extends AsyncBaseNoteRequest {
    private int bgType;

    public NoteLineLayoutBackgroundChangeRequest(int background , boolean resume) {
        bgType = background;
        setPauseInputProcessor(true);
        setResumeInputProcessor(resume);
    }

    @Override
    public void execute(final AsyncNoteViewHelper parent) throws Exception {
        parent.setLineLayoutBackground(bgType);
        renderCurrentPageInBitmap(parent);
        updateShapeDataInfo(parent);
    }

}
