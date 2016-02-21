package com.onyx.kreader.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.utils.StringUtils;
import com.onyx.kreader.utils.UndoRedoManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zengzhu on 2/21/16.
 */
public class ReaderUndoRedoTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    static private String TAG = ReaderUndoRedoTest.class.getSimpleName();


    public ReaderUndoRedoTest() {
        super(ReaderTestActivity.class);
    }

    public void testUndo1() {
        UndoRedoManager manager = new UndoRedoManager();
        final String cmd = "1";
        manager.addToHistory(cmd, false);

        for(int i = 0; i < 10; ++i) {
            String result = manager.undo();
            assertTrue(result.equals(cmd));
            assertNull(manager.undo());

            result = manager.redo();
            assertTrue(result.equals(cmd));
            assertNull(manager.redo());
        }
    }

    public void testUndo2() {
        UndoRedoManager manager = new UndoRedoManager();
        int limit = 10;
        for(int i = 0; i < limit; ++i) {
            manager.addToHistory(String.valueOf(i), false);
        }

        for(int i = limit - 1; i >= 0; --i) {
            String result = manager.undo();
            assertTrue(result.equals(String.valueOf(i)));
        }

        for(int i = 0; i < limit; ++i) {
            String result = manager.redo();
            assertTrue(result.equals(String.valueOf(i)));
        }
    }

    public void testUndo3() {
        UndoRedoManager manager = new UndoRedoManager();
        int limit = 10;
        for(int i = 0; i < limit; ++i) {
            manager.addToHistory(String.valueOf(i), false);
        }

        Set<String> set = new HashSet<String>();
        for(int i = limit - 1; i >= limit / 2; --i) {
            String result = manager.undo();
            assertTrue(result.equals(String.valueOf(i)));
            set.add(result);
        }

        for(int i = limit; i < limit * 2; ++i) {
            manager.addToHistory(String.valueOf(i), false);
        }
        assertNull(manager.redo());

        // it should not contain items in set.
        String item;
        while ((item = manager.undo()) != null) {
            assertFalse(set.contains(item));
        }

    }
}