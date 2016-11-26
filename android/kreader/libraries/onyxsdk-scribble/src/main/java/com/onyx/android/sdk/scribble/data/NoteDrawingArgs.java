package com.onyx.android.sdk.scribble.data;

import android.graphics.Color;
import com.onyx.android.sdk.scribble.NoteViewHelper;
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
    }

    public volatile float strokeWidth = NoteModel.getDefaultStrokeWidth();
    public volatile int strokeColor = Color.BLACK;
    public volatile int style;
    private volatile int lastShapeType = ShapeFactory.SHAPE_INVALID;
    private volatile int currentShapeType = defaultShape();
    public volatile float eraserRadius = 15.0f;
    public volatile int background;
    public volatile PenState penState;

    public void syncFrom(final NoteDrawingArgs other) {
        strokeWidth = other.strokeWidth;
        strokeColor = other.strokeColor;
        style = other.style;
        currentShapeType = other.currentShapeType;
        eraserRadius = other.eraserRadius;
        background = other.background;
        penState = other.penState;
    }

    public static int defaultShape() {
        return ShapeFactory.SHAPE_PENCIL_SCRIBBLE;
    }

    public void setCurrentShapeType(int newShape) {
        lastShapeType = currentShapeType;
        currentShapeType = newShape;
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
    }
}
