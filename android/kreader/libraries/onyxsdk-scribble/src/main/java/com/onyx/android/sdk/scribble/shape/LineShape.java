package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 7/12/16.
 */
public class LineShape extends BaseShape {

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
        applyStrokeStyle(renderContext.paint, getDisplayScale(renderContext));
        renderContext.canvas.drawLine(points[0], points[1], points[2], points[3], renderContext.paint);
    }

}
