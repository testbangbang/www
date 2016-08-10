package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 4/20/16.
 */
public class CircleShape extends BaseShape {

    public int getType() {
        return ShapeFactory.SHAPE_CIRCLE;
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
        RectF rect = new RectF(sx, sy, ex, ey);
        applyStrokeStyle(paint);
        canvas.drawOval(rect, paint);
    }



}
