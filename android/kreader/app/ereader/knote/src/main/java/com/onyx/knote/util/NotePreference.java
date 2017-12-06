package com.onyx.knote.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by solskjaer49 on 2017/5/17 17:06.
 */

public class NotePreference {
    private static SharedPreferences sDefaultPreferences;
    public static final String KEY_NOTE_SORT_BY = "key_note_sort_by";
    public static final String KEY_NOTE_ASC_ORDER = "key_note_asc_order";
    public static final String KEY_IMPORT_MENU_VISIBLE = "key_import_menu_visible";
    public static final String KEY_HAS_IMPORT_OLD_SCRIBBLE = "key_has_import_old_scribble";
    public static final String KEY_HAS_OPEN_IMPORT_OLD_SCRIBBLE_DIALOG = "key_has_open_import_old_scribble_dialog";

    public static void init(Context context) {
        sDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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

    public static void setBooleanValue(Context context, int ResID, boolean value) {
        setBooleanValue(context.getResources().getString(ResID), value);
    }

    public static void setIntValue(Context context, int ResID, int value) {
        setIntValue(context.getResources().getString(ResID), value);
    }

    public static void setStringValue(Context context, int ResID, String value) {
        setStringValue(context.getResources().getString(ResID), value);
    }

    public static void setBooleanValue(String key, boolean value) {
        sDefaultPreferences.edit().putBoolean(key, value).apply();
    }

    public static void setIntValue(String key, int value) {
        sDefaultPreferences.edit().putInt(key, value).apply();
    }

    public static void setStringValue(String key, String value) {
        sDefaultPreferences.edit().putString(key, value).apply();
    }

}
