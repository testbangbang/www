package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;

/**
 * Created by solskjaer49 on 2017/8/9 17:30.
 */

public class ChangeSelectedShapeScaleRequest extends AsyncBaseNoteRequest {
    public ChangeSelectedShapeScaleRequest(float scale) {
        scaleSize = scale;
    }

    private volatile float scaleSize = 1.0f;

    @Override
    public void execute(final AsyncNoteViewHelper parent) throws Exception {
        parent.getNoteDocument().getCurrentPage(getContext()).setScaleToSelectShapeList(scaleSize);
    }
}
