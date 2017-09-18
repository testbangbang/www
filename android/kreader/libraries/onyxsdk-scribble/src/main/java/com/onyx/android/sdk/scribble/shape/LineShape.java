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
        return ShapeUtils.hitTestLine(getDownPoint().x, getDownPoint().y, getCurrentPoint().x, getCurrentPoint().y, x, y, radius);
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
        if (getOrientation() != 0) {
            PointF centerPoint = MathUtils.calculateMiddlePointFromTwoPoint(points[0], points[1], points[2], points[3]);
            transformMatrix.setRotate(getOrientation(), centerPoint.x,centerPoint.y);
            path.transform(transformMatrix);
        }
        applyStrokeStyle(renderContext.paint, getDisplayScale(renderContext));
        renderContext.canvas.drawPath(path, renderContext.paint);
    }

}
