package com.onyx.android.sdk.data.cms;

import android.view.MotionEvent;

/**
 * Created by joy on 6/12/14.
 */
public class OnyxScribblePoint {
    public float x;
    public float y;
    public float pressure;
    public float size;
    public long eventTime;

    public OnyxScribblePoint() {

    }

    public OnyxScribblePoint(float x, float y, float pressure, float size, long eventTime) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
        this.size = size;
        this.eventTime = eventTime;
    }

    public static OnyxScribblePoint fromEvent(MotionEvent e) {
        return new OnyxScribblePoint(e.getX(), e.getY(),
                e.getPressure(), e.getSize(), e.getEventTime());
    }

    public static OnyxScribblePoint fromHistoricalEvent(MotionEvent e, int pos) {
        return new OnyxScribblePoint(e.getHistoricalX(pos), e.getHistoricalY(pos),
                e.getHistoricalPressure(pos), e.getHistoricalSize(pos),
                e.getHistoricalEventTime(pos));
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getPressure() {
        return pressure;
    }

    public float getSize() {
        return size;
    }

    public long getEventTime() {
        return eventTime;
    }

    public float distanceTo(OnyxScribblePoint point) {
        float dx = x - point.getX();
        float dy = y - point.getY();
        return (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public float velocityFrom(OnyxScribblePoint point) {
        return distanceTo(point) / (eventTime - point.getEventTime());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OnyxScribblePoint p = (OnyxScribblePoint)o;
        return x == p.x &&
                y == p.y &&
                Float.compare(pressure, p.pressure) == 0 &&
                Float.compare(size, p.size) == 0 &&
                eventTime == p.eventTime;
    }

    @Override
    public String toString() {
        return "{x: " + x + ", y: " + y + ", pressure: " + pressure +
                ", size: " + size + ", event time: " + eventTime + "}";
    }
}
