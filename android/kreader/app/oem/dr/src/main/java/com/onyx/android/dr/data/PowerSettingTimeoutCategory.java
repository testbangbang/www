package com.onyx.android.dr.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 2016/12/2 15:44.
 */

public class PowerSettingTimeoutCategory {
    public static final int POWER_OFF_TIMEOUT = 0;
    public static final int SCREEN_TIMEOUT = 1;
    public static final int WIFI_INACTIVITY_TIMEOUT = 2;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({POWER_OFF_TIMEOUT, SCREEN_TIMEOUT, WIFI_INACTIVITY_TIMEOUT})
    // Create an interface for validating int types
    public @interface PowerSettingTimeoutCategoryDef {
    }

    public
    @PowerSettingTimeoutCategoryDef
    static int translate(int val) {
        return val;
    }
}
