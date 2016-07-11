package com.onyx.android.sdk.scribble.utils;

import android.graphics.*;
import android.view.MotionEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

import java.util.Iterator;
import java.util.UUID;

/**
 * Created by zhuzeng on 9/18/15.
 */
public class ShapeUtils {

    public static TouchPoint normalize(float scale, float pageX, float pageY, final MotionEvent touchPoint) {
        return new TouchPoint(
                ((touchPoint.getX() - pageX) / scale),
                ((touchPoint.getY() - pageY) / scale),
                touchPoint.getPressure(),
                touchPoint.getSize(),
                touchPoint.getEventTime());
    }

    public static TouchPoint normalize(float scale, float pageX, float pageY,
                                       final float x, final float y, final float pressure, final  float size, long eventTime) {
        return new TouchPoint(
                ((x - pageX) / scale),
                ((y - pageY) / scale),
                pressure,
                size,
                eventTime);
    }

    public static TouchPoint normalize(double scale, int pageX, int pageY, final TouchPoint screenPoint) {
        return new TouchPoint((float) ((screenPoint.getX() - pageX) / scale),
                (float) ((screenPoint.getY() - pageY) / scale),
                screenPoint.getPressure(), screenPoint.getSize(), screenPoint.getTimestamp());
    }

    public static TouchPoint mapPoint(final float scale, final float pageX, final float pageY, final TouchPoint normalizedPoint) {
        return new TouchPoint(
                pageX + scale * normalizedPoint.getX(),
                pageY + scale * normalizedPoint.getY(),
                normalizedPoint.getPressure(),
                normalizedPoint.getSize(),
                normalizedPoint.getTimestamp());
    }

    public static float mapPoint(final float scale, final float translate, final float origin) {
        return translate + scale * origin;
    }

    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static Rect mapInPlace(final Rect origin, final Matrix matrix) {
        float src[] = new float[4];
        float dst[] = new float[4];
        src[0] = origin.left;
        src[1] = origin.top;
        src[2] = origin.right;
        src[3] = origin.bottom;
        matrix.mapPoints(dst, src);
        origin.set((int)dst[0], (int)dst[1], (int)dst[2], (int)dst[3]);
        return origin;
    }

    public static Path renderShape(final Canvas canvas, final Paint paint, final Matrix matrix, final TouchPointList pointList) {
        if (pointList == null || pointList.size() <= 0) {
            return null;
        }
        final Iterator<TouchPoint> iterator = pointList.iterator();
        TouchPoint touchPoint = iterator.next();
        Path path = new Path();

        final float src[] = new float[2];
        final float dst[] = new float[2];
        final float lastDst[] = new float[2];
        touchPoint.mapPoint(matrix, src, dst);
        path.moveTo(dst[0], dst[1]);
        lastDst[0] = dst[0];
        lastDst[1] = dst[1];
        while (iterator.hasNext()) {
            touchPoint = iterator.next();
            touchPoint.mapPoint(matrix, src, dst);
            path.quadTo((lastDst[0] + dst[0]) / 2, (lastDst[1] + dst[1]) / 2, dst[0], dst[1]);
            lastDst[0] = dst[0];
            lastDst[1] = dst[1];
        }
        return path;
    }

    public static boolean withinRange(int last, int current, int range) {
        return Math.abs(current - last) <= range;
    }

    public static boolean contains(float x, float y, float cx, float cy, float limit) {
        float d = (cx - x) * (cx - x) + (cy - y) * (cy - y);
        return d < limit;
    }

    public static boolean contains(float x0, float y0, float x1, float y1, float cx, float cy, float limit) {
        if (contains(x0, y0, cx, cy, limit)) {
            return true;
        }
        if (contains(x1, y1, cx, cy, limit)) {
            return true;
        }
        return false;
    }

    public static boolean contains(final RectF rect, float cx, float cy, float limit) {
        if (rect == null) {
            return false;
        }

        if (rect.contains(cx, cy)) {
            return true;
        }
        if (contains(rect.left, rect.top, cx, cy, limit)) {
            return true;
        }
        if (contains(rect.left, rect.bottom, cx, cy, limit)) {
            return true;
        }
        if (contains(rect.right, rect.top, cx, cy, limit)) {
            return true;
        }
        if (contains(rect.right, rect.bottom, cx, cy, limit)) {
            return true;
        }
        return false;
    }
}
