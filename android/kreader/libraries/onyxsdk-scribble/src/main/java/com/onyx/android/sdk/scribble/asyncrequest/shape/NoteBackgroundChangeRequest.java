package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by solskjaer49 on 16/7/5 14:50.
 */

public class NoteBackgroundChangeRequest extends AsyncBaseNoteRequest {
    private int bgType;

    public NoteBackgroundChangeRequest(int background , boolean resume) {
        bgType = background;
        setPauseInputProcessor(true);
        setResumeInputProcessor(resume);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.setBackground(bgType);
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
