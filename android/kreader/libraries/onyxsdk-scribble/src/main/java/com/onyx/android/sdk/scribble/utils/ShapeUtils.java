package com.onyx.android.sdk.scribble.utils;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import com.onyx.android.sdk.scribble.data.TouchPoint;

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
}
