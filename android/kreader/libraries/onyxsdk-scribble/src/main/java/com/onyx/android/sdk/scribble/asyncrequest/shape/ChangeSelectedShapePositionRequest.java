package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by solskjaer49 on 2017/8/11 12:08.
 */

public class ChangeSelectedShapePositionRequest extends AsyncBaseNoteRequest {

    public ChangeSelectedShapePositionRequest(float targetDx, float targetDy) {
        this.targetDx = targetDx;
        this.targetDy = targetDy;
        setPauseInputProcessor(true);
    }

    private volatile float targetDx = Float.MIN_VALUE;
    private volatile float targetDy = Float.MIN_VALUE;
    private volatile TouchPoint touchPoint = null;

    public ChangeSelectedShapePositionRequest(TouchPoint touchPoint) {
        this.touchPoint = touchPoint;
        setPauseInputProcessor(true);
    }

    @Override
    public void execute(AsyncNoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        benchmarkStart();
        if ((targetDx == Float.MIN_VALUE || targetDy == Float.MIN_VALUE) && touchPoint != null) {
            targetDx = touchPoint.getX() - helper.getNoteDocument().getCurrentPage(helper.getView().getContext()).getSelectedRect().centerX();
            targetDy = touchPoint.getY() - helper.getNoteDocument().getCurrentPage(helper.getView().getContext()).getSelectedRect().centerY();
        }
        helper.getNoteDocument().getCurrentPage(getContext()).setTranslateToSelectShapeList(targetDx, targetDy);
        renderCurrentPageInBitmap(helper);
        updateShapeDataInfo(helper);
    }
}
