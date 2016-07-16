package com.onyx.android.sdk.scribble.data;

import android.graphics.Color;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

/**
 * Created by zhuzeng on 7/2/16.
 */
public class NoteDrawingArgs {

    public static float defaultStrokeWidth() {
        return 3.0f;
    }
    public volatile float strokeWidth = defaultStrokeWidth();
    public volatile int strokeColor = Color.BLACK;
    public volatile int style;
    public volatile int currentShapeType = ShapeFactory.SHAPE_NORMAL_SCRIBBLE;
    public volatile float eraserRadius = 15.0f;
    public volatile int background;
    public volatile NoteViewHelper.PenState penState;

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
