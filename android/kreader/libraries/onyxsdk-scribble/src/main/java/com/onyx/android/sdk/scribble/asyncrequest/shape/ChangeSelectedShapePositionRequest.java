package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;

/**
 * Created by solskjaer49 on 2017/8/11 12:08.
 */

public class ChangeSelectedShapePositionRequest extends AsyncBaseNoteRequest {

    public ChangeSelectedShapePositionRequest(float targetDx, float targetDy) {
        this.targetDx = targetDx;
        this.targetDy = targetDy;
        setPauseInputProcessor(true);
    }

    private volatile float targetDx = 0f;
    private volatile float targetDy = 0f;

    @Override
    public void execute(AsyncNoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        benchmarkStart();
        helper.getNoteDocument().getCurrentPage(getContext()).setTranslateToSelectShapeList(targetDx, targetDy);
        renderCurrentPageInBitmap(helper);
        updateShapeDataInfo(helper);
    }
}
