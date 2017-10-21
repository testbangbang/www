package com.onyx.android.monitor;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wangxu on 17-7-28.
 */

public class SingletonSharedPreference {

    private static final int DEFAULT_INTERVAL_COUNT = 50;
    public static final int GC_REFRESH_INTERVAL_COUNT_MAX = 200;
    public static final int GC_REFRESH_INTERVAL_COUNT_MIN = DEFAULT_INTERVAL_COUNT;

    private final static String GC_INTERVAL_TIME = "gc_interval_count";

    public static int getGcIntervalCount(Context context) {
        return getIntByString(context, GC_INTERVAL_TIME, DEFAULT_INTERVAL_COUNT);
    }

    public static void setGcIntervalCount(Context context, int time) {
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
