package com.onyx.android.libsetting.util;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by solskjaer49 on 2016/12/8 11:41.
 */

public class SystemSettingUtil {
    public static boolean changeSystemConfigIntegerValue(Context context, String dataKey, int value) {
        try {
            return Settings.System.putInt(context.getContentResolver(), dataKey, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getSystemConfigIntegerValue(Context context, String dataKey, int defaultValue) {
        try {
            return Settings.System.getInt(context.getContentResolver(), dataKey, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
