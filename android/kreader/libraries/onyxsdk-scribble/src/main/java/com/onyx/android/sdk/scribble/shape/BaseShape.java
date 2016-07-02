package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import com.onyx.android.sdk.scribble.EPDRenderer;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class BaseShape implements Shape {

    private RectF boundingRect = new RectF();
    private TouchPointList normalizedPoints = new TouchPointList();
    private TouchPoint downPoint = new TouchPoint();
    private TouchPoint currentPoint = new TouchPoint();
    private String uniqueId;
    private String documentUniqueId;
    private String pageUniqueId;

    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType() {
        return ShapeFactory.SHAPE_INVALID;
    }

    public void setDocumentUniqueId(final String id) {
        documentUniqueId = id;
    }

    public String getDocumentUniqueId() {
        return documentUniqueId;
    }

    public void setPageUniqueId(final String pageId) {
        pageUniqueId = pageId;
    }

    public String getPageUniqueId() {
        return pageUniqueId;
    }

    public void setShapeUniqueId(final String id) {
        uniqueId = id;
    }

    public String getShapeUniqueId() {
        return uniqueId;
    }

    public void ensureShapeUniqueId() {
        if (uniqueId == null || uniqueId.trim().isEmpty()) {
            uniqueId = ShapeUtils.generateUniqueId();
        }
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

    public void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        downPoint.x = normalizedPoint.x;
        downPoint.y = normalizedPoint.y;
        boundingRect.union(normalizedPoint.x, normalizedPoint.y);
        normalizedPoints.add(normalizedPoint);
    }

    public void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        currentPoint.x = normalizedPoint.x;
        currentPoint.y = normalizedPoint.y;
        boundingRect.union(normalizedPoint.x, normalizedPoint.y);
        normalizedPoints.add(normalizedPoint);
    }

    public void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        currentPoint.x = normalizedPoint.x;
        currentPoint.y = normalizedPoint.y;
        boundingRect.union(normalizedPoint.x, normalizedPoint.y);
        normalizedPoints.add(normalizedPoint);
    }

    public void addPoints(final TouchPointList points) {
        normalizedPoints.addAll(points);
        if (points.size() < 2) {
            return;
        }
        downPoint.set(points.get(0));
        currentPoint.set(points.get(1));
    }

    public TouchPointList getPoints() {
        return normalizedPoints;
    }

    public void render(final Canvas canvas, final Paint paint, final Matrix matrix) {
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

    public final TouchPointList getNormalizedPoints() {
        return normalizedPoints;
    }
}
