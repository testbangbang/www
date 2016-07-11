package com.onyx.android.sdk.scribble.data;

import android.graphics.Color;

/**
 * Created by zhuzeng on 7/2/16.
 */
public class NoteDrawingArgs {

    public volatile float strokeWidth;
    public volatile int strokeColor = Color.BLACK;
    public volatile int style;
    public volatile int currentShapeType;
    public volatile float eraserRadius = 15.0f;
    public volatile int background;

}
