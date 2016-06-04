package com.onyx.kreader.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import com.onyx.kreader.scribble.data.TouchPoint;
import com.onyx.kreader.scribble.data.TouchPointList;

import java.util.Iterator;
import java.util.List;

/**
 * Created by zhuzeng on 4/19/16.
 * One stroke
 */
public class NormalScribbleShape extends EPDShape {

    public void render(final Matrix matrix, final Canvas canvas, final Paint paint) {
        final TouchPointList pointList = getNormalizedPoints();
        if (pointList.size() <= 0) {
            return;
        }
        paint.setStrokeWidth(getStrokeWidth());
        final Iterator<TouchPoint> iterator = pointList.iterator();
        Path path = new Path();
        TouchPoint touchPoint = iterator.next();
        TouchPoint lastPoint = touchPoint;
        path.moveTo(touchPoint.x, touchPoint.y);
        while (iterator.hasNext()) {
            touchPoint = iterator.next();
            path.quadTo((lastPoint.x + touchPoint.x) / 2, (lastPoint.y + touchPoint.y) / 2, touchPoint.x, touchPoint.y);
            lastPoint = touchPoint;
        }
    }

}
