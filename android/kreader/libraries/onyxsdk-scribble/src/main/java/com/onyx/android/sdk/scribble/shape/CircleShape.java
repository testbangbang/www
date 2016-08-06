package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
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
        float cx = (sx + ex) / 2;
        float cy = (sy + ey) / 2;
        float radius = Math.min(Math.abs(ex - sx) / 2, Math.abs(ey - sy) / 2);
        applyStrokeStyle(paint);
        canvas.drawCircle(cx, cy, radius, paint);
    }



}
