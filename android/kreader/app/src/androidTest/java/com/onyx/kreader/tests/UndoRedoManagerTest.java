package com.onyx.kreader.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.scribble.data.UndoRedoManager;
import com.onyx.kreader.scribble.shape.NormalShape;
import com.onyx.kreader.scribble.shape.Shape;

/**
 * Created by zhuzeng on 6/6/16.
 */
public class UndoRedoManagerTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public UndoRedoManagerTest() {
        super(ReaderTestActivity.class);
    }

    public void testUndoRedo1() {
        UndoRedoManager undoRedoManager = new UndoRedoManager();
        final Shape shape = new NormalShape();
        UndoRedoManager.Action<Shape> action = new UndoRedoManager.Action<>("add", shape);
        undoRedoManager.addToHistory(action, false);
        final UndoRedoManager.Action undo = undoRedoManager.undo();
        assertNotNull(undo);
        assertEquals(action.actionName, undo.actionName);
        assertTrue(action.object == undo.object);

        final UndoRedoManager.Action redo = undoRedoManager.redo();
        assertNotNull(redo);
        assertEquals(action.actionName, redo.actionName);
        assertTrue(action.object == redo.object);

    }
}
