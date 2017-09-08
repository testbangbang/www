package com.onyx.kreader.note.event;

import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by joy on 8/31/17.
 */

public class DFBShapeFinishedEvent {
    public Shape shape;
    public boolean triggerByButton;

    public DFBShapeFinishedEvent(final Shape shape, boolean triggerByButton) {
        this.shape = shape;
        this.triggerByButton = triggerByButton;
    }
}
