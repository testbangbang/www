package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhuzeng on 8/6/16.
 */
public class ShapeSpan extends ReplacementSpan {
    static final String TAG = ShapeSpan.class.getSimpleName();
    private List<Shape> shapeList;
    private int margin = 5;
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
        float height = fm.bottom - fm.top - 2 * margin;
        RectF rect = boundingRect();
        scale = height / rect.height();
        if (scale > 1.0f) {
            scale = Math.min(height / rect.width(), scale);
        }
        width = (int)(rect.width() * scale) + 2 * margin;
        return width;
    }

    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        final Matrix matrix = new Matrix();
        final RectF rect = boundingRect();

        float translateX = x + margin - rect.left  * scale;
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
