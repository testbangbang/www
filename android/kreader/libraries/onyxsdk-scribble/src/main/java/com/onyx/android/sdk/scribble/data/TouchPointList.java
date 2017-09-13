package com.onyx.android.sdk.scribble.data;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

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

    public void add(final int index, final TouchPoint touchPoint) {
        points.add(index, touchPoint);
    }

    public void addAll(final TouchPointList other) {
        points.addAll(other.getPoints());
    }

    public Iterator<TouchPoint> iterator() {
        return points.iterator();
    }

    public void scaleAllPoints(final float scaleValue) {
        for (TouchPoint point : points) {
            point.x = point.x * scaleValue;
            point.y = point.y * scaleValue;
        }
    }

    public void translateAllPoints(final float dx, final float dy) {
        for (TouchPoint point : points) {
            point.x = point.x + dx;
            point.y = point.y + dy;
        }
    }

    public void rotateAllPoints(float rotateAngle, PointF originPoint) {
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate(rotateAngle, originPoint.x, originPoint.y);
        for (TouchPoint point : points) {
            float[] pts = new float[2];
            pts[0] = point.x;
            pts[1] = point.y;
            rotateMatrix.mapPoints(pts);
            point.x = pts[0];
            point.y = pts[1];
        }
    }
}
