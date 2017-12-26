package com.onyx.android.sample.scribble;

import java.util.ArrayList;

/**
 * Created by joy on 12/19/17.
 */

public class Stroke {

    private ArrayList<StrokePoint> points = new ArrayList<>();

    public Stroke() {

    }

    public void addPoint(StrokePoint point) {
        points.add(point);
    }

    public ArrayList<StrokePoint> getPoints() {
        return points;
    }
}
