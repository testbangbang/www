package com.onyx.android.note.utils;

import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

/**
 * Created by lxm on 2018/3/6.
 */

public class DrawUtils {

    public static Shape createShape(NoteDrawingArgs drawingArgs, int layoutType) {
        Shape shape = ShapeFactory.createShape(drawingArgs.getCurrentShapeType());
        shape.setStrokeWidth(drawingArgs.strokeWidth);
        shape.setColor(drawingArgs.getStrokeColor());
        shape.setLayoutType(layoutType);
        return shape;
    }
}
