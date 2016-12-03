package com.onyx.android.sdk.scribble.data;

import org.nustaq.serialization.annotations.Flat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhuzeng on 6/4/16.
 * Have to introduce this class, since dbflow does not support
 * type converter from List<X> to Blob, but only Y to Blob. Have
 * to add a wrapper class for List<X>
 */
@Flat
public class TouchPointList implements Serializable {

    @Flat
    private List<TouchPoint> points;

    public TouchPointList() {
        points = new ArrayList<TouchPoint>();
    }

    public TouchPointList(int size) {
        points = new ArrayList<TouchPoint>(size);
    }

    public final List<TouchPoint> getPoints() {
        return points;
    }

    public void setPoints(final List<TouchPoint> list) {
        points = list;
    }

    public int size() {
        return points.size();
    }

    public TouchPoint get(int i) {
        return points.get(i);
    }

    public void add(final TouchPoint touchPoint) {
        points.add(touchPoint);
    }

    public void addAll(final TouchPointList other) {
        points.addAll(other.getPoints());
    }

    public Iterator<TouchPoint> iterator() {
        return points.iterator();
    }

    public void scaleAllPoint(final float scaleValue) {
        for (TouchPoint point : points) {
            point.x = point.x * scaleValue;
            point.y = point.y * scaleValue;
        }
    }
}
