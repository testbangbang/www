package com.onyx.android.sdk.scribble.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class SortBy {
    public static final int CREATED_AT = 0;
    public static final int UPDATED_AT = 1;
    public static final int TITLE = 2;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({CREATED_AT, UPDATED_AT, TITLE})
    // Create an interface for validating int types
    public @interface SortByDef {
    }


    public
    @SortByDef
    static int translate(int val) {
        return val;
    }
}
