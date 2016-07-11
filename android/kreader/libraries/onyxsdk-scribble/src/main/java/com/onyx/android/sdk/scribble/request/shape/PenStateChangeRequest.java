package com.onyx.android.sdk.scribble.request.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.ShapeManagerOptions;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 7/9/16.
 */
public class PenStateChangeRequest extends BaseNoteRequest {

    private volatile NoteViewHelper.PenState newPenState;

    public PenStateChangeRequest(final NoteViewHelper.PenState state) {
        newPenState = state;
        setPauseInputProcessor(true);
        setResumeInputProcessor(newPenState == NoteViewHelper.PenState.PEN_DRAWING);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.setPenState(newPenState);
    }
}
