package com.onyx.jdread.main.common;

import android.content.Context;

import com.onyx.android.sdk.utils.PreferenceManager;

/**
 * Created by li on 2018/1/19.
 */

public class JDPreferenceManager extends PreferenceManager {
    private static Context context;

    public static void initWithAppContext(Context context) {
        JDPreferenceManager.context = context;
        init(context);
    }

    public static boolean getBooleanValue(int resID, boolean defaultValue) {
        return getBooleanValue(context, resID, defaultValue);
    }

    public static int getIntValue(int resID, int defaultValue) {
        return getIntValue(context, resID, defaultValue);
    }

    public static String getStringValue(int resID, String defaultValue) {
        return getStringValue(context, resID, defaultValue);
    }

    public static boolean getBooleanValue(String key, boolean defaultValue) {
        return getBooleanValue(context, key, defaultValue);
    }

    public static int getIntValue(String key, int defaultValue) {
        return getIntValue(context, key, defaultValue);
    }

    public static String getStringValue(String key, String defaultValue) {
        return getStringValue(context, key, defaultValue);
    }

    public static long getLongValue(String key, long defaultValue) {
        return getLongValue(context, key, defaultValue);
    }

    public static void setBooleanValue(int resID, boolean value) {
        setBooleanValue(context, resID, value);
    }

    public static void setIntValue(int resID, int value) {
        setIntValue(context, resID, value);
    }

    public static void setStringValue(int resID, String value) {
        setStringValue(context, resID, value);
    }

    public static void setLongValue(int resID, long value) {
        setLongValue(context, resID, value);
    }

    public static void setBooleanValue(String key, boolean value) {
        setBooleanValue(context, key, value);
    }

    public static void setIntValue(String key, int value) {
        setIntValue(context, key, value);
    }

    public static void setStringValue(String key, String value) {
        setStringValue(context, key, value);
    }

    public static void setLongValue(String key, long value) {
        setLongValue(context, key, value);
    }
}
