package com.onyx.android.dr.reader.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.onyx.android.dr.R;


/**
 * Created by solskjaer49 on 14-4-22.
 */
public class SingletonSharedPreference {

    public enum AnnotationHighlightStyle { Highlight, Underline }

    // define keys not to be used by PreferenceActivity
    private final static String TTS_SPEECH_RATE_KEY = "tts_speech_rate_key";
    private final static String QUICK_VIEW_GRID_TYPE = "quick_view_grid_type";
    private final static String DIALOG_TABLE_OF_CONTENT_TAB = "dialog_table_of_content_tab";
    private final static String EXPORT_WITH_ANNOTATION = "export_with_annotation";
    private final static String EXPORT_WITH_SCRIBBLE = "export_with_scribble";
    private final static String EXPORT_SCRIBBLE_COLOR = "export_scribble_color";
    private final static String EXPORT_ALL_PAGES = "export_all_pages";
    private final static String LAST_FONT_SIZE = "last_font_size";
    private final static String LAST_LINE_SPACING = "last_line_spacing";
    private final static String LAST_LEFT_MARGIN = "last_left_margin";
    private final static String LAST_TOP_MARGIN = "last_top_margin";
    private final static String LAST_RIGHT_MARGIN = "last_right_margin";
    private final static String LAST_BOTTOM_MARGIN = "last_bottom_margin";
    private final static String SCREEN_ORIENTATION = "screen_orientation";
    private final static String MULTIPLE_TAB_STATE = "multiple_tab_state";
    private final static String MULTIPLE_TAB_VISIBILITY = "multiple_tab_visibility";

    private static Context sContext;
    private static SharedPreferences.Editor sDefaultEditor;

    public static void init(Context context) {
        sContext = context;
    }

    public static SharedPreferences getPrefs() {
        return sContext.getSharedPreferences(sContext.getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
    }

    public static void setBooleanValue(String key, boolean value) {
        sDefaultEditor = getPrefs().edit();
        sDefaultEditor.putBoolean(key, value);
        sDefaultEditor.apply();
    }

    public static void setIntValue(String key, int value) {
        sDefaultEditor = getPrefs().edit();
        sDefaultEditor.putInt(key, value);
        sDefaultEditor.apply();
    }

    public static void setFloatValue(String key, float value) {
        sDefaultEditor = getPrefs().edit();
        sDefaultEditor.putFloat(key, value);
        sDefaultEditor.apply();
    }

    public static void setStringValue(String key, String value) {
        sDefaultEditor = getPrefs().edit();
        sDefaultEditor.putString(key, value);
        sDefaultEditor.apply();
    }

    public static void removeValueByKey(String key){
        sDefaultEditor = getPrefs().edit();
        sDefaultEditor.remove(key);
        sDefaultEditor.apply();
    }

    public static void removeValueByKey(Context context,int resID){
        sDefaultEditor = getPrefs().edit();
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

    public static void setFloatValue(Context context, int ResID, float value) {
        setFloatValue(context.getResources().getString(ResID), value);
    }

    public static boolean getBooleanByStringID(Context context, int ResID, boolean defaultValue) {
        return getPrefs().getBoolean(context.getString(ResID), defaultValue);
    }

    public static int getIntByStringID(Context context, int ResID, int defaultValue) {
        return getPrefs().getInt(context.getString(ResID), defaultValue);
    }

    public static float getFloatByStringID(Context context, int ResID, float defaultValue) {
        return getPrefs().getFloat(context.getString(ResID), defaultValue);
    }

    public static boolean getBooleanByStringResource(String keyString, boolean defaultValue) {
        return getPrefs().getBoolean(keyString, defaultValue);
    }

    public static int getIntByStringResource(String keyString, int defaultValue) {
        return getPrefs().getInt(keyString, defaultValue);
    }

    public static String getStringValue(String keyString) {
        return getPrefs().getString(keyString, "");
    }

    public static void setExportAllPages(boolean value) {
        setBooleanValue(EXPORT_ALL_PAGES, value);
    }

    public static boolean isExportAllPages() {
        return getPrefs().getBoolean(EXPORT_ALL_PAGES, true);
    }

    public static void setLastFontSize(float value) {
        setFloatValue(LAST_FONT_SIZE, value);
    }

    public static float getLastFontSize(float defaultValue) {
        return getPrefs().getFloat(LAST_FONT_SIZE, defaultValue);
    }

    public static void setLastLeftMargin(int value) {
        setIntValue(LAST_LEFT_MARGIN, value);
    }

    public static int getLastLeftMargin(int defaultValue) {
        return getPrefs().getInt(LAST_LEFT_MARGIN, defaultValue);
    }

    public static void setLastTopMargin(int value) {
        setIntValue(LAST_TOP_MARGIN, value);
    }

    public static int getLastTopMargin(int defaultValue) {
        return getPrefs().getInt(LAST_TOP_MARGIN, defaultValue);
    }

    public static void setLastRightMargin(int value) {
        setIntValue(LAST_RIGHT_MARGIN, value);
    }

    public static int getLastRightMargin(int defaultValue) {
        return getPrefs().getInt(LAST_RIGHT_MARGIN, defaultValue);
    }

    public static void setLastBottomMargin(int value) {
        setIntValue(LAST_BOTTOM_MARGIN, value);
    }

    public static int getLastBottomMargin(int defaultValue) {
        return getPrefs().getInt(LAST_BOTTOM_MARGIN, defaultValue);
    }

    public static void setLastLineSpacing(int value) {
        setIntValue(LAST_LINE_SPACING, value);
    }

    public static int getLastLineSpacing(int defaultValue) {
        return getPrefs().getInt(LAST_LINE_SPACING, defaultValue);
    }

    public static void setScreenOrientation(int screenOrientation) {
        setIntValue(SCREEN_ORIENTATION, screenOrientation);
    }

    public static int getScreenOrientation(int defaultValue) {
        return getPrefs().getInt(SCREEN_ORIENTATION, defaultValue);
    }

    public static void setMultipleTabState(String state) {
        setStringValue(MULTIPLE_TAB_STATE, state);
    }

    public static String getMultipleTabState() {
        return getStringValue(MULTIPLE_TAB_STATE);
    }

    public static void setMultipleTabVisibility(boolean visible) {
        setBooleanValue(MULTIPLE_TAB_VISIBILITY, visible);
    }

    public static boolean getMultipleTabVisibility() {
        return getPrefs().getBoolean(MULTIPLE_TAB_VISIBILITY, true);
    }

    public static AnnotationHighlightStyle getAnnotationHighlightStyle(Context context) {
        String value = getPrefs().getString(context.getString(R.string.settings_annotation_highlight_style_key), null);
        if (value != null) {
            try {
                return Enum.valueOf(AnnotationHighlightStyle.class, value);
            } catch (Exception e) {
            }
        }
        return AnnotationHighlightStyle.Highlight;
    }
}
