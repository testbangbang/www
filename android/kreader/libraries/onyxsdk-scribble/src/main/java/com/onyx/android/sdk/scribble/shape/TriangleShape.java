package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by zhuzeng on 8/6/16.
 */
public class TriangleShape extends BaseShape {

    public int getType() {
        return ShapeFactory.SHAPE_TRIANGLE;
    }

    public boolean addMovePoint() {
        return false;
    }

    public boolean hitTest(final float x, final float y, final float radius) {
        return false;
    }

    public void render(final Canvas canvas, final Paint paint, final Matrix matrix) {
        float points[] = new float[6];
        points[0] = getDownPoint().getX();
        points[1] = getDownPoint().getY();
        points[2] = getCurrentPoint().getX();
        points[3] = getCurrentPoint().getY();
        points[4] = Math.abs(2 * points[0] - points[2]);
        points[5] = points[3];
        applyStrokeStyle(paint);
        if (matrix != null) {
            matrix.mapPoints(points);
        }
        canvas.drawLine(points[0], points[1], points[2], points[3], paint);
        canvas.drawLine(points[0], points[1], points[4], points[5], paint);
        canvas.drawLine(points[4], points[5], points[2], points[3], paint);
    }

}
