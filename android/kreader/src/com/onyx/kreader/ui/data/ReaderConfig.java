package com.onyx.kreader.ui.data;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.onyx.kreader.BuildConfig;
import com.onyx.kreader.host.options.ReaderOptions;
import com.onyx.kreader.utils.GObject;
import com.onyx.kreader.utils.RawResourceUtil;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 5/18/14
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderConfig {
    static private ReaderConfig globalInstance;
    static private String TAG = ReaderConfig.class.getSimpleName();
    private GObject backend;

    static public final String KEY_BINDING = "key_binding";
    static public final String KEY_ACTION_TAG = "action";
    static public final String KEY_ARGS_TAG = "args";

    public static final String NEXT_SCREEN = "nextScreen";
    public static final String NEXT_PAGE = "nextPage";
    public static final String PREV_SCREEN = "prevScreen";
    public static final String PREV_PAGE = "prevPage";
    public static final String MOVE_LEFT = "moveLeft";
    public static final String MOVE_RIGHT = "moveRight";
    public static final String MOVE_UP = "moveUp";
    public static final String MOVE_DOWN = "moveDown";
    public static final String SHOW_MENU = "showMenu";
    public static final String INCREASE_FONT_SIZE = "increaseFontSize";
    public static final String DECREASE_FONT_SIZE = "decreaseFontSize";
    public static final String TOGGLE_BOOKMARK = "toggleBookmark";
    public static final String CHANGE_TO_ERASE_MODE = "changeToEraseMode";
    public static final String CHANGE_TO_SCRIBBLE_MODE = "changeToScribbleMode";

    static public final String DELETE_ACSM_AFTER_FULFILLMENT = "delete_acsm_after_fulfillment";
    static public final String ROTATION_OFFSET = "rotation_offset";
    static public final String CLEAR_BEFORE_ANIMATION = "clear_befor_animation";
    static public final String SUPPORT_ZIP_COMPRESSED_BOOKS = "support_zip_compressed_books";

    static public final String DEFAULT_FONT_SIZE = "default_font_size";
    static public final String USE_BIG_PEN = "use_big_pen";
    static public final String DEFAULT_USE_SYSTEM_STATUS_BAR = "default_use_system_status_bar";
    static public final String DEFAULT_USE_READER_STATUS_BAR = "default_use_reader_status_bar";
    static public final String DEFAULT_SHOW_DOC_TITLE_IN_STATUS_BAR = "default_show_doc_title_in_status_bar";
    static public final String DEFAULT_ANNOTATION_HIGHLIGHT_STYLE = "default_annotation_highlight_style";

    static public final String DIALOG_NAVIGATION_SETTINGS_SUBSCREEN_LANDSCAPE_ROWS = "dialog_navigation_settings_subscreen_landscape_rows";
    static public final String DIALOG_NAVIGATION_SETTINGS_SUBSCREEN_LANDSCAPE_COLUMNS = "dialog_navigation_settings_subscreen_landscape_columns";

    static public final String SELECTION_OFFSET_X = "selection_offset_x";
    static public final String DISABLE_NAVIGATION = "disable_navigation";
    static public final String SELECTION_MOVE_DISTANCE_THRESHOLD = "selection_move_distance_threshold";
    static public final String HIDE_SELECTION_MODE_UI_OPTION = "hide_selection_mode_ui_option";

    static public final String GC_INTERVAL = "gc_interval";

    static public final boolean useDebugConfig = false;

    static public ReaderConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new ReaderConfig(context);
        }
        return globalInstance;
    }

    public Map<String, Map<String, JSONObject>> getKeyBinding() {
        if (backend.hasKey(KEY_BINDING)) {
            return (Map<String, Map<String, JSONObject>> )(backend.getObject(KEY_BINDING));
        }
        return null;
    }

    public boolean shouldDeleteAcsmAfterFulfillment() {
        return backend.hasKey(DELETE_ACSM_AFTER_FULFILLMENT) && backend.getBoolean(DELETE_ACSM_AFTER_FULFILLMENT);
    }

    private ReaderConfig(Context context) {
        backend = objectFromDebugModel(context);
        if (backend != null) {
            Log.i(TAG, "Using debug model.");
            return;
        }

        backend = objectFromManufactureAndModel(context);
        if (backend != null) {
            Log.i(TAG, "Using manufacture model.");
            return;
        }

        backend = objectFromModel(context);
        if (backend != null) {
            Log.i(TAG, "Using device model.");
            return;
        }

        backend = objectFromBrand(context);
        if (backend != null) {
            Log.i(TAG, "Using brand model");
            return;
        }

        Log.i(TAG, "Using default model.");
        backend = objectFromDefaultOnyxConfig(context);
    }

    private GObject objectFromBrand(Context context) {
        return objectFromRawResource(context, Build.BRAND);
    }

    private GObject objectFromManufactureAndModel(Context context) {
        final String name = Build.MANUFACTURER + "_" + Build.MODEL;
        return objectFromRawResource(context, name);
    }

    private GObject objectFromDebugModel(Context context) {
        if (BuildConfig.DEBUG && useDebugConfig) {
            return objectFromRawResource(context, "debug");
        }
        return null;
    }

    private GObject objectFromModel(Context context) {
        final String name = Build.MODEL;
        return objectFromRawResource(context, name);
    }

    private GObject objectFromDefaultOnyxConfig(Context context) {
        return objectFromRawResource(context, "onyx");
    }

    private GObject objectFromRawResource(Context context, final String name) {
        GObject object = null;
        try {
            int res = context.getResources().getIdentifier(name.toLowerCase(), "raw", context.getPackageName());
            object = RawResourceUtil.objectFromRawResource(context, res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return object;
        }
    }

    public int getRotationOffset(){
        if (backend.hasKey(ROTATION_OFFSET)) {
            return backend.getInt(ROTATION_OFFSET);
        }
        return 0;
    }

    public boolean getClearBeforeAnimation() {
      return backend.hasKey(CLEAR_BEFORE_ANIMATION) && backend.getBoolean(CLEAR_BEFORE_ANIMATION);
    }

    public boolean getSupportZipCompressedBooks() {
        return backend.hasKey(SUPPORT_ZIP_COMPRESSED_BOOKS) && backend.getBoolean(SUPPORT_ZIP_COMPRESSED_BOOKS);
    }

    public double getDefaultFontSize(Context context) {
        if (backend.hasKey(DEFAULT_FONT_SIZE)) {
            return backend.getDouble(DEFAULT_FONT_SIZE);
        }
        return ReaderOptions.getFallbackFontSize(context);
    }

    public boolean useBigPen() {
        return backend.hasKey(USE_BIG_PEN) && backend.getBoolean(USE_BIG_PEN);
    }

    /**
     * Set default use system status bar or not.
     * default return true.if nothing has set in json.
     * @return
     */
    public boolean defaultUseSystemStatusBar() {
        return !backend.hasKey(DEFAULT_USE_SYSTEM_STATUS_BAR) || backend.getBoolean(DEFAULT_USE_SYSTEM_STATUS_BAR, true);
    }

    /**
     * Set default use reader status bar or not.
     * default return false.if nothing has set in json.
     * @return
     */
    public boolean defaultUseReaderStatusBar() {
        return backend.hasKey(DEFAULT_USE_READER_STATUS_BAR) && backend.getBoolean(DEFAULT_USE_READER_STATUS_BAR, false);
    }

    /**
     * Get the default setting for whether to show doc/book title instead of file name in the status bar.
     * @return
     */
    public boolean defaultShowDocTitleInStatusBar() {
        return backend.hasKey(DEFAULT_SHOW_DOC_TITLE_IN_STATUS_BAR) && backend.getBoolean(DEFAULT_SHOW_DOC_TITLE_IN_STATUS_BAR, false);
    }

    public SingletonSharedPreference.AnnotationHighlightStyle defaultAnnotationHighlightStyle() {
        SingletonSharedPreference.AnnotationHighlightStyle style = SingletonSharedPreference.AnnotationHighlightStyle.Underline;
        if (backend.hasKey(DEFAULT_ANNOTATION_HIGHLIGHT_STYLE)) {
            String value = backend.getString(DEFAULT_ANNOTATION_HIGHLIGHT_STYLE);
            try {
                style = Enum.valueOf(SingletonSharedPreference.AnnotationHighlightStyle.class, value);
            } catch (Exception e) {
            }
        }
        return style;
    }

    public int getDialogNavigationSettingsSubScreenLandscapeRows() {
        return backend.getInt(DIALOG_NAVIGATION_SETTINGS_SUBSCREEN_LANDSCAPE_ROWS, -1);
    }

    public int getDialogNavigationSettingsSubScreenLandscapeColumns() {
        return backend.getInt(DIALOG_NAVIGATION_SETTINGS_SUBSCREEN_LANDSCAPE_COLUMNS, -1);
    }

    public int getSelectionOffsetX() {
        return backend.getInt(SELECTION_OFFSET_X, 15);
    }

    public boolean disableNavigation() {
        return backend.hasKey(DISABLE_NAVIGATION) && backend.getBoolean(DISABLE_NAVIGATION);
    }

    public boolean hideSelectionModeUiOption() {
        return backend.hasKey(HIDE_SELECTION_MODE_UI_OPTION) && backend.getBoolean(HIDE_SELECTION_MODE_UI_OPTION);
    }

    public int getSelectionMoveDistanceThreshold() {
        return backend.getInt(SELECTION_MOVE_DISTANCE_THRESHOLD, 8);
    }

    public int getDefaultGcInterval(int fallback) {
        if (backend.hasKey(GC_INTERVAL)) {
            return backend.getInt(GC_INTERVAL);
        }
        return fallback;
    }
}
