package com.onyx.android.sdk.scribble.shape;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

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
        Log.e("TAG", "render: " );
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
        if (getOrientation() != 0) {
            transformMatrix.setRotate(getOrientation(), rect.centerX(), rect.centerY());
            path.transform(transformMatrix);
        }
        applyStrokeStyle(renderContext.paint, getDisplayScale(renderContext));
        renderContext.canvas.drawPath(path, renderContext.paint);
    }

//    @Override
//    public RectF getBoundingRect() {
//        float sx = getDownPoint().getX();
//        float sy = getDownPoint().getY();
//        float ex = getCurrentPoint().getX();
//        float ey = getCurrentPoint().getY();
//        RectF boundingRect = new RectF(sx, sy, ex, ey);;
//        Matrix transformMatrix = new Matrix();
//        if (getOrientation() != 0) {
//            transformMatrix.setRotate(getOrientation(), boundingRect.centerX(), boundingRect.centerY());
//            transformMatrix.mapRect(boundingRect);
//        }
//        return boundingRect;
//    }

    @Override
    public void onRotate(final float angle, PointF pointF) {
        Log.d("CircleShape", "angle:" + angle);
        float newAngle = (getOrientation() + 45) % 360;
        setOrientation(newAngle % 360);
    }

}
