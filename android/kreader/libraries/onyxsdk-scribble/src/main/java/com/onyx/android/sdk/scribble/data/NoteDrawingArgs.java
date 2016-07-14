package com.onyx.android.sdk.scribble.data;

import android.graphics.Color;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

/**
 * Created by zhuzeng on 7/2/16.
 */
public class NoteDrawingArgs {

    public volatile float strokeWidth = 15.0f;
    public volatile int strokeColor = Color.BLACK;
    public volatile int style;
    public volatile int currentShapeType = ShapeFactory.SHAPE_NORMAL_SCRIBBLE;
    public volatile float eraserRadius = 15.0f;
    public volatile int background;

    public void syncFrom(final NoteDrawingArgs other) {
        strokeWidth = other.strokeWidth;
        strokeColor = other.strokeColor;
        style = other.style;
        currentShapeType = other.currentShapeType;
        eraserRadius = other.eraserRadius;
        background = other.background;
    }

}
