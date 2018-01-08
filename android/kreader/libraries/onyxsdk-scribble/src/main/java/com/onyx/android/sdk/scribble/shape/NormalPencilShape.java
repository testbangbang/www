package com.onyx.android.sdk.scribble.shape;

import android.graphics.Path;

import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 4/19/16.
 * One stroke
 */
public class NormalPencilShape extends EPDShape {

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
        renderByDefault(renderContext);
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

}
