package com.onyx.android.sdk.scribble.data;

import android.util.Log;

import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zhuzeng on 7/19/16.
 */
public class NotePageUndoRedoManager {
    private static final String TAG = NotePageUndoRedoManager.class.getSimpleName();

    public static void undo(final NotePage notePage, final UndoRedoManager undoRedoManager, final boolean isLineLayoutMode) {
        final UndoRedoManager.Action action = undoRedoManager.undo();
        List<Shape> normalObject = null;
        HashMap<String,List<Shape>> transformObject = null;
        if (action == null) {
            return;
        }
        if (action.getObject() instanceof List){
            Log.e(TAG, "undo: action.getObject() instanceof List" );
            normalObject = (List<Shape>) action.getObject();
        }
        if (action.getObject() instanceof HashMap){
            Log.e(TAG, "undo: action.getObject() instanceof HashMap" );
            transformObject = (HashMap<String,List<Shape>>) action.getObject();
        }
        if (normalObject != null) {
            if (normalObject.get(0).isFreePosition() == isLineLayoutMode) {
                return;
            }
        }
        switch (action.getActionName()) {
            case ShapeActions.ACTION_ADD_SHAPE:
                if (normalObject != null) {
                    notePage.removeShape(normalObject.get(0), false);
                }
                break;
            case ShapeActions.ACTION_ADD_SHAPE_LIST:
                if (normalObject != null) {
                    notePage.removeShapeList(normalObject, false);
                }
                break;
            case ShapeActions.ACTION_REMOVE_SHAPE:
                if (normalObject != null) {
                    notePage.addShape(normalObject.get(0), false);
                }
                break;
            case ShapeActions.ACTION_REMOVE_SHAPE_LIST:
                if (normalObject != null) {
                    notePage.addShapeList(normalObject, false);
                }
                break;
            case ShapeActions.ACTION_TRANSFORM_SHAPE:
                if (transformObject != null) {
                    notePage.removeShape(transformObject.get(ShapeActions.TRANSFORM_FINISHED).get(0), false);
                    notePage.addShape(transformObject.get(ShapeActions.TRANSFORM_ORIGINAL).get(0), false);
                }
                break;
            case ShapeActions.ACTION_TRANSFORM_SHAPE_LIST:
                if (transformObject != null) {
                    notePage.removeShapeList(transformObject.get(ShapeActions.TRANSFORM_FINISHED), false);
                    notePage.addShapeList(transformObject.get(ShapeActions.TRANSFORM_ORIGINAL), false);
                }
                break;
        }
    }

    public static void redo(final NotePage notePage, final UndoRedoManager undoRedoManager, final boolean isLineLayoutMode) {
        final UndoRedoManager.Action action = undoRedoManager.redo();
        List<Shape> normalObject = null;
        HashMap<String,List<Shape>> transformObject = null;
        if (action == null) {
            return;
        }
        if (action.getObject() instanceof List){
            Log.e(TAG, "undo: action.getObject() instanceof List" );
            normalObject = (List<Shape>) action.getObject();
        }
        if (action.getObject() instanceof HashMap){
            Log.e(TAG, "undo: action.getObject() instanceof HashMap" );
            transformObject = (HashMap<String,List<Shape>>) action.getObject();
        }
        if (normalObject != null) {
            if (normalObject.get(0).isFreePosition() == isLineLayoutMode) {
                return;
            }
        }
        switch (action.getActionName()) {
            case ShapeActions.ACTION_ADD_SHAPE:
                if (normalObject != null) {
                    notePage.addShape(normalObject.get(0), false);
                }
                break;
            case ShapeActions.ACTION_ADD_SHAPE_LIST:
                if (normalObject != null) {
                    notePage.addShapeList(normalObject, false);
                }
                break;
            case ShapeActions.ACTION_REMOVE_SHAPE:
                if (normalObject != null) {
                    notePage.removeShape(normalObject.get(0), false);
                }
                break;
            case ShapeActions.ACTION_REMOVE_SHAPE_LIST:
                if (normalObject != null) {
                    notePage.removeShapeList(normalObject, false);
                }
                break;
            case ShapeActions.ACTION_TRANSFORM_SHAPE:
                if (transformObject != null) {
                    notePage.removeShape(transformObject.get(ShapeActions.TRANSFORM_ORIGINAL).get(0), false);
                    notePage.addShape(transformObject.get(ShapeActions.TRANSFORM_FINISHED).get(0), false);
                }
                break;
            case ShapeActions.ACTION_TRANSFORM_SHAPE_LIST:
                if (transformObject != null) {
                    notePage.removeShapeList(transformObject.get(ShapeActions.TRANSFORM_ORIGINAL), false);
                    notePage.addShapeList(transformObject.get(ShapeActions.TRANSFORM_FINISHED), false);
                }
                break;
        }
    }

}
