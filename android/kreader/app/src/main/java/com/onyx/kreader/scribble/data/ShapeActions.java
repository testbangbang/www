package com.onyx.kreader.scribble.data;

import com.onyx.kreader.scribble.shape.Shape;

/**
 * Created by zhuzeng on 6/7/16.
 */
public class ShapeActions {

    public static final String ACTION_ADD_SHAPE = "addShape";
    public static final String ACTION_REMOVE_SHAPE = "removeShape";

    public static UndoRedoManager.Action<Shape> addShapeAction(final Shape shape) {
        final UndoRedoManager.Action<Shape> action = new UndoRedoManager.Action<Shape>(ACTION_ADD_SHAPE, shape);
        return action;
    }

    public static UndoRedoManager.Action<Shape> removeShapeAction(final Shape shape) {
        final UndoRedoManager.Action<Shape> action = new UndoRedoManager.Action<Shape>(ACTION_REMOVE_SHAPE, shape);
        return action;
    }

}
