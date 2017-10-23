package com.onyx.android.sun.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.onyx.android.sun.SunApplication;


/**
 * Created by jackdeng on 2017/10/23.
 */
public class SharedPreferencesUtil {


    public static void putValue(String name, String key, int value) {
        Editor sp = getEditor(name);
        sp.putInt(key, value);
        sp.commit();
    }

    public static void putValue(String name, String key, boolean value) {
        Editor sp = getEditor(name);
        sp.putBoolean(key, value);
        sp.commit();
    }

    public static void putValue(String name, String key, String value) {
        Editor sp = getEditor(name);
        sp.putString(key, value);
        sp.commit();
    }

    public static void putValue(String name, String key, float value) {
        Editor sp = getEditor(name);
        sp.putFloat(key, value);
        sp.commit();
    }

    public static void putValue(String name, String key, long value) {
        Editor sp = getEditor(name);
        sp.putLong(key, value);
        sp.commit();
    }

    public static int getValue(String name, String key, int defValue) {
        SharedPreferences sp = getSharedPreferences(name);
        int value = sp.getInt(key, defValue);
        return value;
    }

    public static boolean getValue(String name, String key, boolean defValue) {
        SharedPreferences sp = getSharedPreferences(name);
        boolean value = sp.getBoolean(key, defValue);
        return value;
    }

    public static String getValue(String name, String key, String defValue) {
        SharedPreferences sp = getSharedPreferences(name);
        String value = sp.getString(key, defValue);
        return value;
    }

    public static float getValue(String name, String key, float defValue) {
        SharedPreferences sp = getSharedPreferences(name);
        float value = sp.getFloat(key, defValue);
        return value;
    }

    public static long getValue(String name, String key, long defValue) {
        SharedPreferences sp = getSharedPreferences(name);
        long value = sp.getLong(key, defValue);
        return value;
    }

    public static Editor getEditor(String name) {
        return getSharedPreferences(name).edit();
    }

    public static void cleanDatas(String name) {
        Editor editor = getEditor(name);
        editor.clear().commit();
    }


    public static void cleanValueByKey(String name,String key) {
        Editor editor = getEditor(name).remove(key);
        editor.commit();
    }

    private static SharedPreferences getSharedPreferences(String name) {
        return SunApplication.getInstance().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

}
