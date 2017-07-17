package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

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

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState() && resume);
        helper.getNoteDocument().removeShapesByGroupId(getContext(), groupId);
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
