package com.onyx.android.libsetting.util;

import android.content.Context;

/**
 * Created by solskjaer49 on 2016/12/21 18:53.
 */

public class KeyBindingSettingUtil {
    private static final String KEY_MAP_MODE = "key_map_mode";
    private static final String LONG_PRESS_FEATURE = "long_press_feature";

    public static void setKeyMapMode(Context context, int targetKeyMap) {
        SystemSettingUtil.changeSystemConfigIntegerValue(context, KEY_MAP_MODE, targetKeyMap);
    }

    public static String getKeyMapMode(Context context) {
        return Integer.toString(SystemSettingUtil.getSystemConfigIntegerValue(context, KEY_MAP_MODE, 1));
    }

    public static void setDpadLongPressFeature(Context context, int targetFeature) {
        SystemSettingUtil.changeSystemConfigIntegerValue(context, LONG_PRESS_FEATURE, targetFeature);
    }

    public static String getDpadLongPressFeature(Context context) {
        return Integer.toString(SystemSettingUtil.getSystemConfigIntegerValue(context, LONG_PRESS_FEATURE, 1));
    }
}
