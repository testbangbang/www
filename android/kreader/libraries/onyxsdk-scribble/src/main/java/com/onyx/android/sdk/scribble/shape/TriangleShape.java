package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 8/6/16.
 */
public class TriangleShape extends BaseShape {

    private float points[] = new float[6];

    public int getType() {
        return ShapeFactory.SHAPE_TRIANGLE;
    }

    public boolean addMovePoint() {
        return false;
    }

    public boolean hitTest(final float x, final float y, final float radius) {
        return ShapeUtils.hitTestLine(points[0], points[1], points[2], points[3], x, y, radius) ||
                ShapeUtils.hitTestLine(points[0], points[1], points[4], points[5], x, y, radius) ||
                ShapeUtils.hitTestLine(points[4], points[5], points[2], points[3], x, y, radius);
    }

    public void render(final RenderContext renderContext) {
        final float[] renderPoints = updatePoints(renderContext);
        applyStrokeStyle(renderContext.paint, getDisplayScale(renderContext));
        renderContext.canvas.drawLine(renderPoints[0], renderPoints[1], renderPoints[2], renderPoints[3], renderContext.paint);
        renderContext.canvas.drawLine(renderPoints[0], renderPoints[1], renderPoints[4], renderPoints[5], renderContext.paint);
        renderContext.canvas.drawLine(renderPoints[4], renderPoints[5], renderPoints[2], renderPoints[3], renderContext.paint);
    }

    private float[] updatePoints(final RenderContext renderContext) {
        points[0] = getDownPoint().getX();
        points[1] = getDownPoint().getY();
        points[2] = getCurrentPoint().getX();
        points[3] = getCurrentPoint().getY();
        points[4] = Math.abs(2 * points[0] - points[2]);
        points[5] = points[3];
        float result [] = new float[6];
        if (renderContext.matrix != null) {
            renderContext.matrix.mapPoints(result, points);
            return result;
        }
        return points;
    }
}
