package com.onyx.android.sdk.reader.tests;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.reader.utils.HistoryManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zengzhu on 2/21/16.
 */
public class ReaderUndoRedoTest extends ApplicationTestCase<Application> {

    static private String TAG = ReaderUndoRedoTest.class.getSimpleName();


    public ReaderUndoRedoTest() {
        super(Application.class);
    }

    public void testUndo1() {
        HistoryManager manager = new HistoryManager();
        final String cmd = "1";
        final String cmd2 = "2";
        manager.addToHistory(cmd, false);
        manager.addToHistory(cmd2, false);

        for(int i = 0; i < 10; ++i) {
            String result = manager.undo();
            assertTrue(result.equals(cmd));
            assertNull(manager.undo());

            result = manager.redo();
            assertTrue(result.equals(cmd2));
            assertNull(manager.redo());
        }
    }

    public void testUndo2() {
        HistoryManager manager = new HistoryManager();
        int limit = 10;
        for(int i = 0; i < limit; ++i) {
            manager.addToHistory(String.valueOf(i), false);
        }

        for(int i = limit - 1; i >= 1; --i) {
            String result = manager.undo();
            assertTrue(result.equals(String.valueOf(i - 1)));
        }

        for(int i = 0; i < limit - 2; ++i) {
            String result = manager.redo();
            assertTrue(result.equals(String.valueOf(i + 1)));
        }
    }

    public void testUndo3() {
        HistoryManager manager = new HistoryManager();
        int limit = 10;
        for(int i = 0; i < limit; ++i) {
            manager.addToHistory(String.valueOf(i), false);
        }

        Set<String> set = new HashSet<String>();
        for(int i = limit - 1; i >= limit / 2; --i) {
            String result = manager.undo();
            assertTrue(result.equals(String.valueOf(i - 1)));
            set.add(result);
        }
        set.remove(manager.getCurrent());

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