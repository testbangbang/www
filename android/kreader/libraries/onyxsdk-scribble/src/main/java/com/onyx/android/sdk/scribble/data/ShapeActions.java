package com.onyx.android.sdk.scribble.data;

import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/7/16.
 */
public class ShapeActions {

    public static final String ACTION_ADD_SHAPE = "addShape";
    public static final String ACTION_REMOVE_SHAPE = "removeShape";
    public static final String ACTION_REMOVE_SHAPE_LIST = "removeShapeList";
    public static final String ACTION_ADD_SHAPE_LIST = "addShapeList";
    public static final String ACTION_TRANSFORM_SHAPE = "transformShape";
    public static final String ACTION_TRANSFORM_SHAPE_LIST = "transformShapeList";

    public static UndoRedoManager.Action<List<Shape>> addShapeAction(final Shape shape) {
        List<Shape>list = new ArrayList<>();
        list.add(shape);
        return new UndoRedoManager.Action<>(ACTION_ADD_SHAPE, list);
    }

    public static UndoRedoManager.Action<List<Shape>> removeShapeAction(final Shape shape) {
        List<Shape>list = new ArrayList<>();
        list.add(shape);
        return new UndoRedoManager.Action<>(ACTION_REMOVE_SHAPE, list);
    }

    public static UndoRedoManager.Action<List<Shape>> addShapeListAction(final List<Shape> shapeList) {
        List<Shape>list = new ArrayList<>();
        list.addAll(shapeList);
        return new UndoRedoManager.Action<>(ACTION_ADD_SHAPE_LIST, list);
    }

    public static UndoRedoManager.Action<List<Shape>> removeShapeListAction(final List<Shape> shapeList) {
        List<Shape>list = new ArrayList<>();
        list.addAll(shapeList);
        return new UndoRedoManager.Action<>(ACTION_REMOVE_SHAPE_LIST, list);
    }

    public static UndoRedoManager.Action<List<Shape>> transformShapeAction(final Shape shape) {
        List<Shape> list = new ArrayList<>();
        list.add(shape);
        return new UndoRedoManager.Action<>(ACTION_TRANSFORM_SHAPE, list);
    }

    public static UndoRedoManager.Action<List<Shape>> transformShapeListAction(final List<Shape> shapeList) {
        List<Shape> list = new ArrayList<>();
        list.addAll(shapeList);
        return new UndoRedoManager.Action<>(ACTION_TRANSFORM_SHAPE, list);
    }

}
