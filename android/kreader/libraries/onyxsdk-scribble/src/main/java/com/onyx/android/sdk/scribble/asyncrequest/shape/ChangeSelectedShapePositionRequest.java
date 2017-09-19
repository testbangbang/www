package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by solskjaer49 on 2017/8/11 12:08.
 */

public class ChangeSelectedShapePositionRequest extends AsyncBaseNoteRequest {

    private volatile float targetDx = Float.MIN_VALUE;
    private volatile float targetDy = Float.MIN_VALUE;
    private volatile TouchPoint touchPoint = null;

    public ChangeSelectedShapePositionRequest(float targetDx, float targetDy, boolean isAddToHistory) {
        this.targetDx = targetDx;
        this.targetDy = targetDy;
        this.isAddToHistory = isAddToHistory;
        setPauseInputProcessor(true);
    }

    public ChangeSelectedShapePositionRequest(TouchPoint touchPoint, boolean isAddToHistory) {
        this.touchPoint = touchPoint;
        this.isAddToHistory = isAddToHistory;
        setPauseInputProcessor(true);
    }

    private volatile boolean isAddToHistory = false;

    @Override
    public void execute(NoteManager helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        benchmarkStart();
        helper.getNoteDocument().getCurrentPage(getContext()).saveCurrentSelectShape();
        if ((Float.compare(targetDx, Float.MIN_VALUE) == 0 || (Float.compare(targetDy, Float.MIN_VALUE) == 0)) && touchPoint != null) {
            targetDx = touchPoint.getX() - helper.getNoteDocument().getCurrentPage(
                    getContext()).getSelectedRect().getRectF().centerX();
            targetDy = touchPoint.getY() - helper.getNoteDocument().getCurrentPage(
                    getContext()).getSelectedRect().getRectF().centerY();
        }
        helper.getNoteDocument().getCurrentPage(getContext()).setTranslateToSelectShapeList(targetDx, targetDy, isAddToHistory);
        renderCurrentPageInBitmap(helper);
        updateShapeDataInfo(helper);
    }
}
