package com.onyx.android.sdk.utils;

import android.os.Build;

/**
 * Created by solskjaer49 on 2017/5/17 18:56.
 */

public class CompatibilityUtil {
    public static boolean apiLevelCheck(int requireAPILevel) {
        return Build.VERSION.SDK_INT >= requireAPILevel;
    }
}
