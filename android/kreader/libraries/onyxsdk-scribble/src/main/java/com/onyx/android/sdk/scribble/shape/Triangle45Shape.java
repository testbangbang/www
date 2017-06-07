package com.onyx.android.sdk.scribble.shape;

/**
 * Created by zhuzeng on 8/6/16.
 */
public class Triangle45Shape extends TriangleShape {

    public int getType() {
        return ShapeFactory.SHAPE_TRIANGLE_45;
    }

    @Override
    protected void calculatePoint() {
        points[0] = getDownPoint().getX();
        points[1] = getDownPoint().getY();
        points[2] = getCurrentPoint().getX();
        points[3] = getCurrentPoint().getY();
        //sin90
        float frac = 1;
        float deltaX = points[2] - points[0];
        float deltaY = points[3] - points[1];
        points[4] = points[0] + (float) (Math.sqrt(1 - Math.pow(frac, 2)) * deltaX - frac * deltaY);
        points[5] = points[1] + (float) (Math.sqrt(1 - Math.pow(frac, 2)) * deltaY + frac * deltaX);
    }
}
