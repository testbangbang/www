package com.onyx.android.sdk.scribble.data;

import com.onyx.android.sdk.scribble.shape.Shape;

/**
 * Created by zhuzeng on 7/19/16.
 */
public class NotePageUndoRedoManager {

    public static void undo(final NotePage notePage, final UndoRedoManager undoRedoManager) {
        final UndoRedoManager.Action<Shape> action = undoRedoManager.undo();
        if (action == null) {
            return;
        }
        if (ShapeActions.ACTION_ADD_SHAPE.equalsIgnoreCase(action.getActionName())) {
            notePage.removeShape(action.getObject(), false);
        } else if (ShapeActions.ACTION_REMOVE_SHAPE.equalsIgnoreCase(action.getActionName())) {
            notePage.addShape(action.getObject(), false);
        }
    }

    public static void redo(final NotePage notePage, final UndoRedoManager undoRedoManager) {
        final UndoRedoManager.Action<Shape> action =  undoRedoManager.redo();
        if (action == null) {
            return;
        }
        if (ShapeActions.ACTION_ADD_SHAPE.equalsIgnoreCase(action.getActionName())) {
            notePage.addShape(action.getObject(), false);
        } else if (ShapeActions.ACTION_REMOVE_SHAPE.equalsIgnoreCase(action.getActionName())) {
            notePage.removeShape(action.getObject(), false);
        }
    }

}
