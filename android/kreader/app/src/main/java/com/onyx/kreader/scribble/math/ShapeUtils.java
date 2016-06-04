package com.onyx.kreader.scribble.math;

import android.content.Context;
import android.view.MotionEvent;
import com.onyx.kreader.scribble.data.TouchPoint;

/**
 * Created by zhuzeng on 9/18/15.
 */
public class ShapeUtils {

    public static TouchPoint normalize(double scale, int pageX, int pageY, final MotionEvent touchPoint) {
        return new TouchPoint(
                (float) ((touchPoint.getX() - pageX) / scale),
                (float) ((touchPoint.getY() - pageY) / scale),
                touchPoint.getPressure(),
                touchPoint.getSize(),
                touchPoint.getEventTime());
    }

    public static TouchPoint normalize(double scale, int pageX, int pageY, final float x, final float y, final float pressure, final  float size, long eventTime) {
        return new TouchPoint(
                (float) ((x - pageX) / scale),
                (float) ((y - pageY) / scale),
                pressure,
                size,
                eventTime);
    }

    public static TouchPoint normalize(double scale, int pageX, int pageY, final TouchPoint screenPoint) {
        return new TouchPoint((float) ((screenPoint.getX() - pageX) / scale),
                (float) ((screenPoint.getY() - pageY) / scale),
                screenPoint.getPressure(), screenPoint.getSize(), screenPoint.getTimestamp());
    }

}
