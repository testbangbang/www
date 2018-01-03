package com.onyx.android.sample.scribble;

/**
 * Created by joy on 12/19/17.
 */

public class StrokePoint implements Cloneable {
    public float x;
    public float y;
    public float size;
    public float pressure;
    public long time; // ms, in SystemClock.currentThreadTimeMillis base
    public int toolType;

    public StrokePoint() {

    }

    public StrokePoint(StrokePoint point) {
        StrokePoint p = new StrokePoint();
        p.x = point.x;
        p.y = point.y;
        p.size = point.size;
        p.pressure = point.pressure;
        p.time = point.time;
        p.toolType = point.toolType;
    }

    public StrokePoint(float x, float y, float size, float pressure, long time, int toolType) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.pressure = pressure;
        this.time = time;
        this.toolType = toolType;
    }

    @Override
    public StrokePoint clone() {
        StrokePoint p = new StrokePoint(this);
        return p;
    }
}
