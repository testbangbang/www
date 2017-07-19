package com.onyx.android.dr.reader.common;

import android.content.Context;

import com.onyx.android.sdk.utils.PreferenceManager;

/**
 * Created by huxiaomao on 17/5/12.
 */

public class ReadPhysicalKeyConfig {
    public static final String READ_PHYSICAL_KEY_KEY = "ReadPhysicalKeyKey";
    public static final int READ_Physical_Key_ONE = 1;
    public static final int READ_Physical_Key_TWO = 2;
    public static final int READ_Physical_Key_THREE = 3;

    public static void saveReadSettingFontFace(Context context, int value) {
        PreferenceManager.setIntValue(context, READ_PHYSICAL_KEY_KEY, value);
    }

    public static int getReadSettingFontFace(Context context) {
        return PreferenceManager.getIntValue(context, READ_PHYSICAL_KEY_KEY,
                READ_Physical_Key_ONE);
    }
}
