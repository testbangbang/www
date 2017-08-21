package com.onyx.android.sdk.scribble.data;


import android.view.MotionEvent;

import com.onyx.android.sdk.data.PageInfo;

import org.nustaq.serialization.annotations.Flat;

import java.io.Serializable;

/**
 * Created by zhuzeng on 4/22/16.
 */
@Flat
public class TouchPoint implements Serializable, Cloneable {

    @Flat
    public float x;

    @Flat
    public float y;

    @Flat
    public float pressure;

    @Flat
    public float size;

    @Flat
    public long timestamp;

    public TouchPoint() {
    }

    public TouchPoint(final float px, final float py, final float p, final float s, final long ts) {
        x = px;
        y = py;
        pressure = p;
        size = s;
        timestamp = ts;
    }

    public TouchPoint(final MotionEvent motionEvent) {
        x = motionEvent.getX();
        y = motionEvent.getY();
        pressure = motionEvent.getPressure();
        size = motionEvent.getSize();
        timestamp = motionEvent.getEventTime();
    }

    public TouchPoint(final TouchPoint source) {
        x = source.getX();
        y = source.getY();
        pressure = source.getPressure();
        size = source.getSize();
        timestamp = source.getTimestamp();
    }

    public void set(final TouchPoint point) {
        x = point.x;
        y = point.y;
        pressure = point.pressure;
        size = point.size;
        timestamp = point.timestamp;
    }

    public void offset(int dx, int dy) {
        x += dx;
        y += dy;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void normalize(final PageInfo pageInfo) {
        x = (x - pageInfo.getDisplayRect().left) / pageInfo.getActualScale();
        y = (y - pageInfo.getDisplayRect().top) / pageInfo.getActualScale();
    }

    public void origin(final PageInfo pageInfo) {
        x = x * pageInfo.getActualScale() + pageInfo.getDisplayRect().left;
        y = y * pageInfo.getActualScale() + pageInfo.getDisplayRect().top;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static TouchPoint create(final MotionEvent motionEvent) {
        return new TouchPoint(motionEvent);
    }

    @Override
    protected TouchPoint clone() throws CloneNotSupportedException {
        TouchPoint clone;
        clone = (TouchPoint) super.clone();
        return clone;
    }
}
