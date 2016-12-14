package com.onyx.android.note.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 16/8/4 15:58.
 */

public class ScribbleMenuCategory {
    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({PEN_WIDTH, PEN_STYLE, ERASER, BG, COLOR, DELETE, SPACE, ENTER, KEYBOARD})
    // Create an interface for validating int types
    public @interface ScribbleMenuCategoryDef {
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


    public
    @ScribbleMenuCategoryDef
    static int translate(int val) {
        return val;
    }
}
