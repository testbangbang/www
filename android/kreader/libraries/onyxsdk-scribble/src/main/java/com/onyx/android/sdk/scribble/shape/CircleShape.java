package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by zhuzeng on 4/20/16.
 */
public class CircleShape extends BaseShape {

    private TouchPoint downPoint = new TouchPoint();
    private TouchPoint currentPoint = new TouchPoint();

    public int getType() {
        return ShapeFactory.SHAPE_CIRCLE;
    }


    public void render(final Canvas canvas, final Paint paint, final Matrix matrix) {
        float cx = (downPoint.x + currentPoint.x) / 2;
        float cy = (downPoint.y + currentPoint.y) / 2;
        float radius = Math.abs(cx - downPoint.x);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(cx, cy, radius, paint);
    }



}
