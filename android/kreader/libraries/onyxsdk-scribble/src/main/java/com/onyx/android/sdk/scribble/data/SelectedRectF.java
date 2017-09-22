package com.onyx.android.sdk.scribble.data;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by solskjaer49 on 2017/9/15 18:31.
 */

public class SelectedRectF {
    private final int detectionRange = 10;

    public ShapeTransformAction getTouchPointHitTest(PointF touchPoint) {
        Matrix pointRevertMatrix = new Matrix();
        float[] pointArray = new float[2];
        pointArray[0] = touchPoint.x;
        pointArray[1] = touchPoint.y;
        //TODO:A little trick here, because no rotated rect detect in android,
        // so we just rotate back the point to 0 degrees to do the detection.
        if (Float.compare(orientation, 0f) == 1) {
            pointRevertMatrix.setRotate(360 - orientation, rectF.centerX(), rectF.centerY());
            pointRevertMatrix.mapPoints(pointArray);
        }
        List<RectF> zoomDetectRectList = buildZoomDetectionRectList();
        for (RectF rectF : zoomDetectRectList) {
            if (rectF.contains(pointArray[0], pointArray[1])) {
                return ShapeTransformAction.Zoom;
            }
        }

        float rotationHandlerRectTop = Float.compare(rectF.top - 50 - detectionRange, 0) < 0
                ? 0f : rectF.top - 50 - detectionRange;
        float rotationHandlerRectBottom = Float.compare(rotationHandlerRectTop, 0) < 0
                ? detectionRange : rotationHandlerRectTop + 2 * detectionRange;
        RectF rotationHandlerRect = new RectF(rectF.centerX() - detectionRange, rotationHandlerRectTop,
                rectF.centerX() + detectionRange, rotationHandlerRectBottom);
        return rotationHandlerRect.contains(pointArray[0], pointArray[1]) ?
                ShapeTransformAction.Rotation : ShapeTransformAction.Move;
    }

    public float getOrientation() {
        return orientation;
    }

    public SelectedRectF setOrientation(float orientation) {
        this.orientation = orientation;
        return this;
    }

    public RectF getRectF() {
        return rectF;
    }

    public SelectedRectF setRectF(RectF rectF) {
        this.rectF = rectF;
        return this;
    }

    public SelectedRectF(float orientation, RectF rectF) {
        this.orientation = orientation;
        this.rectF = rectF;
    }

    private List<RectF> buildZoomDetectionRectList() {
        //TODO:dynamic detectionRange By selectRect width/height?
        List<RectF> resultRectFList = new ArrayList<>();
        RectF leftTopCornerRect = new RectF(rectF.left - detectionRange, rectF.top - detectionRange,
                rectF.right + detectionRange, rectF.top + detectionRange);
        RectF rightTopCornerRect = new RectF(rectF.right - detectionRange, rectF.top - detectionRange,
                rectF.right + detectionRange, rectF.top + detectionRange);
        RectF leftBottomCornerRect = new RectF(rectF.left - detectionRange, rectF.bottom - detectionRange,
                rectF.right + detectionRange, rectF.bottom + detectionRange);
        RectF rightBottomCornerRect = new RectF(rectF.right - detectionRange, rectF.bottom - detectionRange,
                rectF.right + detectionRange, rectF.bottom + detectionRange);
        resultRectFList.add(leftTopCornerRect);
        resultRectFList.add(rightTopCornerRect);
        resultRectFList.add(leftBottomCornerRect);
        resultRectFList.add(rightBottomCornerRect);
        return resultRectFList;
    }

    private float orientation = 0f;

    public SelectedRectF(RectF rectF) {
        this(0, rectF);
    }

    private RectF rectF = new RectF();
}
