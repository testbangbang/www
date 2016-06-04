package com.onyx.kreader.scribble.data;

import org.nustaq.serialization.annotations.Flat;

import java.io.Serializable;

/**
 * Created by zhuzeng on 4/22/16.
 */
@Flat
public class TouchPoint implements Serializable {

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
