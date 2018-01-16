package com.onyx.android.sdk.scribble.request.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class ClearPageUndoRedoRequest extends BaseNoteRequest {

    public ClearPageUndoRedoRequest() {
        setPauseInputProcessor(false);
        setResumeInputProcessor(true);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.clearPageUndoRedo(getContext());
    }
}
