package com.onyx.android.sdk.scribble.data;

import android.util.Log;

import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.HashMap;
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
    public static final String TRANSFORM_ORIGINAL = "transform_original";
    public static final String TRANSFORM_FINISHED = "transform_finished";

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

    public static UndoRedoManager.Action<HashMap<String, List<Shape>>> transformShapeAction(final Shape originalShape,
                                                                                            final Shape transformShape) {
        List<Shape> originalShapeList = new ArrayList<>();
        List<Shape> transformShapeList = new ArrayList<>();
        originalShapeList.add(originalShape);
        transformShapeList.add(transformShape);
        HashMap<String, List<Shape>> transformShapeHashMap = new HashMap<>();
        transformShapeHashMap.put(TRANSFORM_ORIGINAL, originalShapeList);
        transformShapeHashMap.put(TRANSFORM_FINISHED, transformShapeList);
        return new UndoRedoManager.Action<>(ACTION_TRANSFORM_SHAPE, transformShapeHashMap);
    }

    public static UndoRedoManager.Action<HashMap<String, List<Shape>>> transformShapeListAction(final List<Shape> originalShapeList,
                                                                                                final List<Shape> transformShapeList) {
        List<Shape> originalList = new ArrayList<>();
        List<Shape> transformList = new ArrayList<>();
        originalList.addAll(originalShapeList);
        transformList.addAll(transformShapeList);
        HashMap<String, List<Shape>> transformShapeHashMap = new HashMap<>();
        Log.d("ShapeActions", "originalList.size():" + originalList.size());
        Log.d("ShapeActions", "transformList.size():" + transformList.size());
        transformShapeHashMap.put(TRANSFORM_ORIGINAL, originalList);
        transformShapeHashMap.put(TRANSFORM_FINISHED, transformList);
        return new UndoRedoManager.Action<>(ACTION_TRANSFORM_SHAPE_LIST, transformShapeHashMap);
    }

}
