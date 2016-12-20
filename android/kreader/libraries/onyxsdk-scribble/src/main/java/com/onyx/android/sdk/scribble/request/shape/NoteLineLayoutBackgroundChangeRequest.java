package com.onyx.android.sdk.scribble.request.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by ming on 16/12/9 14:50.
 */

public class NoteLineLayoutBackgroundChangeRequest extends BaseNoteRequest {
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
