package com.onyx.android.sdk.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 2016/12/9 10:30.
 */

public class DeviceType {
    public static final int IMX6 = 0;
    public static final int RK = 1;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({IMX6, RK})
    // Create an interface for validating int types
    public @interface DeviceTypeDef {
    }

    public
    @DeviceTypeDef
    static int translate(int val) {
        return val;
    }
}
