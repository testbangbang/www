package com.onyx.android.sdk.scribble.shape;

import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.InkUtils;

/**
 * Created by zhuzeng on 4/21/16.
 */
public class BrushScribbleShape extends EPDShape  {

    public int getType() {
        return ShapeFactory.SHAPE_BRUSH_SCRIBBLE;
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

    // render path with width list and generate path list.
    public void render(final RenderContext renderContext) {
        applyStrokeStyle(renderContext.paint, getDisplayScale(renderContext));
        InkUtils.drawStroke(renderContext.canvas, renderContext.paint, getPoints().getPoints(), renderContext.matrix);
    }

}
