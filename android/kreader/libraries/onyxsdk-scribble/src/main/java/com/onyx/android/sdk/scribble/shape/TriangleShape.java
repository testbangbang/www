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
        float sx = getDownPoint().getX();
        float sy = getDownPoint().getY();
        float ex = getCurrentPoint().getX();
        float ey = getCurrentPoint().getY();
        float cx = Math.abs(2 * sx - ex);
        float cy = ey;
        applyStrokeStyle(paint);
        canvas.drawLine(sx, sy, ex, ey, paint);
        canvas.drawLine(sx, sy, cx, cy, paint);
        canvas.drawLine(cx, cy, ex, ey, paint);
    }

}
