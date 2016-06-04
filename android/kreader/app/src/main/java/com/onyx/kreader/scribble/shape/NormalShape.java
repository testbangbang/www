package com.onyx.kreader.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import com.onyx.kreader.scribble.data.TouchPoint;
import com.onyx.kreader.scribble.data.TouchPointList;

/**
 * Created by zhuzeng on 4/25/16.
 */
public class NormalShape implements Shape {

    private TouchPoint downPoint = new TouchPoint();
    private TouchPoint currentPoint = new TouchPoint();

    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType() {
        return ShapeFactory.SHAPE_INVALID;
    }

    /**
     * get instance unique id
     * @return
     */
    public String getUniqueId() {
        return null;
    }

    public int getZOrder() {
        return 0;
    }

    public void setZOrder(int order) {
    }

    public int getColor() {
        return 0;
    }

    public void setColor(int color) {

    }

    public float getStrokeWidth() {
        return 3.0f;
    }

    public void setStrokeWidth(final float width) {}

    public boolean supportDFB() {
        return false;
    }


    public RectF getBoundingRect() {
        return null;
    }

    public void moveTo(final float x, final float y) {

    }

    public void resize(final float width, final float height) {}
    public int getOrientation() {
        return 0;
    }

    public void setOrientation(int orientation) {}


    public void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint){
        downPoint.x = normalizedPoint.x;
        downPoint.y = normalizedPoint.y;
    }

    public void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        currentPoint.x = normalizedPoint.x;
        currentPoint.y = normalizedPoint.y;
    }

    public void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        currentPoint.x = normalizedPoint.x;
        currentPoint.y = normalizedPoint.y;
    }

    public void addPoints(final TouchPointList points) {

    }

    public void render(final Matrix matrix, final Canvas canvas, final Paint paint) {
    }

    public boolean hitTest(final float x, final float y) {
        return false;
    }

    public final TouchPoint getCurrentPoint() {
        return currentPoint;
    }

    public final TouchPoint getDownPoint() {
        return downPoint;
    }
}
