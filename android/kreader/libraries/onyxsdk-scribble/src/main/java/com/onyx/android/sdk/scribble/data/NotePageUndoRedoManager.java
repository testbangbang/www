package com.onyx.android.sdk.scribble.data;

import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;

/**
 * Created by zhuzeng on 7/19/16.
 */
public class NotePageUndoRedoManager {

    public static void undo(final NotePage notePage, final UndoRedoManager undoRedoManager, final boolean isLineLayoutMode) {
        final UndoRedoManager.Action<List<Shape>> action = undoRedoManager.undo();
        if (action == null) {
            return;
        }
        if (action.getObject().get(0).isFreePosition() == isLineLayoutMode) {
            return;
        }
        switch (action.getActionName()) {
            case ShapeActions.ACTION_ADD_SHAPE:
                notePage.removeShape(action.getObject().get(0), false);
                break;
            case ShapeActions.ACTION_ADD_SHAPE_LIST:
                notePage.removeShapeList(action.getObject(), false);
                break;
            case ShapeActions.ACTION_REMOVE_SHAPE:
                notePage.addShape(action.getObject().get(0), false);
                break;
            case ShapeActions.ACTION_REMOVE_SHAPE_LIST:
                notePage.addShapeList(action.getObject(), false);
                break;
        }
    }

    public static void redo(final NotePage notePage, final UndoRedoManager undoRedoManager, final boolean isLineLayoutMode) {
        final UndoRedoManager.Action<List<Shape>> action = undoRedoManager.redo();
        if (action == null) {
            return;
        }
        if (action.getObject().get(0).isFreePosition() == isLineLayoutMode) {
            return;
        }
        switch (action.getActionName()) {
            case ShapeActions.ACTION_ADD_SHAPE:
                notePage.addShape(action.getObject().get(0), false);
                break;
            case ShapeActions.ACTION_ADD_SHAPE_LIST:
                notePage.addShapeList(action.getObject(), false);
                break;
            case ShapeActions.ACTION_REMOVE_SHAPE:
                notePage.removeShape(action.getObject().get(0), false);
                break;
            case ShapeActions.ACTION_REMOVE_SHAPE_LIST:
                notePage.removeShapeList(action.getObject(), false);
                break;
        }
    }

}
