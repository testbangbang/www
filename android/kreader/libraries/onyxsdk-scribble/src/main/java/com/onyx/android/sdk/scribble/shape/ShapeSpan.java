package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

import java.util.List;

/**
 * Created by zhuzeng on 8/6/16.
 */
public class ShapeSpan extends ReplacementSpan {
    static final String TAG = ShapeSpan.class.getSimpleName();
    private List<Shape> shapeList;

    public ShapeSpan(final List<Shape> s) {
        shapeList = s;
    }

    public final List<Shape> getShapeList() {
        return shapeList;
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return 100;
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        final Matrix matrix = new Matrix();
        final RectF rect = boundingRect();
        float xScale = Math.min(100 / rect.width(), 1.0f);
        float yScale = Math.min((bottom - top) / rect.height(), 1.0f);
        float scale = Math.min(xScale, yScale);

        matrix.postScale(scale, scale);
        matrix.postTranslate(x - rect.left * scale, - rect.top * scale + (bottom - top - rect.height() * scale));

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1.0f);
        for (Shape shape : shapeList) {
            shape.render(canvas, paint, matrix);
        }
        canvas.drawLine(x, bottom, x + 100, bottom, paint);
    }

    private RectF boundingRect() {
        RectF rect = new RectF();
        for (Shape shape : shapeList) {
            rect.union(shape.getBoundingRect());
        }
        return rect;

    }

}
