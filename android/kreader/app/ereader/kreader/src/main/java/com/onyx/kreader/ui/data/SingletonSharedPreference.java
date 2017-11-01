package com.onyx.kreader.ui.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.onyx.kreader.R;
import com.onyx.kreader.ui.requests.ExportNotesRequest;
import com.onyx.kreader.device.DeviceConfig;

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
    private final static String CURRENT_TAB = "current_tab";

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

    public static boolean isSystemStatusBarEnabled(Context context) {
        return getBooleanByStringID(context, R.string.settings_enable_system_status_bar_key, DeviceConfig.sharedInstance(context).isDefaultUseSystemStatusBar());
    }

    public static boolean isReaderStatusBarEnabled(Context context) {
        return getBooleanByStringID(context, R.string.settings_enable_reader_status_bar_key, DeviceConfig.sharedInstance(context).isDefaultUseReaderStatusBar());
    }

    public static boolean isMultipleTabsEnabled(Context context) {
        return getBooleanByStringID(context, R.string.settings_enable_multiple_tabs_key, DeviceConfig.sharedInstance(context).isSupportMultipleTabs());
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
        return Integer.parseInt(getPrefs().getString(context.getString(R.string.settings_search_menu_position_key), "1"));
    }

    public static boolean isShowNote(Context context) {
        return getBooleanByStringID(context, R.string.settings_note_key, true);
    }

    public static void setIsShowNote(Context context, boolean value) {
        setBooleanValue(context, R.string.settings_note_key, value);
    }

    public static boolean isShowBookmark(Context context) {
        return getBooleanByStringID(context, R.string.settings_bookmark_key, true);
    }

    public static boolean isShowSideNoteIndicator(Context context) {
        return getBooleanByStringID(context, R.string.settings_show_side_note_indicator_key, true);
    }

    public static boolean isShowHyperlink(Context context) {
        return getBooleanByStringID(context, R.string.settings_hyperlink_key, true);
    }

    public static boolean isEnableDithering(Context context) {
        return getBooleanByStringID(context, R.string.settings_dithering_key, false);
    }

    public static boolean isEnableRegal(Context context) {
        return getBooleanByStringID(context, R.string.settings_regal_mode_key, true);
    }

    public static boolean isShowDocTitleInStatusBar(Context context) {
        return getBooleanByStringID(context, R.string.settings_show_doc_title_key, DeviceConfig.sharedInstance(context).isDefaultShowDocTitleInStatusBar());
    }

    public static boolean isCropToWidthInLandscape(Context context) {
        return getBooleanByStringID(context, R.string.settings_crop_to_width_in_landscape_key, true);
    }

    public static boolean isShowAnnotation(Context context) {
        return getBooleanByStringID(context, R.string.settings_show_annotation_key, true);
    }

    public static void setIsShowAnnotation(Context context, boolean value) {
        setBooleanValue(context, R.string.settings_show_annotation_key, value);
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
        String value = getPrefs().getString(context.getString( R.string.settings_scribble_base_width_key), String.valueOf(defaultValue));
        try {
            defaultValue = Float.parseFloat(value);
        } catch (Exception e) {
        } finally {
            return defaultValue;
        }
    }

    public static void setTtsSpeechRate(Context context, float value) {
        setFloatValue(TTS_SPEECH_RATE_KEY, value);
    }

    public static float getTtsSpeechRate(Context context) {
        return getPrefs().getFloat(TTS_SPEECH_RATE_KEY, 1.0f);
    }

    public static void setQuickViewGridType(Context context, int value) {
        setIntValue(QUICK_VIEW_GRID_TYPE, value);
    }

    public static int getQuickViewGridType(Context context, int defaultValue) {
        return getPrefs().getInt(QUICK_VIEW_GRID_TYPE, defaultValue);
    }

    public static void setDialogTableOfContentTab(Context context, int value) {
        setIntValue(DIALOG_TABLE_OF_CONTENT_TAB, value);
    }

    public static int getDialogTableOfContentTab(Context context, int defaultValue) {
        return getPrefs().getInt(DIALOG_TABLE_OF_CONTENT_TAB, defaultValue);
    }

    public static void setExportWithAnnotation(boolean value) {
        setBooleanValue(EXPORT_WITH_ANNOTATION, value);
    }

    public static boolean isExportWithAnnotation() {
        return getPrefs().getBoolean(EXPORT_WITH_ANNOTATION, true);
    }

    public static void setExportWithScribble(boolean value) {
        setBooleanValue(EXPORT_WITH_SCRIBBLE, value);
    }

    public static boolean isExportWithScribble() {
        return getPrefs().getBoolean(EXPORT_WITH_SCRIBBLE, true);
    }

    public static void setExportScribbleColor(ExportNotesRequest.BrushColor color) {
        setIntValue(EXPORT_SCRIBBLE_COLOR, color.ordinal());
    }

    public static ExportNotesRequest.BrushColor getExportScribbleColor() {
        int ordinal = getPrefs().getInt(EXPORT_SCRIBBLE_COLOR, 0);
        ExportNotesRequest.BrushColor[] values = ExportNotesRequest.BrushColor.values();
        if (ordinal > values.length) {
            return ExportNotesRequest.BrushColor.Original;
        }
        return values[ordinal];
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

    public static void setCurrentTab(String currentTab) {
        setStringValue(CURRENT_TAB, currentTab);
    }

    public static String getCurrentTab() {
        return getPrefs().getString(CURRENT_TAB, "");
    }
}
