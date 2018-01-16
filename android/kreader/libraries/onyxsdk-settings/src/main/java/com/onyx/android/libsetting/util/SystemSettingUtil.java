package com.onyx.android.libsetting.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by solskjaer49 on 2016/12/8 11:41.
 */

public class SystemSettingUtil {
    public static boolean changeSystemConfigIntegerValue(Context context, String dataKey, int value) {
        try {
            if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
                Settings.Global.putInt(context.getContentResolver(), dataKey, value);
            } else {
                Settings.System.putInt(context.getContentResolver(), dataKey, value);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getSystemConfigIntegerValue(Context context, String dataKey, int defaultValue) {
        try {
            if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
                return Settings.Global.getInt(context.getContentResolver(), dataKey, defaultValue);
            } else {
                return Settings.System.getInt(context.getContentResolver(), dataKey, defaultValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
