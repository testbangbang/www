package com.onyx.android.sdk.scribble.utils;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.BrushScribbleShape;
import com.onyx.android.sdk.scribble.shape.RenderContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 01/12/2016.
 */

public class InkUtils {

    static {
        System.loadLibrary("ink_utils");
    }

    private static List<MappingConfig.PressureEntry> pressureEntries;

    private static native void nativeDrawStroke(Canvas canvas, Paint paint, float[] points);

    public static class PathEntry {
        public Path path;
        public float pathWidth;

        public PathEntry(final Path p, final float w) {
            path = p;
            pathWidth = w;
        }
    }

    public static void drawStroke(Canvas canvas, Paint paint, List<TouchPoint> points, Matrix matrix) {
        float[] src = new float[2];
        float[] dst = new float[2];
        float[] array = new float[points.size() * 5];
        for (int i = 0; i < points.size(); i++) {
            int idx = 5 * i;
            src[0] = points.get(i).x;
            src[1] = points.get(i).y;
            matrix.mapPoints(dst, src);

            array[idx] = dst[0];
            array[idx + 1] = dst[1];
            array[idx + 2] = points.get(i).pressure / 2047.0f;
            array[idx + 3] = points.get(i).size;
            array[idx + 4] = points.get(i).timestamp;
        }

        nativeDrawStroke(canvas, paint, array);
    }

    public static void setPressureEntries(final List<MappingConfig.PressureEntry> list) {
        pressureEntries = list;
    }

    public static float pressureRatio(final float pressure) {
        int index = ((int)pressure) >> 6;
        return pressureEntries.get(index).ratio;
    }

    public static float strokeWidth(final TouchPoint normalizedPoint,
                                    final float baseStrokeWidth,
                                    final float lastStrokeWidth) {
        float newStrokeWidth = pressureRatio(normalizedPoint.getPressure()) * baseStrokeWidth;
        return (lastStrokeWidth + newStrokeWidth) / 2;
    }

    public static boolean inRange(final float newStrokeWidth, final float lastStrokeWidth) {
        return Math.abs(newStrokeWidth - lastStrokeWidth) < 0.5f;
    }

    public static List<PathEntry> generate(final RenderContext renderContext, final BrushScribbleShape shape) {
        final List<PathEntry> list = new ArrayList<>();
        if (shape == null || shape.getNormalizedPoints().size() <= 0) {
            return list;
        }

        final TouchPointList touchPointList = shape.getNormalizedPoints();
        TouchPoint touchPoint = touchPointList.get(0);
        TouchPoint lastTouchPoint = touchPoint;
        float lastStrokeWidth = strokeWidth(touchPoint, shape.getStrokeWidth(), shape.getStrokeWidth());
        float currentStrokeWidth = lastStrokeWidth;

        Path path = new Path();
        path.moveTo(touchPoint.getX(), touchPoint.getY());
        for(int i = 1; i < touchPointList.size(); ++i) {
            touchPoint = touchPointList.get(i);
            currentStrokeWidth = strokeWidth(touchPoint, shape.getStrokeWidth(), lastStrokeWidth);
            if (inRange(currentStrokeWidth, lastStrokeWidth)) {
                quadTo(path, lastTouchPoint, touchPoint);
            } else {
                flushPath(list, path, lastStrokeWidth, renderContext);
                path = new Path();
                path.moveTo(lastTouchPoint.x, lastTouchPoint.y);
                quadTo(path, lastTouchPoint, touchPoint);
                lastStrokeWidth = currentStrokeWidth;
            }
            lastTouchPoint = touchPoint;
        }
        if (touchPointList.size() > 1) {
            flushPath(list, path, currentStrokeWidth, renderContext);
        }
        return list;
    }

    private static void flushPath(final List<PathEntry> list,
                                  final Path path,
                                  final float currentStrokeWidth,
                                  final RenderContext renderContext) {
        PathEntry entry = new PathEntry(path, currentStrokeWidth);
        list.add(entry);
        if (renderContext.matrix != null) {
            path.transform(renderContext.matrix);
        }
    }

    private static void quadTo(final Path path, final TouchPoint last, final TouchPoint current) {
        path.quadTo((last.x + current.x) / 2,
                (last.y + current.y) / 2,
                current.x,
                current.y);
    }

}
