package com.onyx.android.sdk.scribble.request.shape;

import android.util.Log;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by ming on 12/16/16.
 */
public class ShapeRemoveByGroupIdRequest extends BaseNoteRequest {

    private String groupId;
    public ShapeRemoveByGroupIdRequest(final String groupId) {
        this.groupId = groupId;
        setPauseInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        benchmarkStart();
        helper.getNoteDocument().removeShapesByGroupId(getContext(), groupId);
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
    }

}
