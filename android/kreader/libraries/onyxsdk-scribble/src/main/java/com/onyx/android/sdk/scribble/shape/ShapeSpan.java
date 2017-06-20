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

    public interface Callback {
        void onFinishDrawShapes(List<Shape> shapes);
    }

    public static int SHAPE_SPAN_MARGIN = 5;
    public static float HEIGHT_SCALE_LIMIT_RANGE = 5.0f;
    private List<Shape> shapeList;
    private float scale = 1.0f;
    private int width = 1;
    private boolean needUpdateShape = false;
    public ShapeSpan(final List<Shape> s) {
        shapeList = s;
    }
    private Callback callback;

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
        RectF rect = boundingRect();
        if (needUpdateShape) {
            float height = fm.bottom - fm.top - 2 * SHAPE_SPAN_MARGIN;
            scale = height / rect.height();
            if (scale > HEIGHT_SCALE_LIMIT_RANGE) {
                scale = Math.min(height / rect.width(), scale);
            }
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
        for (Shape shape : shapeList) {
            if (shape.getType() == ShapeFactory.SHAPE_TEXT) {
                shape.getShapeExtraAttributes().setTextSize(paint.getTextSize());
            }
            if (needUpdateShape) {
                shape.getPoints().scaleAllPoints(scale);
                shape.onTranslate(translateX, translateY);
            }
        }
        if (callback != null) {
            callback.onFinishDrawShapes(shapeList);
            clearCallback();
        }
        needUpdateShape = false;
    }

    private void clearCallback() {
        callback = null;
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

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}
