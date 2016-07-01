package com.onyx.android.sdk.scribble.data;


import android.graphics.Matrix;
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

    public void set(final TouchPoint point) {
        x = point.x;
        y = point.y;
        pressure = point.pressure;
        size = point.size;
        timestamp = point.timestamp;
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

    public void mapPoint(final Matrix matrix, final float[] src, final float[] dst) {
        src[0] = x;
        src[1] = y;
        matrix.mapPoints(dst, src);
    }

}
