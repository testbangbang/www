package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by zhuzeng on 4/25/16.
 */
public class RectangleShape extends BaseShape {


    public int getType() {
        return ShapeFactory.SHAPE_RECTANGLE;
    }

    public boolean addMovePoint() {
        return false;
    }

    public void render(final Canvas canvas, final Paint paint, final Matrix matrix) {
        RectF rect = new RectF(getDownPoint().x,
                getDownPoint().y,
                getCurrentPoint().x,
                getCurrentPoint().y);
        applyStrokeStyle(paint);
        if (matrix != null) {
            matrix.mapRect(rect);
        }
        canvas.drawRect(rect, paint);
    }


}
