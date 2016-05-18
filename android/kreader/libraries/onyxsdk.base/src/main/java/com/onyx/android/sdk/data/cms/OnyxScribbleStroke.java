package com.onyx.android.sdk.data.cms;

import android.graphics.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 6/21/14
 * Time: 3:07 PM
 * Provide better scribble rendering.
 */

public class OnyxScribbleStroke {

    public static abstract class OnPointAddedListener {
        public void onPointAdded(OnyxScribblePoint point, float newWidth, boolean createNewPath, boolean repaint) { }
    }

    private List<Path> pathList = new ArrayList<Path>();
    private List<Float> widthList = new ArrayList<Float>();
    private int ignoreCount = 40;

    private OnPointAddedListener onPointAddedListener = null;
    public void setOnPointAddedListener(OnPointAddedListener listener) {
        onPointAddedListener = listener;
    }
    private void notifyPointAdded(OnyxScribblePoint point, float width, boolean createNewPath, boolean repaint) {
        if (onPointAddedListener != null) {
            onPointAddedListener.onPointAdded(point, width, createNewPath, repaint);
        }
    }

    public OnyxScribbleStroke() {
        super();
    }

    public void clear() {
        pathList.clear();
        widthList.clear();
    }

    public void setIgnoreCount(int c) {
        ignoreCount = c;
    }

    /**
     * Don't need to draw all path.
     * @return
     */
    private int getLastAddedIndex() {
        return Math.max(0, pathList.size() - ignoreCount);
    }

    public Path getLastPath() {
        if (pathList.size() <= 0) {
            return null;
        }
        return pathList.get(pathList.size() - 1);
    }

    public int getSegmentSize() {
        return pathList.size();
    }

    public void addSegment(final Path path, final float width) {
        pathList.add(path);
        widthList.add(width);
    }

    public boolean addScreenPoint(final OnyxScribblePoint lastPoint, final float lastWidth, final OnyxScribblePoint newPoint, final float newWidth, boolean repaint) {
        boolean createNewPath = false;
        Path path;
        int cmp = Float.compare(lastWidth, newWidth);
        if (cmp == 0 && pathList.size() > 0) {
            path = pathList.get(pathList.size() - 1);
            path.quadTo((newPoint.getX() + lastPoint.getX()) / 2, (newPoint.getY() + lastPoint.getY()) / 2, newPoint.getX(), newPoint.getY());
        } else {
            path = new Path();
            if (lastPoint != null) {
                path.moveTo(lastPoint.getX(), lastPoint.getY());
                path.quadTo((newPoint.getX() + lastPoint.getX()) / 2, (newPoint.getY() + lastPoint.getY()) / 2, newPoint.getX(), newPoint.getY());
            } else {
                path.moveTo(newPoint.getX(), newPoint.getY());
            }
            pathList.add(path);
            widthList.add(newWidth);
            createNewPath = true;
        }
        notifyPointAdded(newPoint, newWidth, createNewPath, repaint);
        return createNewPath;
    }

    public boolean addScreenPoint(final float lastWidth, final float newWidth, final OnyxScribblePoint lastPoint, final OnyxScribblePoint newPoint, boolean repaint) {
        return addScreenPoint(lastPoint, lastWidth, newPoint, newWidth, repaint);
    }

    public boolean addScreenPoint(final float lastWidth, final float newWidth, final OnyxScribblePoint lastPoint, final OnyxScribblePoint newPoint) {
        return addScreenPoint(lastWidth, newWidth, lastPoint, newPoint, false);
    }

    public Rect getStrokeRegion(final float size) {
        RectF result = new RectF();
        RectF rf = new RectF();
        for (Path path : pathList) {
            path.computeBounds(rf, true);
            result.union(rf);
        }
        Rect strokeRegion = new Rect((int)(result.left - size), (int)(result.top - size), (int)(result.right + size), (int)(result.bottom + size));
        return strokeRegion;
    }

    public Rect getDirtyRegion(final float size) {
        RectF result = new RectF();
        RectF rf = new RectF();
        for (int i = getLastAddedIndex(); i < pathList.size(); ++i) {
            Path path = pathList.get(i);
            path.computeBounds(rf, true);
            result.union(rf);
        }
        Rect strokeRegion = new Rect((int)(result.left - size), (int)(result.top - size), (int)(result.right + size), (int)(result.bottom + size));
        return strokeRegion;
    }

    public void drawDirtyPath(Canvas canvas, Paint paint)  {
        for(int i = getLastAddedIndex(); i < pathList.size(); ++i) {
            Path path = pathList.get(i);
            paint.setStrokeWidth(widthList.get(i));
            canvas.drawPath(path, paint);
        }
    }

    public void draw(Canvas canvas, Paint paint, Matrix matrix,int color) {
        for(int i = 0; i < pathList.size(); ++i) {
            Path path = pathList.get(i);
            Path rendering = path;
            if (matrix != null) {
                Path dst = new Path();
                path.transform(matrix, dst);
                rendering = dst;
            }
            paint.setColor(color);
            paint.setStrokeWidth(widthList.get(i));
            canvas.drawPath(rendering, paint);
        }
    }
}
