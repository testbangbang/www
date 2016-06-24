package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

import java.util.Iterator;

/**
 * Created by zhuzeng on 4/19/16.
 * One stroke
 */
public class NormalScribbleShape extends EPDShape {

    public void render(final Canvas canvas, final Paint paint, final Matrix matrix) {
        final TouchPointList pointList = getNormalizedPoints();
        if (pointList.size() <= 0) {
            return;
        }
        paint.setStrokeWidth(getStrokeWidth());
        final Iterator<TouchPoint> iterator = pointList.iterator();
        Path path = new Path();
        TouchPoint touchPoint = iterator.next();
        touchPoint.mapInPlace(matrix);
        TouchPoint lastPoint = touchPoint;
        path.moveTo(touchPoint.x, touchPoint.y);
        while (iterator.hasNext()) {
            touchPoint = iterator.next();
            touchPoint.mapInPlace(matrix);
            path.quadTo((lastPoint.x + touchPoint.x) / 2, (lastPoint.y + touchPoint.y) / 2, touchPoint.x, touchPoint.y);
            lastPoint = touchPoint;
        }
        canvas.drawPath(path, paint);
    }

}
