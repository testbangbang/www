package com.onyx.android.sdk.scribble.shape;

import android.graphics.Path;
import com.onyx.android.sdk.scribble.EPDRenderer;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 4/21/16.
 */
public class BrushScribbleShape extends EPDShape  {

    private float lastStrokeWidth;
    private float lastX, lastY;

    public static float pressureRatio(final float pressure) {
        if (pressure <= 128) {
            return 0.1f;
        } else if (pressure <= 256) {
            return 0.2f;
        } else if (pressure <= 512) {
            return 0.3f;
        } else if (pressure <= 640) {
            return 0.5f;
        }
        return 1.0f;
    }

    public static float strokeWidth(final TouchPoint normalizedPoint,
                                    final TouchPoint screenPoint,
                                    final float baseStrokeWidth,
                                    final float lastStrokeWidth) {
        float newStrokeWidth = pressureRatio(normalizedPoint.getPressure()) * baseStrokeWidth;
        return (lastStrokeWidth + newStrokeWidth) / 2;
    }

    public static boolean inRange(final float newStrokeWidth, final float lastStrokeWidth) {
        return Math.abs(newStrokeWidth - lastStrokeWidth) < 0.1f;
    }

    public float getLastStrokeWidth() {
        return lastStrokeWidth;
    }

    public void setLastStrokeWidth(float lastStrokeWidth) {
        this.lastStrokeWidth = lastStrokeWidth;
    }

    public void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        setPenDown();
        addDownPoint(normalizedPoint, screenPoint);
        float strokeWidth = strokeWidth(normalizedPoint, screenPoint,
                getDisplayStrokeWidth(),
                getDisplayStrokeWidth());
        EPDRenderer.moveTo(screenPoint.x, screenPoint.y, strokeWidth);
        setLastStrokeWidth(strokeWidth);
        lastX = screenPoint.getX();
        lastY = screenPoint.getY();
    }

    public void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        if (isPenNull()) {
            onDown(normalizedPoint, screenPoint);
        }
        setPenMove();
        addMovePoint(normalizedPoint, screenPoint);
        float newStrokeWidth = strokeWidth(normalizedPoint, screenPoint, getDisplayStrokeWidth(), getLastStrokeWidth());
        if (inRange(getLastStrokeWidth(), newStrokeWidth)) {
            EPDRenderer.quadTo(screenPoint.x, screenPoint.y, updateMode);
        } else {
            EPDRenderer.moveTo(lastX, lastY, newStrokeWidth);
            EPDRenderer.quadTo(screenPoint.x, screenPoint.y, updateMode);
            setLastStrokeWidth(newStrokeWidth);
        }
        lastX = screenPoint.getX();
        lastY = screenPoint.getY();
    }

    public void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        if (isPenMove()) {
            addUpPoint(normalizedPoint, screenPoint);
            EPDRenderer.quadTo(screenPoint.x, screenPoint.y, updateMode);
        }
        setPenNull();
    }

    // render path with width list and generate path list.
    public void render(final RenderContext renderContext) {
        applyStrokeStyle(renderContext.paint, getDisplayScale(renderContext));
        Path path = getOriginDisplayPath();
        if (path == null) {
            path = ShapeUtils.renderShape(renderContext, getNormalizedPoints());
            setOriginDisplayPath(path);
        }
        renderContext.canvas.drawPath(path, renderContext.paint);
    }

}
