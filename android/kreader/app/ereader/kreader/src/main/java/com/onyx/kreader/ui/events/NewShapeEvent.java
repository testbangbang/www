package com.onyx.kreader.ui.events;

import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class NewShapeEvent {

    public Shape shape;
    public NewShapeEvent(final Shape s) {
        shape = s;
    }
}
