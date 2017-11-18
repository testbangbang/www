package com.onyx.creama.note.event;

import com.onyx.android.sdk.scribble.shape.ShapeFactory;

/**
 * Created by solskjaer49 on 2017/11/18 17:51.
 */

public class ShapeChangeEvent {
    private int shapeType = ShapeFactory.SHAPE_PENCIL_SCRIBBLE;

    public ShapeChangeEvent(int shapeType) {
        this.shapeType = shapeType;
    }

    public int getShapeType() {
        return shapeType;
    }
}
