package com.onyx.android.sdk.ui.utils;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by solskjaer49 on 16/6/25 20:21.
 */

public class ScreenSpecUtil {
    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    public static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
}
