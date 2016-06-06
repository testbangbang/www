package com.onyx.kreader.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import com.onyx.kreader.scribble.data.TouchPoint;
import com.onyx.kreader.scribble.data.TouchPointList;

/**
 * Created by zhuzeng on 4/25/16.
 * shape on canvas.
 */
public class NormalShape implements Shape {

    private TouchPoint downPoint = new TouchPoint();
    private TouchPoint currentPoint = new TouchPoint();
    private String uniqueId;

    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType() {
        return ShapeFactory.SHAPE_INVALID;
    }

    public void setUniqueId(final String id) {
        uniqueId = id;
    }

    public String getUniqueId() {
        return uniqueId;
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

    public void resize(final float width, final float height) {
    }

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
        // the first is download
        if (points.size() != 2) {
            return;
        }
        downPoint.set(points.get(0));
        currentPoint.set(points.get(1));
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
