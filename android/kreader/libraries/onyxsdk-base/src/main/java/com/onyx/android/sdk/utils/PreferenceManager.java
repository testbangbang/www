package com.onyx.android.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by suicheng on 2016/11/18.
 */
public class PreferenceManager {
    private static SharedPreferences sDefaultPreferences;
    private static SharedPreferences.Editor sDefaultEditor;

    public static void init(Context context) {
        sDefaultPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences getDefaultPrefs() {
        return sDefaultPreferences;
    }

    public static boolean getBooleanValue(Context context, int ResID, boolean defaultValue) {
        return getBooleanValue(context, context.getResources().getString(ResID), defaultValue);
    }

    public static int getIntValue(Context context, int ResID, int defaultValue) {
        return getIntValue(context, context.getResources().getString(ResID), defaultValue);
    }

    public static String getStringValue(Context context, int ResID, String defaultValue) {
        return getStringValue(context, context.getResources().getString(ResID), defaultValue);
    }

    public static boolean getBooleanValue(Context context, String key, boolean defaultValue) {
        return sDefaultPreferences.getBoolean(key, defaultValue);
    }

    public static int getIntValue(Context context, String key, int defaultValue) {
        return sDefaultPreferences.getInt(key, defaultValue);
    }

    public static String getStringValue(Context context, String key, String defaultValue) {
        return sDefaultPreferences.getString(key, defaultValue);
    }

    public static long getLongValue(Context context, String key, long defaultValue) {
        return sDefaultPreferences.getLong(key, defaultValue);
    }

    public static void setBooleanValue(Context context, int ResID, boolean value) {
        setBooleanValue(context, context.getResources().getString(ResID), value);
    }

    public static void setIntValue(Context context, int ResID, int value) {
        setIntValue(context, context.getResources().getString(ResID), value);
    }

    public static void setStringValue(Context context, int ResID, String value) {
        setStringValue(context, context.getResources().getString(ResID), value);
    }

    public static void setLongValue(Context context, int ResID, long value) {
        setLongValue(context, context.getResources().getString(ResID), value);
    }

    public static void setBooleanValue(Context context, String key, boolean value) {
        sDefaultEditor = sDefaultPreferences.edit();
        sDefaultEditor.putBoolean(key, value);
        sDefaultEditor.apply();
    }

    public static void setIntValue(Context context, String key, int value) {
        sDefaultEditor = sDefaultPreferences.edit();
        sDefaultEditor.putInt(key, value);
        sDefaultEditor.apply();
    }

    public static void setStringValue(Context context, String key, String value) {
        sDefaultEditor = sDefaultPreferences.edit();
        sDefaultEditor.putString(key, value);
        sDefaultEditor.apply();
    }

    public static void setLongValue(Context context, String key, long value) {
        sDefaultEditor = sDefaultPreferences.edit();
        sDefaultEditor.putLong(key, value);
        sDefaultEditor.apply();
    }
}
