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

    public boolean isAddMovePoint() {
        return false;
    }

    public boolean hitTest(final float x, final float y, final float radius) {
        float sx = getDownPoint().getX();
        float sy = getDownPoint().getY();
        float ex = getCurrentPoint().getX();
        float ey = getCurrentPoint().getY();
        return ShapeUtils.collide((sx + ex) / 2, (sy + ey) / 2, Math.abs(sx - ex), Math.abs(sy - ey),
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
        applyStrokeStyle(renderContext.paint, getDisplayScale(renderContext));
        renderContext.canvas.drawOval(rect, renderContext.paint);
    }



}
