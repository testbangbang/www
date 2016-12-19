package com.onyx.android.sdk.scribble.shape;

import android.graphics.Path;
import android.util.Log;

import com.onyx.android.sdk.scribble.EPDRenderer;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.InkUtils;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

import java.util.List;

/**
 * Created by zhuzeng on 4/21/16.
 */
public class BrushScribbleShape extends EPDShape  {

    private float lastStrokeWidth;
    private float lastX, lastY;
    private List<InkUtils.PathEntry> pathList;

    public int getType() {
        return ShapeFactory.SHAPE_BRUSH_SCRIBBLE;
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
        float strokeWidth = InkUtils.strokeWidth(normalizedPoint,
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
        float newStrokeWidth = InkUtils.strokeWidth(normalizedPoint, getDisplayStrokeWidth(), getLastStrokeWidth());
        if (InkUtils.inRange(getLastStrokeWidth(), newStrokeWidth)) {
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
        if (pathList == null) {
            pathList = InkUtils.generate(renderContext, this);
        }
        for(InkUtils.PathEntry entry : pathList) {
            renderContext.paint.setStrokeWidth(entry.pathWidth * getDisplayScale(renderContext));
            renderContext.canvas.drawPath(entry.path, renderContext.paint);
        }
    }

}
