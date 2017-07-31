package com.onyx.android.sdk.scribble.data;

import android.graphics.Color;

import com.onyx.android.sdk.scribble.EPDRenderer;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

/**
 * Created by zhuzeng on 7/2/16.
 */
public class NoteDrawingArgs {

    public enum PenState {
        PEN_NULL,                   // not initialized yet.
        PEN_SCREEN_DRAWING,         // in direct screen drawing state, the input could be raw input or touch panel.
        PEN_CANVAS_DRAWING,         // in canvas drawing state
        PEN_USER_ERASING,           // in user erasing state
        PEN_SHAPE_SELECTING         // in user shape select state
    }

    public volatile float strokeWidth = NoteModel.getDefaultStrokeWidth();
    public volatile int strokeColor = defaultColor();
    public volatile int style;
    private volatile int lastShapeType = ShapeFactory.SHAPE_INVALID;
    private volatile int currentShapeType = defaultShape();
    public volatile float eraserRadius = NoteModel.getDefaultEraserRadius();
    public volatile int background;
    private volatile int lineLayoutBackground;
    public volatile PenState penState;
    public final static int MAX_STROKE_WIDTH = 20;

    //TODO:for picture edit.
    public volatile String bgFilePath;

    public void copyFrom(final NoteDrawingArgs other) {
        strokeWidth = other.strokeWidth;
        strokeColor = other.strokeColor;
        style = other.style;
        currentShapeType = other.currentShapeType;
        eraserRadius = other.eraserRadius;
        background = other.background;
        penState = other.penState;
        bgFilePath = other.bgFilePath;
        setLineLayoutBackground(other.getLineLayoutBackground());
    }

    public static int defaultShape() {
        return ShapeFactory.SHAPE_PENCIL_SCRIBBLE;
    }

    public static int defaultColor() {
        return Color.BLACK;
    }

    public void setCurrentShapeType(int newShape) {
        lastShapeType = currentShapeType;
        currentShapeType = newShape;
    }

    public void resetCurrentShapeType() {
        currentShapeType = defaultShape();
    }

    public int getLineLayoutBackground() {
        return lineLayoutBackground;
    }

    public void setLineLayoutBackground(int lineLayoutBackground) {
        this.lineLayoutBackground = lineLayoutBackground;
    }

    public int getCurrentShapeType() {
        return currentShapeType;
    }

    public int restoreCurrentShapeType() {
        currentShapeType = defaultShape();
        if (lastShapeType != ShapeFactory.SHAPE_INVALID) {
            currentShapeType = lastShapeType;
        }
        return currentShapeType;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        EPDRenderer.setStrokeColor(strokeColor);
    }

    public float getEraserRadius() {
        return eraserRadius;
    }

    public void setEraserRadius(float eraserRadius) {
        this.eraserRadius = eraserRadius;
    }
}
