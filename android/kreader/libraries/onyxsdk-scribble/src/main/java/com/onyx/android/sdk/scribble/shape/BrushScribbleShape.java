package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import com.onyx.android.sdk.scribble.EPDRenderer;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 4/21/16.
 */
public class BrushScribbleShape extends EPDShape  {

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

    // render path with width list and generate path list.
    public void render(final Canvas canvas, final Paint paint, final Matrix matrix) {
        applyStrokeStyle(paint);
        Path path = getOriginDisplayPath();
        if (path == null) {
            path = ShapeUtils.renderShape(canvas, paint, matrix, getNormalizedPoints());
            setOriginDisplayPath(path);
        }
        canvas.drawPath(path, paint);
    }

}
