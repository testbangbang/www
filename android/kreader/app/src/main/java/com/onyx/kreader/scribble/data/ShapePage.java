package com.onyx.kreader.scribble.data;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.onyx.kreader.scribble.shape.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 4/23/16.
 * Manager for a list of shapes in single page.
 * To make it easy for activity or other class to manage shapes.
 */
public class ShapePage {

    private boolean dirty = false;
    private String uniqueName;
    private List<Shape> shapeList = new ArrayList<Shape>();
    private float pageX, pageY;
    private float displayScale = 1.0f;
    static private final float src[] = new float[2];
    static private final float dst[] = new float[2];
    private int currentShapeType;
    private Shape currentShape;

    public final String getUniqueName() {
        return uniqueName;
    }

    public void addShape(final Shape shape) {
        shapeList.add(shape);
    }

    public final List<Shape> getShapeList() {
        return shapeList;
    }

    public final List<Shape> deatchShapeList() {
        final List<Shape> list = shapeList;
        shapeList = null;
        return list;
    }

    public void setPageDisplayPosition(float px, float py) {
        pageX = px;
        pageY = py;
    }

    public void setDisplayScale(final float scale) {
        displayScale = scale;
    }

    public final TouchPoint normalizedTouchPoint(final float x, final float y, final float pressure, final float size, final long timestamp) {
        return new TouchPoint(x - pageX, y - pageX, pressure, size, timestamp);
    }

    public void render(final Bitmap bitmap, final Paint paint) {
        if (shapeList == null) {
            return;
        }
        Canvas canvas = new Canvas(bitmap);
        for(Shape shape : shapeList) {
            shape.render(null, canvas, paint);
        }
    }

    public void prepareShapePool(int shapeType) {
        currentShapeType = shapeType;
    }

    // create a new shape if not exist and make it as current shape.
    public final Shape getShapeFromPool() {
        switch (currentShapeType) {
            case ShapeFactory.SHAPE_NORMAL_SCRIBBLE:
                currentShape = new NormalScribbleShape();
                break;
            case ShapeFactory.SHAPE_CIRCLE:
                currentShape = new CircleShape();
                break;
            case ShapeFactory.SHAPE_RECTANGLE:
                currentShape = new RectangleShape();
                break;
            case ShapeFactory.SHAPE_TEXT:
                currentShape = new TexShape();
                break;
            case ShapeFactory.SHAPE_VARY_SCRIBBLE:
                currentShape = new BrushScribbleShape();
                break;
        }
        return currentShape;
    }

    public final Shape getCurrentShape() {
        return currentShape;
    }

}
