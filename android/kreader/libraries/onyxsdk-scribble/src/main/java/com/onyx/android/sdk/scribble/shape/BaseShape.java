package com.onyx.android.sdk.scribble.shape;

import android.graphics.*;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

import java.util.List;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class BaseShape implements Shape {

    private RectF boundingRect;
    private TouchPointList normalizedPoints = new TouchPointList();
    private TouchPoint downPoint = new TouchPoint();
    private TouchPoint currentPoint = new TouchPoint();
    private String uniqueId;
    private String documentUniqueId;
    private String pageUniqueId;
    private int color = Color.BLACK;
    private float strokeWidth;
    private Path originDisplayPath;
    private Path scaledDisplayPath;
    private int originWidth;
    private int originHeight;

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

    public boolean addMovePoint() {
        return true;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(final float width) {
        strokeWidth = width;
    }

    public boolean supportDFB() {
        return false;
    }

    public void updateBoundingRect() {
        List<TouchPoint> list = normalizedPoints.getPoints();
        for(TouchPoint touchPoint: list) {
            if (boundingRect == null) {
                boundingRect = new RectF(touchPoint.x, touchPoint.y, touchPoint.x, touchPoint.y);
            } else {
                boundingRect.union(touchPoint.x, touchPoint.y);
            }
        }
    }

    public RectF getBoundingRect() {
        return boundingRect;
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
        currentPoint.x = normalizedPoint.x;
        currentPoint.y = normalizedPoint.y;
        normalizedPoints.add(normalizedPoint);
        updateBoundingRect();
    }

    public void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        currentPoint.x = normalizedPoint.x;
        currentPoint.y = normalizedPoint.y;
        if (addMovePoint()) {
            normalizedPoints.add(normalizedPoint);
        }
    }

    public void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        currentPoint.x = normalizedPoint.x;
        currentPoint.y = normalizedPoint.y;
        normalizedPoints.add(normalizedPoint);
        updateBoundingRect();
    }

    public void addPoints(final TouchPointList points) {
        normalizedPoints.addAll(points);
        updateBoundingRect();
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

    public boolean fastHitTest(final float x, final float y, final float radius) {
        final RectF boundingRect = getBoundingRect();
        if (boundingRect == null) {
            return false;
        }
        final float limit = radius * radius;
        return ShapeUtils.contains(boundingRect, x, y, limit);
    }

    public boolean hitTest(final float x, final float y, final float radius) {
        final float limit = radius * radius;
        float thickness = getStrokeWidth();
        float x1, y1, x2, y2;
        float left, top, right, bottom;
        float xMin = Float.MAX_VALUE, xMax = Float.MIN_VALUE, yMin = Float.MAX_VALUE, yMax = Float.MIN_VALUE;
        boolean hit = false;
        int first, second;
        final List<TouchPoint> points = normalizedPoints.getPoints();
        for (int i = 0; i < points.size() - 1; ++i) {
            first = i;
            second = i + 1;

            x1 = points.get(first).getX();
            y1 = points.get(first).getY();

            x2 = points.get(second).getX();
            y2 = points.get(second).getY();

            left = x1 - thickness;
            top = y1 - thickness;
            right = x1 + thickness;
            bottom = y1 + thickness;

            boolean isIntersect = ShapeUtils.contains(x1, y1, x2, y2, x, y, limit);
            if (isIntersect) {
                hit = true;
                break;
            }
            if (xMin > left) {
                xMin = left;
            }
            if (xMax < right) {
                xMax = right;
            }
            if (yMin > top) {
                yMin = top;
            }
            if (yMax < bottom) {
                yMax = bottom;
            }
        }
        return hit;

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

    public void applyStrokeStyle(final Paint paint) {
        paint.setStrokeWidth(getStrokeWidth());
        paint.setColor(getColor());
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeMiter(4.0f);
    }

    public Path getOriginDisplayPath() {
        return originDisplayPath;
    }

    public void setOriginDisplayPath(final Path p) {
        originDisplayPath = p;
    }

    public Path getScaledDisplayPath() {
        return scaledDisplayPath;
    }

    public void setScaledDisplayPath(Path scaledDisplayPath) {
        this.scaledDisplayPath = scaledDisplayPath;
    }

    public void clear() {
        originDisplayPath = null;
    }

    public void setPageOriginWidth(int width) {
        originWidth = width;
    }

    public int getPageOriginWidth() {
        return originWidth;
    }

    public void setPageOriginHeight(int height) {
        originHeight = height;
    }

    public int getPageOriginHeight() {
        return originHeight;
    }

}
