package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PageNextRequest extends AsyncBaseNoteRequest {

    public PageNextRequest() {
        setPauseInputProcessor(true);
    }

    // always render page.
    public void execute(final NoteManager noteManager) throws Exception {
        setResumeInputProcessor(noteManager.useDFBForCurrentState());
        noteManager.getNoteDocument().nextPage();
        renderCurrentPageInBitmap(noteManager);
        updateShapeDataInfo(noteManager);
    }


}
