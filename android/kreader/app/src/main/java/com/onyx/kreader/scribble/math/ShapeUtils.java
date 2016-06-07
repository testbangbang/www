package com.onyx.kreader.scribble.math;

import android.content.Context;
import android.graphics.Matrix;
import android.view.MotionEvent;
import com.onyx.kreader.scribble.data.TouchPoint;

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

}
