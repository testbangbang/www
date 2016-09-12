package com.onyx.android.sdk.scribble;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.scribble.data.ShapeActions;
import com.onyx.android.sdk.scribble.data.UndoRedoManager;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.List;

/**
 * Created by zhuzeng on 7/19/16.
 */
public class UndoRedoTest extends ApplicationTestCase<Application> {


    public UndoRedoTest() {
        super(Application.class);
    }

    private Shape randomShape() {
        return ShapeFactory.createShape(TestUtils.randInt(ShapeFactory.SHAPE_CIRCLE, ShapeFactory.SHAPE_LINE));
    }

    public void testUndoRedo1() {
        UndoRedoManager undoRedoManager = new UndoRedoManager();
        final Shape shape = randomShape();
        UndoRedoManager.Action<List<Shape>> addShapeAction =  ShapeActions.addShapeAction(shape);
        undoRedoManager.addToHistory(addShapeAction, false);
        assertTrue(undoRedoManager.canUndo());

        UndoRedoManager.Action<List<Shape>> undoAction = undoRedoManager.undo();
        assertTrue(undoAction.getActionName().equalsIgnoreCase(addShapeAction.getActionName()));
        assertEquals(undoAction.getObject(), addShapeAction.getObject());

        assertFalse(undoRedoManager.canUndo());
        assertTrue(undoRedoManager.canRedo());

    }

}
