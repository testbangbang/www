package com.onyx.android.sdk.scribble.utils;

import android.graphics.*;
import android.view.MotionEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.math.OnyxMatrix;
import com.onyx.android.sdk.scribble.shape.RenderContext;

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

    public static Path renderShape(final RenderContext renderContext, final TouchPointList pointList) {
        if (pointList == null || pointList.size() <= 0) {
            return null;
        }
        final Iterator<TouchPoint> iterator = pointList.iterator();
        TouchPoint touchPoint = iterator.next();
        final float lastDst[] = new float[2];
        Path path = new Path();
        path.moveTo(touchPoint.getX(), touchPoint.getY());
        lastDst[0] = touchPoint.getX();
        lastDst[1] = touchPoint.getY();
        while (iterator.hasNext()) {
            touchPoint = iterator.next();
            path.quadTo((lastDst[0] + touchPoint.getX()) / 2, (lastDst[1] + touchPoint.getY()) / 2, touchPoint.getX(), touchPoint.getY());
            lastDst[0] = touchPoint.getX();
            lastDst[1] = touchPoint.getY();
        }
        path.transform(renderContext.matrix);
        return path;
    }

    public static void renderSelectionHandlers(final RenderContext renderContext, final RectF boundingRect) {
        final RectF rect = new RectF(boundingRect);
        if (renderContext.matrix != null) {
            renderContext.matrix.mapRect(rect);
        }
        renderContext.canvas.drawRect(rect, renderContext.paint);
        renderRect(rect.left, rect.top, renderContext.handlerSize, renderContext.canvas, renderContext.paint);
        renderRect(rect.left, rect.bottom, renderContext.handlerSize, renderContext.canvas, renderContext.paint);
        renderRect(rect.right, rect.top, renderContext.handlerSize, renderContext.canvas, renderContext.paint);
        renderRect(rect.right, rect.bottom, renderContext.handlerSize, renderContext.canvas, renderContext.paint);
    }

    public static void renderRect(final float x, final float y, final float size, final Canvas canvas, final Paint paint) {
        canvas.drawRect(x - size, y - size, x + size, y + size, paint);
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

    public static boolean hitTestLine(final float sx, final float sy, final float ex, final float ey, final float cx, final float cy, final float radius) {
        float dx = ex - sx;
        float dy = ey - sy;
        float a = dx * dx + dy * dy;
        float b = 2 * (dx * (sx - cx) + dy * (sy - cy));
        float c = cx * cx + cy * cy;
        c += sx * sx + sy * sy;
        c -= 2 * (cx * sx + cy * sy);
        c -= radius * radius;
        float bb4ac = b * b - 4 * a * c;
        return bb4ac >= 0;
    }
}
