package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

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

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.setLineLayoutBackground(bgType);
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
