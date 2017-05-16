package com.onyx.android.sdk.scribble.shape;

/**
 * Created by zhuzeng on 8/6/16.
 */
public class Triangle90Shape extends TriangleShape {
    @Override
    protected void calculatePoint() {
        points[0] = getDownPoint().getX();
        points[1] = getDownPoint().getY();
        points[2] = getCurrentPoint().getX();
        points[3] = getCurrentPoint().getY();
        points[4] = points[0];
        points[5] = points[3];
    }
}
