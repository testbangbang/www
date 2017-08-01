package com.onyx.android.monitor;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wangxu on 17-7-28.
 */

public class SingletonSharedPreference {

    private static final int DEFAULT_INTERVAL_TIME = 1;  //minute
    private final static String GC_INTERVAL_TIME = "gc_interval_time";

    public static int getGcIntervalTime(Context context) {
        return getIntByString(context, GC_INTERVAL_TIME, DEFAULT_INTERVAL_TIME);
    }

    public static void setGcIntervalTime(Context context, int time) {
        setIntValue(context, GC_INTERVAL_TIME, time);
    }

    public static int getIntByString(Context context, String tag, int defaultValue) {
        return getPrefs(context).getInt(tag, defaultValue);
    }

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
    }

    public static void setIntValue(Context context, String key, int value) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }
}
