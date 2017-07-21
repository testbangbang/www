package com.onyx.edu.note.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 16/8/4 15:58.
 */

public class ScribbleFunctionBarMenuID {
    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({PEN_WIDTH, PEN_STYLE, ERASER, BG, COLOR, DELETE, SPACE, ENTER,
            KEYBOARD, ADD_PAGE, DELETE_PAGE, PREV_PAGE, NEXT_PAGE})
    // Create an interface for validating int types
    public @interface ScribbleFunctionBarMenuDef {
    }

    public static final int PEN_WIDTH = 0;
    public static final int PEN_STYLE = 1;
    public static final int ERASER = 2;
    public static final int BG = 3;
    public static final int COLOR = 4;
    public static final int DELETE = 5;
    public static final int SPACE = 6;
    public static final int ENTER = 7;
    public static final int KEYBOARD = 8;
    public static final int ADD_PAGE = 9;
    public static final int DELETE_PAGE = 10;
    public static final int PREV_PAGE = 11;
    public static final int NEXT_PAGE = 12;


    public
    @ScribbleFunctionBarMenuDef
    static int translate(int val) {
        return val;
    }
}
