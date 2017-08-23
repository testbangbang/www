package com.onyx.android.sdk.scribble.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.hanvon.core.Algorithm;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.RenderContext;

import java.util.Iterator;
import java.util.UUID;

/**
 * Created by zhuzeng on 9/18/15.
 */
public class ShapeUtils {

    public static final int maxIterations = 10;
    public static final float[] innerPolygonCoef = new float[maxIterations + 1];
    public static final float[] outerPolygonCoef = new float[maxIterations + 1];
    private static boolean iterationInited = false;


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

    public static TouchPoint normalize(float scale, int pageX, int pageY, final TouchPoint screenPoint) {
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
            path.quadTo((lastDst[0] + touchPoint.getX()) / 2, (lastDst[1] + touchPoint.getY()) / 2,
                    touchPoint.getX(), touchPoint.getY());
            lastDst[0] = touchPoint.getX();
            lastDst[1] = touchPoint.getY();
        }
        if (renderContext.matrix != null) {
            path.transform(renderContext.matrix);
        }
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

    public static boolean hitTest(final float x1, final float y1, final float x2, final float y2, final float x, final float y, float limit) {
        float value = Algorithm.distance(x1, y1, x2, y2, x, y);
        return value <= limit;
    }

    public static float distance(final float x1, final float y1, final float x2, final float y2, final float x, final float y) {
        return Algorithm.distance(x1, y1, x2, y2, x, y);
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

    private static void initIteration() {
        if (iterationInited) {
            return;
        }
        iterationInited = true;
        for (int t = 0; t <= maxIterations; t++) {
            int numNodes = 4 << t;
            innerPolygonCoef[t] = 0.5f/ (float)(Math.cos(4 * (float)Math.acos(0.0) / (float)numNodes));
            outerPolygonCoef[t] = 0.5f/ (float)(Math.cos(2 * Math.acos(0.0) / (float) numNodes) * Math.cos(2 * Math.acos(0.0) / (float)numNodes));
        }
    }

    // http://yehar.com/blog/?p=2926
    // Test for collision between an ellipse of horizontal radius w and vertical radius h at (x0, y0) and
    // a circle of radius r at (x1, y1)
    public static boolean collide(float x0, float y0, float w, float h, float x1, float y1, float r)  {
        float x = Math.abs(x1 - x0);
        float y = Math.abs(y1 - y0);

        if (x*x + (h - y)*(h - y) <= r*r || (w - x)*(w - x) + y*y <= r*r || x*h + y*w <= w*h
                || ((x*h + y*w - w*h)*(x*h + y*w - w*h) <= r*r*(w*w + h*h) && x*w - y*h >= -h*h && x*w - y*h <= w*w)) {
            return true;
        }
        if ((x-w)*(x-w) + (y-h)*(y-h) <= r*r || (x <= w && y - r <= h) || (y <= h && x - r <= w)) {
            return iterate(x, y, w, 0, 0, h, r*r);
        }
        return false;
    }

    public static boolean iterate(double x, double y, double c0x, double c0y, double c2x, double c2y, double rr) {
        initIteration();
        for (int t = 1; t <= maxIterations; t++) {
            double c1x = (c0x + c2x)*innerPolygonCoef[t];
            double c1y = (c0y + c2y)*innerPolygonCoef[t];
            double tx = x - c1x;
            double ty = y - c1y;
            if (tx*tx + ty*ty <= rr) {
                return true;
            }
            double t2x = c2x - c1x;
            double t2y = c2y - c1y;
            if (tx*t2x + ty*t2y >= 0 && tx*t2x + ty*t2y <= t2x*t2x + t2y*t2y &&
                    (ty*t2x - tx*t2y >= 0 || rr*(t2x*t2x + t2y*t2y) >= (ty*t2x - tx*t2y)*(ty*t2x - tx*t2y))) {
                return true;
            }
            double t0x = c0x - c1x;
            double t0y = c0y - c1y;
            if (tx*t0x + ty*t0y >= 0 && tx*t0x + ty*t0y <= t0x*t0x + t0y*t0y &&
                    (ty*t0x - tx*t0y <= 0 || rr*(t0x*t0x + t0y*t0y) >= (ty*t0x - tx*t0y)*(ty*t0x - tx*t0y))) {
                return true;
            }
            double c3x = (c0x + c1x)*outerPolygonCoef[t];
            double c3y = (c0y + c1y)*outerPolygonCoef[t];
            if ((c3x-x)*(c3x-x) + (c3y-y)*(c3y-y) < rr) {
                c2x = c1x;
                c2y = c1y;
                continue;
            }
            double c4x = c1x - c3x + c1x;
            double c4y = c1y - c3y + c1y;
            if ((c4x-x)*(c4x-x) + (c4y-y)*(c4y-y) < rr) {
                c0x = c1x;
                c0y = c1y;
                continue;
            }
            double t3x = c3x - c1x;
            double t3y = c3y - c1y;
            if (ty*t3x - tx*t3y <= 0 || rr*(t3x*t3x + t3y*t3y) > (ty*t3x - tx*t3y)*(ty*t3x - tx*t3y)) {
                if (tx*t3x + ty*t3y > 0) {
                    if (Math.abs(tx*t3x + ty*t3y) <= t3x*t3x + t3y*t3y || (x-c3x)*(c0x-c3x) + (y-c3y)*(c0y-c3y) >= 0) {
                        c2x = c1x;
                        c2y = c1y;
                        continue;
                    }
                } else if (-(tx*t3x + ty*t3y) <= t3x*t3x + t3y*t3y || (x-c4x)*(c2x-c4x) + (y-c4y)*(c2y-c4y) >= 0) {
                    c0x = c1x;
                    c0y = c1y;
                    continue;
                }
            }
            return false;
        }
        return false; // Out of iterations so it is unsure if there was a collision. But have to return something.
    }


    
}
