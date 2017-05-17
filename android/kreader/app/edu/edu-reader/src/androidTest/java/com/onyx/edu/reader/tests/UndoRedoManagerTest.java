package com.onyx.edu.reader.tests;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by zhuzeng on 6/6/16.
 */
public class UndoRedoManagerTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public UndoRedoManagerTest() {
        super(ReaderTestActivity.class);
    }

//    public void testUndoRedo1() {
//        UndoRedoManager undoRedoManager = new UndoRedoManager();
//        final Shape shape = new NormalPencilShape();
//        UndoRedoManager.Action<Shape> action = new UndoRedoManager.Action<>("add", shape);
//        undoRedoManager.addToHistory(action, false);
//        final UndoRedoManager.Action undo = undoRedoManager.undo();
//        assertNotNull(undo);
//        assertEquals(action.actionName, undo.actionName);
//        assertTrue(action.object == undo.object);
//        assertFalse(undoRedoManager.canUndo());
//        assertTrue(undoRedoManager.canRedo());
//
//        final UndoRedoManager.Action redo = undoRedoManager.redo();
//        assertNotNull(redo);
//        assertEquals(action.actionName, redo.actionName);
//        assertTrue(action.object == redo.object);
//        assertTrue(undoRedoManager.canUndo());
//        assertFalse(undoRedoManager.canRedo());
//    }
//
//    public void testUndoRedo2() {
//        UndoRedoManager undoRedoManager = new UndoRedoManager();
//        final Shape shape = new NormalPencilShape();
//        UndoRedoManager.Action<Shape> action1 = new UndoRedoManager.Action<>("1", shape);
//        undoRedoManager.addToHistory(action1, false);
//
//        final Shape shape2 = new NormalPencilShape();
//        UndoRedoManager.Action<Shape> action2 = new UndoRedoManager.Action<>("2", shape2);
//        undoRedoManager.addToHistory(action2, false);
//
//
//        final UndoRedoManager.Action undo1 = undoRedoManager.undo();
//        assertNotNull(undo1);
//        assertEquals(action2.actionName, undo1.actionName);
//        assertTrue(action2.object == undo1.object);
//        assertTrue(undoRedoManager.canUndo());
//        assertTrue(undoRedoManager.canRedo());
//
//        final UndoRedoManager.Action undo2 = undoRedoManager.undo();
//        assertNotNull(undo2);
//        assertEquals(action1.actionName, undo2.actionName);
//        assertTrue(action1.object == undo2.object);
//        assertFalse(undoRedoManager.canUndo());
//        assertTrue(undoRedoManager.canRedo());
//
//        final UndoRedoManager.Action redo1 = undoRedoManager.redo();
//        assertNotNull(redo1);
//        assertEquals(action1.actionName, redo1.actionName);
//        assertTrue(action1.object == redo1.object);
//        assertTrue(undoRedoManager.canUndo());
//        assertTrue(undoRedoManager.canRedo());
//
//        final UndoRedoManager.Action redo2 = undoRedoManager.redo();
//        assertNotNull(redo2);
//        assertEquals(action2.actionName, redo2.actionName);
//        assertTrue(action2.object == redo2.object);
//        assertTrue(undoRedoManager.canUndo());
//        assertFalse(undoRedoManager.canRedo());
//
//
//    }
}
