package com.onyx.android.sdk.ui.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SelectionMode {
    public static final int NORMAL_MODE = 0;
    public static final int PASTE_MODE = 1;
    public static final int MULTISELECT_MODE = 2;
    public static final int LONG_PRESS_MODE = 3;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({NORMAL_MODE, PASTE_MODE, MULTISELECT_MODE, LONG_PRESS_MODE})
    // Create an interface for validating int types
    public @interface SelectionModeDef {
    }
}
