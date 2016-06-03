package com.onyx.kreader.scribble.data;

/**
 * Created by zhuzeng on 4/22/16.
 */
public class TouchPoint {

    public float x;
    public float y;
    public float pressure;
    public float size;
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
}
