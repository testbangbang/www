package com.onyx.android.sdk.scribble.shape;

import android.graphics.Path;

import com.hanvon.core.Algorithm;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 4/19/16.
 * One stroke
 */
public class NormalPencilShape extends EPDShape {

    private static boolean useHwColorPaint = false;

    public int getType() {
        return ShapeFactory.SHAPE_PENCIL_SCRIBBLE;
    }

    public void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        super.onDown(normalizedPoint, screenPoint);
    }

    public void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        super.onMove(normalizedPoint, screenPoint);
    }

    public void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        super.onUp(normalizedPoint, screenPoint);
    }

    public void render(final RenderContext renderContext) {
        if (useHwColorPaint) {
            renderByHWColorPaint();
        } else {
            renderByDefault(renderContext);
        }
    }

    private void renderByDefault(final RenderContext renderContext) {
        applyStrokeStyle(renderContext.paint, getDisplayScale(renderContext));
        Path path = getOriginDisplayPath();
        if (path == null || renderContext.force) {
            path = ShapeUtils.renderShape(renderContext, getNormalizedPoints());
            setOriginDisplayPath(path);
        }
        if (path == null) {
            return;
        }
        renderContext.canvas.drawPath(path, renderContext.paint);
    }

    private void renderByHWColorPaint() {
        Algorithm.setPen(0, 3, 6, 3, 12);
        Algorithm.setSimulatePressure(true);

        float[] points = new float[2048];
        int[] rect = new int[4];
        for(TouchPoint touchPoint : getNormalizedPoints().getPoints()) {
            Algorithm.drawLineEx((int) touchPoint.x, (int) touchPoint.y, touchPoint.pressure / 1024f, rect, points);
        }
        Algorithm.drawLineEx(-1, -1, 0, rect, points);
    }

}
