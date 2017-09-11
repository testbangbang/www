package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;

/**
 * Created by ming on 12/16/16.
 */
public class ShapeRemoveByGroupIdRequest extends AsyncBaseNoteRequest {

    private String groupId;
    private boolean resume;

    public ShapeRemoveByGroupIdRequest(final String groupId, final boolean resume) {
        this.groupId = groupId;
        this.resume = resume;
        setPauseInputProcessor(true);
    }

    @Override
    public void execute(final NoteManager noteManager) throws Exception {
        setResumeInputProcessor(noteManager.useDFBForCurrentState() && resume);
        noteManager.getNoteDocument().removeShapesByGroupId(getContext(), groupId);
        renderCurrentPageInBitmap(noteManager);
        updateShapeDataInfo(noteManager);
    }

}
