package com.onyx.android.sdk.scribble.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 16/8/4 18:30.
 */

public class ScribbleMode {
    public static final int MODE_NORMAL_SCRIBBLE = 0;
    public static final int MODE_SPAN_SCRIBBLE = 1;
    public static final int MODE_SHAPE_TRANSFORM = 2;
    public static final int MODE_PIC_EDIT = 3;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({MODE_NORMAL_SCRIBBLE, MODE_SPAN_SCRIBBLE, MODE_SHAPE_TRANSFORM, MODE_PIC_EDIT})
    // Create an interface for validating int types
    public @interface ScribbleModeDef {
    }

    public
    @ScribbleModeDef
    static int translate(int val) {
        return val;
    }
}