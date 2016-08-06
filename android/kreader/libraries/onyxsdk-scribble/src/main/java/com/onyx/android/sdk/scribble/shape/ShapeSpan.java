package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

/**
 * Created by zhuzeng on 8/6/16.
 */
public class ShapeSpan extends ReplacementSpan {

    private Shape shape;

    public ShapeSpan(final Shape s) {
        shape = s;
    }

    public final Shape getShape() {
        return shape;
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        float height = fm.ascent - fm.bottom;
        float scale = shape.getBoundingRect().height() / height;
        return (int)(shape.getBoundingRect().width() * scale + 0.5);
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        final Matrix matrix = new Matrix();
        shape.render(canvas, paint, matrix);
    }


}
