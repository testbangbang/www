package com.onyx.android.sdk.scribble.shape;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 4/25/16.
 */
public class RectangleShape extends NonEPDShape {

    public int getType() {
        return ShapeFactory.SHAPE_RECTANGLE;
    }

    public boolean isAddMovePoint() {
        return false;
    }

    public boolean hitTest(final float x, final float y, final float radius) {
        return ShapeUtils.hitTestLine(getDownPoint().x, getDownPoint().y, getDownPoint().x, getCurrentPoint().y, x, y, radius) ||
                ShapeUtils.hitTestLine(getDownPoint().x, getDownPoint().y, getCurrentPoint().x, getDownPoint().y, x, y, radius) ||
                ShapeUtils.hitTestLine(getDownPoint().x, getCurrentPoint().y, getCurrentPoint().x, getCurrentPoint().y, x, y, radius) ||
                ShapeUtils.hitTestLine(getCurrentPoint().x, getDownPoint().y, getCurrentPoint().x, getCurrentPoint().y, x, y, radius);
    }

    public void render(final RenderContext renderContext) {
        RectF rect = new RectF(getDownPoint().x,
                getDownPoint().y,
                getCurrentPoint().x,
                getCurrentPoint().y);
        applyStrokeStyle(renderContext.paint, getDisplayScale(renderContext));
        if (renderContext.matrix != null) {
            renderContext.matrix.mapRect(rect);
        }
        Matrix transformMatrix = new Matrix();
        Path path = new Path();
        path.addRect(rect, Path.Direction.CW);
        if (getOrientation() != 0) {
            transformMatrix.setRotate(getOrientation(), rect.centerX(), rect.centerY());
            path.transform(transformMatrix);
        }
        renderContext.canvas.drawPath(path, renderContext.paint);
    }
}
