package com.onyx.android.sdk.scribble.shape;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;

import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.MathUtils;

/**
 * Created by zhuzeng on 7/12/16.
 */
public class LineShape extends NonEPDShape {

    public int getType() {
        return ShapeFactory.SHAPE_LINE;
    }

    public boolean isAddMovePoint() {
        return false;
    }

    public boolean hitTest(final float x, final float y, final float radius) {
        final float[] renderPoints = new float[4];
        renderPoints[0] = getDownPoint().x;
        renderPoints[1] = getDownPoint().y;
        renderPoints[2] = getCurrentPoint().x;
        renderPoints[3] = getCurrentPoint().y;
        Matrix transformMatrix = new Matrix();
        if (Float.compare(getOrientation(), 0f) != 0) {
            transformMatrix.setRotate(getOrientation(), getBoundingRect().centerX(), getBoundingRect().centerY());
            transformMatrix.mapPoints(renderPoints);
        }
        return ShapeUtils.hitTestLine(renderPoints[0], renderPoints[1], renderPoints[2], renderPoints[3], x, y, radius);
    }

    public void render(final RenderContext renderContext) {
        float points[] = new float[4];
        points[0] = getDownPoint().getX();
        points[1] = getDownPoint().getY();
        points[2] = getCurrentPoint().getX();
        points[3] = getCurrentPoint().getY();
        if (renderContext.matrix != null) {
            renderContext.matrix.mapPoints(points);
        }
        Matrix transformMatrix = new Matrix();
        Path path = new Path();
        path.moveTo(points[0], points[1]);
        path.lineTo(points[2], points[3]);
        if (Float.compare(getOrientation(), 0f) != 0) {
            PointF centerPoint = MathUtils.calculateMiddlePointFromTwoPoint(points[0], points[1], points[2], points[3]);
            transformMatrix.setRotate(getOrientation(), centerPoint.x,centerPoint.y);
            path.transform(transformMatrix);
        }
        applyStrokeStyle(renderContext.paint, getDisplayScale(renderContext));
        renderContext.canvas.drawPath(path, renderContext.paint);
    }

}
