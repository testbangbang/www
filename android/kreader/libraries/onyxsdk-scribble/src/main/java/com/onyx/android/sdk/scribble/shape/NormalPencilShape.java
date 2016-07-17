package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 4/19/16.
 * One stroke
 */
public class NormalPencilShape extends EPDShape {

    public void render(final Canvas canvas, final Paint paint, final Matrix matrix) {
        applyStrokeStyle(paint);
        Path path = getDisplayPath();
        if (path == null) {
            path = ShapeUtils.renderShape(canvas, paint, matrix, getNormalizedPoints());
            setDisplayPath(path);
        }
        canvas.drawPath(path, paint);
    }

}
