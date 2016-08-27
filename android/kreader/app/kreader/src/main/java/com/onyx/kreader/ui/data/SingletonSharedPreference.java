package com.onyx.kreader.ui.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.onyx.kreader.R;

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

    public static boolean isSystemStatusBarEnabled(Context context) {
        return getBooleanByStringID(context, R.string.settings_enable_system_status_bar_key, ReaderConfig.sharedInstance(context).defaultUseSystemStatusBar());
    }

    public static boolean isReaderStatusBarEnabled(Context context) {
        return getBooleanByStringID(context, R.string.settings_enable_reader_status_bar_key, ReaderConfig.sharedInstance(context).defaultUseReaderStatusBar());
    }

    public static boolean isAnimationEnabled(Context context) {
        return getBooleanByStringID(context, R.string.settings_animation_key, true);
    }

    public static boolean isScribbleBarOverStatusBar(Context context) {
        return getBooleanByStringID(context, R.string.settings_scribble_bar_show_reader_status_bar_key, false);
    }

    public static boolean isStatusBarShowBatteryGraphical(Context context){
        return getBooleanByStringID(context,R.string.settings_battery_graphic_show_key,false);
    }

    public static boolean isStatusBarShowBatteryPercentage(Context context){
        return getBooleanByStringID(context,R.string.settings_battery_percentage_show_key,false);
    }

    public static boolean isStatusBarTimeShow(Context context){
        return getBooleanByStringID(context,R.string.settings_time_show_key,false);
    }

    public static boolean isStatusBarTime24HourFormat(Context context){
        return getBooleanByStringID(context,R.string.settings_time_show_format_key,false);
    }

    public static boolean isShowQuitDialog(Context context){
        return getBooleanByStringID(context, R.string.settings_close_dialog_key, false);
    }

    public static int getSearchPopUpMenuPosition(Context context){
        return Integer.parseInt(sPreferences.getString(context.getString(R.string.settings_search_menu_position_key), "1"));
    }

    public static boolean isShowBookmark(Context context) {
        return getBooleanByStringID(context, R.string.settings_bookmark_key, true);
    }

    public static boolean isShowHyperlink(Context context) {
        return getBooleanByStringID(context, R.string.settings_hyperlink_key, true);
    }

    public static boolean isEnableDithering(Context context) {
        return getBooleanByStringID(context, R.string.settings_dithering_key, false);
    }

    public static boolean isShowDocTitleInStatusBar(Context context) {
        return getBooleanByStringID(context, R.string.settings_show_doc_title_key, ReaderConfig.sharedInstance(context).defaultShowDocTitleInStatusBar());
    }

    public static boolean isCropToWidthInLandscape(Context context) {
        return getBooleanByStringID(context, R.string.settings_crop_to_width_in_landscape_key, true);
    }

    public static boolean isShowAnnotation(Context context) {
        return getBooleanByStringID(context, R.string.settings_show_annotation_key, true);
    }

    public static AnnotationHighlightStyle getAnnotationHighlightStyle(Context context) {
        String value = sPreferences.getString(context.getString(R.string.settings_annotation_highlight_style_key), null);
        if (value != null) {
            try {
                return Enum.valueOf(AnnotationHighlightStyle.class, value);
            } catch (Exception e) {
            }
        }
        return AnnotationHighlightStyle.Underline;
    }

    public static boolean isSmoothScribble(Context context) {
        return getBooleanByStringID(context, R.string.settings_scribble_smooth_key, true);
    }

    public static int getLeftMargin(Context context) {
        return getIntByStringID(context, R.string.settings_left_margin_key, 0);
    }

    public static int getTopMargin(Context context) {
        return getIntByStringID(context, R.string.settings_top_margin_key, 0);
    }

    public static int getRightMargin(Context context) {
        return getIntByStringID(context, R.string.settings_right_margin_key, 0);
    }

    public static int getBottomMargin(Context context) {
        return getIntByStringID(context, R.string.settings_bottom_margin_key, 0);
    }

    public static boolean isAcquireLockInScribble(Context context) {
        return getBooleanByStringID(context, R.string.settings_acquire_scribble_lock_key, true);
    }

    public static float getBaseLineWidth(Context context) {
        float defaultValue = 1.0f;
        String value = sPreferences.getString(context.getString( R.string.settings_scribble_base_width_key), String.valueOf(defaultValue));
        try {
            defaultValue = Float.parseFloat(value);
        } catch (Exception e) {
        } finally {
            return defaultValue;
        }
    }

}
