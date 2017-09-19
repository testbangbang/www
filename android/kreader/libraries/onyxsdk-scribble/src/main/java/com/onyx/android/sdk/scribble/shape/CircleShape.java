package com.onyx.android.sdk.scribble.shape;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 4/20/16.
 */
public class CircleShape extends NonEPDShape {

    public int getType() {
        return ShapeFactory.SHAPE_CIRCLE;
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
        return ShapeUtils.collide((renderPoints[0] + renderPoints[2]) / 2, (renderPoints[1] + renderPoints[3]) / 2, Math.abs(renderPoints[0] - renderPoints[2]), Math.abs(renderPoints[1] - renderPoints[3]),
                x, y, radius);
    }

    public void render(final RenderContext renderContext) {
        float sx = getDownPoint().getX();
        float sy = getDownPoint().getY();
        float ex = getCurrentPoint().getX();
        float ey = getCurrentPoint().getY();
        RectF rect = new RectF(sx, sy, ex, ey);
        if (renderContext.matrix != null) {
            renderContext.matrix.mapRect(rect);
        }
        Matrix transformMatrix = new Matrix();
        Path path = new Path();
        path.addOval(rect, Path.Direction.CW);
        if (Float.compare(getOrientation(), 0f) != 0) {
            transformMatrix.setRotate(getOrientation(), rect.centerX(), rect.centerY());
            path.transform(transformMatrix);
        }
        applyStrokeStyle(renderContext.paint, getDisplayScale(renderContext));
        renderContext.canvas.drawPath(path, renderContext.paint);
    }

}
