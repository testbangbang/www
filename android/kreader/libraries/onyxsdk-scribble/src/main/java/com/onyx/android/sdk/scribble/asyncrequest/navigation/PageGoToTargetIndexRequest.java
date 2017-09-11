package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageGoToTargetIndexRequest extends AsyncBaseNoteRequest {
    private int targetIndex;
    private boolean resume;

    public PageGoToTargetIndexRequest(int index,boolean r) {
        targetIndex = index;
        setPauseInputProcessor(true);
        resume = r;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        setResumeInputProcessor(resume && noteManager.useDFBForCurrentState());
        noteManager.getNoteDocument().gotoPage(targetIndex);
        renderCurrentPageInBitmap(noteManager);
        updateShapeDataInfo(noteManager);
    }

}
