package com.onyx.android.dr.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.R;
import com.onyx.android.sdk.dict.conf.AppConfig;
import com.onyx.android.sdk.dict.data.DictionaryKey;
import com.onyx.android.sdk.dict.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by solskjaer49 on 15-12-27.
 */
public class DictPreference {
    private static SharedPreferences sDefaultPreferences;
    private static SharedPreferences.Editor sDefaultEditor;
    public static final int SORT_TYPE_LETTER = 0;

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

    public static Set<String> getStringSetValue(Context context, int ResID, Set<String> defaultValue) {
        return getStringSetValue(context, context.getResources().getString(ResID), defaultValue);
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

    public static Set<String> getStringSetValue(Context context, String key, Set<String> defaultValue) {
        return sDefaultPreferences.getStringSet(key, defaultValue);
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

    public static void setStringSet(Context context, String key, Set<String> value) {
        sDefaultEditor = sDefaultPreferences.edit();
        sDefaultEditor.putStringSet(key, value);
        sDefaultEditor.apply();
    }

    public static int getCustomTextSize(Context context) {
        String defaultFontSize = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_view_text_size_key), null);
        if (defaultFontSize == null) {
            defaultFontSize = AppConfig.sharedInstance(context).getDefaultFontSize() + "";
            setStringValue(context, context.getString(R.string.pref_view_text_size_key), defaultFontSize);
        }
        return Integer.parseInt(getStringValue(context, context.getString(R.string.pref_view_text_size_key), defaultFontSize));
    }

    public static List<String> getPreferredDictList(Context context,final String key) {
        List<String> preferredDictList = new ArrayList<String>();
        for (String dictPath : getStringSetValue(context, key, new HashSet<String>())) {
            if (dictPath != null && FileUtils.exists(dictPath)) {//check delete file
                preferredDictList.add(dictPath);
            }
        }
        return preferredDictList;
    }

    public static void setPreferredDictList(Context context, Set<String> dictList,final String key) {
        setStringSet(context, key, dictList);
    }

    public static void saveDictionaryKey(Context context, String dictPath, DictionaryKey dictionaryKey) {
        String value = JSON.toJSONString(dictionaryKey);
        setStringValue(context, dictPath, value);
    }

    public static DictionaryKey getDictionaryKey(Context context, String dictPath) {
        String value = getStringValue(context, dictPath, null);
        DictionaryKey dictionaryKey = null;
        if (value != null && value.length() > 0) {
            dictionaryKey = JSON.parseObject(value, DictionaryKey.class);
        }
        return dictionaryKey;
    }
    public static int getSortType(Context context, String key) {
        return sDefaultPreferences.getInt(key, SORT_TYPE_LETTER);
    }
}
