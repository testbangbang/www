package com.onyx.android.sdk.scribble.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 16/7/1 16:58.
 */

public class NoteBackgroundType {
    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({EMPTY, GRID, LINE, MATS, MUSIC, ENGLISH, TABLE, COLUMN, LEFT_GRID,
            GRID_POINT, GRID_5_5, LINE_1_6, LINE_2_0, CALENDAR, FILE})
    // Create an interface for validating int types
    public @interface NoteBackgroundDef {
    }

    public static final int EMPTY = 0;
    public static final int GRID = 1;
    public static final int LINE = 2;
    public static final int MATS = 3;
    public static final int MUSIC = 4;
    public static final int ENGLISH = 5;
    public static final int TABLE = 6;
    public static final int COLUMN = 7;
    public static final int LEFT_GRID = 8;
    public static final int GRID_POINT = 9;
    public static final int GRID_5_5 = 10;
    public static final int LINE_1_6 = 11;
    public static final int LINE_2_0 = 12;
    public static final int CALENDAR = 13;

    public static final int FILE = 26;

    public
    @NoteBackgroundDef
    static int translate(int val) {
        return val;
    }
}
