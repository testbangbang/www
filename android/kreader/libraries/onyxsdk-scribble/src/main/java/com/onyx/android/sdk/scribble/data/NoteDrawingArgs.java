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
        PEN_USER_ERASING, PenState,           // in user erasing state
    }

    public volatile float strokeWidth = NoteModel.getDefaultStrokeWidth();
    public volatile int strokeColor = Color.BLACK;
    public volatile int style;
    public volatile int currentShapeType = ShapeFactory.SHAPE_PENCIL_SCRIBBLE;
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

}
