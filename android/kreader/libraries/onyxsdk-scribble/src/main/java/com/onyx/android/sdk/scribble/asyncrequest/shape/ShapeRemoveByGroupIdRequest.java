package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;

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
    public void execute(final AsyncNoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState() && resume);
        helper.getNoteDocument().removeShapesByGroupId(getContext(), groupId);
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
