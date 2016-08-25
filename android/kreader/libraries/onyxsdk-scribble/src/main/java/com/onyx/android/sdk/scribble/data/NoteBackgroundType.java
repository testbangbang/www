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
    @IntDef({EMPTY, GRID, LINE})
    // Create an interface for validating int types
    public @interface NoteBackgroundDef {
    }

    public static final int EMPTY = 0;
    public static final int GRID = 1;
    public static final int LINE = 2;

    public
    @NoteBackgroundDef
    static int translate(int val) {
        return val;
    }
}
