package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;


/**
 * Created by zhuzeng on 12/25/15.
 * remove from memory
 */
public class PageRemoveRequest extends AsyncBaseNoteRequest {

    public PageRemoveRequest() {
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    @Override
    public void execute(final NoteManager noteManager) throws Exception {
        noteManager.getNoteDocument().removePage(getContext(), noteManager.getNoteDocument().getCurrentPageIndex());
        renderCurrentPageInBitmap(noteManager);
        updateShapeDataInfo(noteManager);
    }
}
