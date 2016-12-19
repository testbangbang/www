package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;
import android.util.Log;

import java.util.List;

/**
 * Created by zhuzeng on 8/6/16.
 */
public class ShapeSpan extends ReplacementSpan {
    static final String TAG = ShapeSpan.class.getSimpleName();
    public static int SHAPE_SPAN_MARGIN = 3;
    private List<Shape> shapeList;
    private float scale = 1.0f;
    private int width = 1;
    private boolean needUpdateShape = false;

    public ShapeSpan(final List<Shape> s) {
        shapeList = s;
    }

    public ShapeSpan(List<Shape> shapeList, boolean needUpdateShape) {
        this.shapeList = shapeList;
        this.needUpdateShape = needUpdateShape;
    }

    public final List<Shape> getShapeList() {
        return shapeList;
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (fm == null) {
            return width;
        }
        float height = fm.bottom - fm.top - 2 * SHAPE_SPAN_MARGIN;
        RectF rect = boundingRect();
        scale = height / rect.height();
        if (scale > 1.0f) {
            scale = Math.min(height / rect.width(), scale);
        }
        width = (int)(rect.width() * scale) + 2 * SHAPE_SPAN_MARGIN;
        return width;
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        final Matrix matrix = new Matrix();
        final RectF rect = boundingRect();

        float translateX = x + SHAPE_SPAN_MARGIN - rect.left  * scale;
        float translateY = top - rect.top * scale + (bottom - top - rect.height() * scale);
        if (needUpdateShape) {
            matrix.postScale(scale, scale);
            matrix.postTranslate(translateX, translateY);
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1.0f * scale);
        RenderContext renderContext = RenderContext.create(canvas, paint, matrix);
        for (Shape shape : shapeList) {
            shape.render(renderContext);
            if (shape.getType() == ShapeFactory.SHAPE_TEXT) {
                shape.getShapeExtraAttributes().setTextSize(paint.getTextSize());
            }
            if (needUpdateShape) {
                shape.getPoints().scaleAllPoints(scale);
                shape.onTranslate(translateX, translateY);
            }
        }
        needUpdateShape = false;
    }

    private RectF boundingRect() {
        RectF rect = new RectF();
        for (Shape shape : shapeList) {
            rect.union(shape.getBoundingRect());
        }
        if (rect.isEmpty() && shapeList.size() > 0) {
            rect.set(shapeList.get(0).getBoundingRect());
        }
        return rect;
    }

    public float getScale() {
        return scale;
    }
}
