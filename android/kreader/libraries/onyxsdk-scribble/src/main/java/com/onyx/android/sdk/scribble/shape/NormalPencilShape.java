package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import com.hanvon.core.HWColorPaint;
import com.onyx.android.sdk.scribble.EPDRenderer;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 4/19/16.
 * One stroke
 */
public class NormalPencilShape extends EPDShape {

    private static boolean useHwColorPaint = false;

    public void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        super.onDown(normalizedPoint, screenPoint);
        EPDRenderer.moveTo(screenPoint.x, screenPoint.y, getStrokeWidth());
    }

    public void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        super.onMove(normalizedPoint, screenPoint);
        EPDRenderer.quadTo(screenPoint.x, screenPoint.y, updateMode);
    }

    public void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        super.onUp(normalizedPoint, screenPoint);
        EPDRenderer.quadTo(screenPoint.x, screenPoint.y, updateMode);
    }

    public void render(final Canvas canvas, final Paint paint, final Matrix matrix) {
        if (useHwColorPaint) {
            renderByHWColorPaint();
        } else {
            renderByDefault(canvas, paint, matrix);
        }
    }

    private void renderByDefault(final Canvas canvas, final Paint paint, final Matrix matrix) {
        applyStrokeStyle(paint);
        Path path = getDisplayPath();
        if (path == null) {
            path = ShapeUtils.renderShape(canvas, paint, matrix, getNormalizedPoints());
            setDisplayPath(path);
        }
        canvas.drawPath(path, paint);
    }

    private void renderByHWColorPaint() {
        HWColorPaint.setPen(0, 3, 6, 3, 12);
        HWColorPaint.setSimulatePressure(true);

        float[] points = new float[2048];
        int[] rect = new int[4];
        for(TouchPoint touchPoint : getNormalizedPoints().getPoints()) {
            HWColorPaint.drawLineEx((int)touchPoint.x, (int)touchPoint.y, touchPoint.pressure / 1024f, rect, points);
        }
        HWColorPaint.drawLineEx(-1, -1, 0, rect, points);
    }

}
