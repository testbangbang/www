package com.onyx.android.dr.util;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by solskjaer49 on 2016/12/8 11:41.
 */

public class SystemSettingUtil {
    public static boolean changeSystemConfigIntegerValue(Context context, String dataKey, int value) {
        try {
            Settings.System.putInt(context.getContentResolver(), dataKey, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
