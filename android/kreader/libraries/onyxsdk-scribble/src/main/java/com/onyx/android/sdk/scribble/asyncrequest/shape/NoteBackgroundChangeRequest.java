package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;

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

    @Override
    public void execute(final NoteManager noteManager) throws Exception {
        noteManager.setBackground(bgType);
        renderCurrentPageInBitmap(noteManager);
        updateShapeDataInfo(noteManager);
    }

}
