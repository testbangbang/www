package com.onyx.android.sdk.ui.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TouchDirection {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int BOTH = 2;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({HORIZONTAL, VERTICAL, BOTH})
    // Create an interface for validating int types
    public @interface TouchDirectionDef {
    }
}
