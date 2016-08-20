package com.onyx.android.sdk.scribble.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class AscDescOrder {
    public static final int ASC = 0;
    public static final int DESC = 1;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({ASC, DESC})
    // Create an interface for validating int types
    public @interface AscDescOrderDef {
    }


    public
    @AscDescOrderDef
    static int translate(int val) {
        return val;
    }
}
