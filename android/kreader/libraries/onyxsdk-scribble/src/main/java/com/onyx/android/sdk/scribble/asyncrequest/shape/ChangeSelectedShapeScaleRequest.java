package com.onyx.android.sdk.scribble.asyncrequest.shape;

import android.graphics.PointF;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by solskjaer49 on 2017/8/9 17:30.
 */

public class ChangeSelectedShapeScaleRequest extends AsyncBaseNoteRequest {
    private volatile float scaleSize = Float.MIN_VALUE;

    public ChangeSelectedShapeScaleRequest(TouchPoint touchPoint, boolean isAddToHistory) {
        this.isAddToHistory = isAddToHistory;
        this.touchPoint = touchPoint;
        setPauseInputProcessor(true);
    }

    public ChangeSelectedShapeScaleRequest(float scaleSize, boolean isAddToHistory) {
        this.scaleSize = scaleSize;
        this.isAddToHistory = isAddToHistory;
        setPauseInputProcessor(true);
    }

    private volatile boolean isAddToHistory = false;

    private volatile TouchPoint touchPoint = null;

    @Override
    public void execute(final NoteManager manager) throws Exception {
        setResumeInputProcessor(manager.useDFBForCurrentState());
        benchmarkStart();
        manager.getNoteDocument().getCurrentPage(getContext()).saveCurrentSelectShape();
        RectF originSelectedRect = manager.getNoteDocument().getCurrentPage(getContext()).getSelectedRect().getRectF();
        PointF originCenterPoint = new PointF(originSelectedRect.centerX(), originSelectedRect.centerY());
        if ((Float.compare(scaleSize, Float.MIN_VALUE) == 0) && touchPoint != null) {
            double newDistance = getTwoPointsDistance(new PointF(touchPoint.getX(), touchPoint.getY()), originCenterPoint);
            double originalDistance = getTwoPointsDistance(new PointF(originSelectedRect.left, originSelectedRect.top), originCenterPoint);
            scaleSize = ((float) newDistance / (float) originalDistance);
        }
        manager.getNoteDocument().getCurrentPage(getContext()).setScaleToSelectShapeList(scaleSize, false);
        renderCurrentPageInBitmap(manager);
        RectF afterScaleRect = manager.getNoteDocument().getCurrentPage(getContext()).getSelectedRect().getRectF();
        PointF afterZoomCenterPoint = new PointF(afterScaleRect.centerX(), afterScaleRect.centerY());
        manager.getNoteDocument().getCurrentPage(getContext()).setTranslateToSelectShapeList(
                originCenterPoint.x - afterZoomCenterPoint.x,
                originCenterPoint.y - afterZoomCenterPoint.y, isAddToHistory);
        renderCurrentPageInBitmap(manager);
        updateShapeDataInfo(manager);
    }

    private float getTwoPointsDistance(PointF point1, PointF point2) {
        return (float) Math.sqrt(Math.pow(point1.x - point2.x, 2) +
                Math.pow(point1.y - point2.y, 2));
    }
}
