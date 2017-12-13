package com.onyx.android.sdk.scribble.asyncrequest.shape;

import android.graphics.PointF;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.utils.MathUtils;

/**
 * Created by solskjaer49 on 2017/8/11 12:08.
 */

public class ChangeSelectedShapeRotationRequest extends AsyncBaseNoteRequest {
    private static final String TAG = ChangeSelectedShapeRotationRequest.class.getSimpleName();

    private volatile float targetRotationAngle = Float.MIN_VALUE;
    private volatile TouchPoint touchPoint = null;

    public ChangeSelectedShapeRotationRequest(float rotationAngle, boolean isAddToHistory) {
        this.targetRotationAngle = rotationAngle;
        this.isAddToHistory = isAddToHistory;
        setPauseInputProcessor(true);
    }

    public ChangeSelectedShapeRotationRequest(TouchPoint touchPoint, boolean isAddToHistory) {
        this.touchPoint = touchPoint;
        this.isAddToHistory = isAddToHistory;
        setPauseInputProcessor(true);
    }

    private volatile boolean isAddToHistory = false;

    @Override
    public void execute(NoteManager manager) throws Exception {
        setResumeInputProcessor(manager.useDFBForCurrentState());
        benchmarkStart();
        manager.getNoteDocument().getCurrentPage(getContext()).saveCurrentSelectShape();
        RectF selectedRect = manager.getNoteDocument().getCurrentPage(
                getContext()).getSelectedRect().getRectF();
        float centerX = selectedRect.centerX();
        float centerY = selectedRect.centerY();
        PointF extendedPoint = manager.getNoteDocument().getCurrentPage(
                getContext()).getRotateExtendPoint();
        if ((Float.compare(targetRotationAngle, Float.MIN_VALUE) == 0 && touchPoint != null)) {
            targetRotationAngle = MathUtils.calculateAngle(new PointF(centerX, centerY),
                    new PointF(extendedPoint.x, extendedPoint.y), new PointF(touchPoint.x, touchPoint.y));
        }
        manager.getNoteDocument().getCurrentPage(getContext()).
                setRotationAngleToSelectShapeList(targetRotationAngle, new PointF(centerX, centerY), isAddToHistory);
        renderCurrentPageInBitmap(manager);
        updateShapeDataInfo(manager);
    }


}
