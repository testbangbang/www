package com.onyx.android.sdk.wifi;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 2016/12/2 15:44.
 */

public class PskType {
    public static final int UNKNOWN = 0;
    public static final int WPA = 1;
    public static final int WPA2 = 2;
    public static final int WPA_WPA2 = 3;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({UNKNOWN, WPA, WPA2, WPA_WPA2})
    // Create an interface for validating int types
    public @interface PskTypeDef {
    }

    public
    @PskTypeDef
    static int translate(int val) {
        return val;
    }
}
