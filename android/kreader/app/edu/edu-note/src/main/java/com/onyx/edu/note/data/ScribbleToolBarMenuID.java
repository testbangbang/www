package com.onyx.edu.note.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 16/8/4 15:58.
 */

public class ScribbleToolBarMenuID {
    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({SWITCH_SCRIBBLE_MODE, REDO, UNDO, SAVE, SETTING, EXPORT})
    // Create an interface for validating int types
    public @interface ScribbleToolBarMenuDef {
    }

    public static final int SWITCH_SCRIBBLE_MODE = 0;
    public static final int UNDO = 1;
    public static final int REDO = 2;
    public static final int SAVE = 3;
    public static final int SETTING = 4;
    public static final int EXPORT = 5;


    public
    @ScribbleToolBarMenuDef
    static int translate(int val) {
        return val;
    }
}
