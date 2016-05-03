package com.onyx.kreader.ui.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by solskjaer49 on 14-4-22.
 */
public class SingletonSharedPreference {

    public enum AnnotationHighlightStyle { Highlight, Underline }

    private static SharedPreferences sPreferences;
    private static SharedPreferences.Editor sDefaultEditor;
    public static void init(Context context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences getPrefs() {
        return sPreferences;
    }

    public static void setBooleanValue(String key, boolean value) {
        sDefaultEditor = sPreferences.edit();
        sDefaultEditor.putBoolean(key, value);
        sDefaultEditor.apply();
    }

    public static void setIntValue(String key, int value) {
        sDefaultEditor = sPreferences.edit();
        sDefaultEditor.putInt(key, value);
        sDefaultEditor.apply();
    }

    public static void setStringValue(String key, String value) {
        sDefaultEditor = sPreferences.edit();
        sDefaultEditor.putString(key, value);
        sDefaultEditor.apply();
    }

    public static void removeValueByKey(String key){
        sDefaultEditor = sPreferences.edit();
        sDefaultEditor.remove(key);
        sDefaultEditor.apply();
    }

    public static void removeValueByKey(Context context,int resID){
        sDefaultEditor = sPreferences.edit();
        sDefaultEditor.remove(context.getResources().getString(resID));
        sDefaultEditor.apply();
    }

    public static void setStringValue(Context context, int ResID, String value) {
        setStringValue(context.getResources().getString(ResID), value);
    }

    public static void setBooleanValue(Context context, int ResID, boolean value) {
        setBooleanValue(context.getResources().getString(ResID), value);
    }

    public static void setIntValue(Context context, int ResID, int value) {
        setIntValue(context.getResources().getString(ResID), value);
    }

    public static boolean getBooleanByStringID(Context context, int ResID, boolean defaultValue) {
        return sPreferences.getBoolean(context.getString(ResID), defaultValue);
    }

    public static int getIntByStringID(Context context, int ResID, int defaultValue) {
        return sPreferences.getInt(context.getString(ResID), defaultValue);
    }

    public static boolean getBooleanByStringResource(String keyString, boolean defaultValue) {
        return sPreferences.getBoolean(keyString, defaultValue);
    }

    public static int getIntByStringResource(String keyString, int defaultValue) {
        return sPreferences.getInt(keyString, defaultValue);
    }

}
