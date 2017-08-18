package com.onyx.android.sdk.scribble.asyncrequest.shape;

import android.graphics.PointF;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by solskjaer49 on 2017/8/9 17:30.
 */

public class ChangeSelectedShapeScaleRequest extends AsyncBaseNoteRequest {
    public ChangeSelectedShapeScaleRequest(float scale) {
        scaleSize = scale;
        setPauseInputProcessor(true);
    }

    private volatile float scaleSize = Float.MIN_VALUE;

    public ChangeSelectedShapeScaleRequest(TouchPoint touchPoint) {
        this.touchPoint = touchPoint;
        setPauseInputProcessor(true);
    }

    private volatile TouchPoint touchPoint = null;

    @Override
    public void execute(final AsyncNoteViewHelper parent) throws Exception {
        setResumeInputProcessor(parent.useDFBForCurrentState());
        benchmarkStart();
        RectF originSelectedRect = parent.getNoteDocument().getCurrentPage(getContext()).getSelectedRect();
        PointF originCenterPoint = new PointF(originSelectedRect.centerX(), originSelectedRect.centerY());
        if ((Float.compare(scaleSize, Float.MIN_VALUE) == 0) && touchPoint != null) {
            double newDistance = getTwoPointsDistance(new PointF(touchPoint.getX(), touchPoint.getY()), originCenterPoint);
            double originalDistance = getTwoPointsDistance(new PointF(originSelectedRect.left, originSelectedRect.top), originCenterPoint);
            scaleSize = ((float) newDistance / (float) originalDistance);
        }
        parent.getNoteDocument().getCurrentPage(getContext()).setScaleToSelectShapeList(scaleSize);
        renderCurrentPageInBitmap(parent);
        RectF afterScaleRect = parent.getNoteDocument().getCurrentPage(getContext()).getSelectedRect();
        PointF afterZoomCenterPoint = new PointF(afterScaleRect.centerX(), afterScaleRect.centerY());
        parent.getNoteDocument().getCurrentPage(getContext()).setTranslateToSelectShapeList(
                originCenterPoint.x - afterZoomCenterPoint.x,
                originCenterPoint.y - afterZoomCenterPoint.y);
        renderCurrentPageInBitmap(parent);
        updateShapeDataInfo(parent);
    }

    private float getTwoPointsDistance(PointF point1, PointF point2) {
        return (float) Math.sqrt(Math.pow(point1.x - point2.x, 2) +
                Math.pow(point1.y - point2.y, 2));
    }
}
