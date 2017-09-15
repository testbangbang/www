package com.onyx.kreader.note.event;

import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by joy on 8/31/17.
 */

public class DFBShapeFinishedEvent {
    public Shape shape;
    public boolean shortcut;

    public DFBShapeFinishedEvent(final Shape shape, boolean shortcut) {
        this.shape = shape;
        this.shortcut = shortcut;
    }
}
