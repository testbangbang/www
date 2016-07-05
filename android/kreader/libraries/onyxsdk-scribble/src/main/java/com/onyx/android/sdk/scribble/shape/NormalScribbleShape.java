package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.onyx.android.sdk.scribble.utils.ShapeUtils;

/**
 * Created by zhuzeng on 4/19/16.
 * One stroke
 */
public class NormalScribbleShape extends EPDShape {

    public void render(final Canvas canvas, final Paint paint, final Matrix matrix) {
        paint.setStrokeWidth(getStrokeWidth());
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);
        ShapeUtils.renderShape(canvas, paint, matrix, getNormalizedPoints());
    }

}
