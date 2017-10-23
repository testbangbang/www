package com.onyx.android.sun.devicesetting;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.BuildConfig;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.utils.RawResourceUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 5/18/14
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppConfig {
    static private AppConfig globalInstance;
    static private String TAG = AppConfig.class.getSimpleName();
    private static final boolean useDebugConfig = false;
    private GObject backend;

    public static final String ROW_TAG = "row";
    public static final String COLUMN_TAG = "column";

    public static final String WIDTH_TAG = "width";
    public static final String HEIGHT_TAG = "height";

    public static final Map<String, Boolean> MENU_ITEM_DEFAULT_VALUES = new HashMap<>();

    static {

    }

    public static class DeviceSettingInfo {
        public static final String DEVICE_SETTING_TAG = "device_setting";
        public static final String PAGE_VIEW_INFO_TAG = "page_view_info";
        public static final int DEVICE_SETTING_PAGE_VIEW_ROW = 1;
        public static final int DEVICE_SETTING_PAGE_VIEW_COLUMN = 1;
        public static final String LANGUAGE_SETTINGS_PAGE_VIEW_INFO_TAG = "language_settings_page_view_info";
        public static final int DEVICE_SETTING_LANGUAGE_SETTINGS_PAGE_VIEW_ROW = 6;
        public static final int DEVICE_SETTING_LANGUAGE_SETTINGS_PAGE_VIEW_COLUMN = 1;

        public static final String WIFI_SETTINGS_PAGE_VIEW_INFO_TAG = "wifi_settings_page_view_info";
        public static final int DEVICE_SETTING_WIFI_SETTINGS_PAGE_VIEW_ROW = 5;
        public static final int DEVICE_SETTING_WIFI_SETTINGS_PAGE_VIEW_COLUMN = 1;

        public static final String WIFI_SETTINGS_PAGE_VIEW_SIZE_TAG = "wifi_settings_page_view_size";
        public static final float WIFI_SETTINGS_PAGE_VIEW_SIZE_WIDTH = 0.65f;
        public static final float WIFI_SETTINGS_PAGE_VIEW_SIZE_HEIGHT = 0.8f;
    }

    public static class MainMenuInfo {
        public static final String MAIN_MENU = "main_menu";
        public static final String MENU_BOOK_SHELF = "menu_book_shelf";
        public static final String MENU_READING_ROOM = "menu_reading_room";
        public static final String MENU_USER = "menu_user";
        public static final String MENU_GROUP_ONE = "menu_group_one";
        public static final String MENU_TEXT_SEARCH = "menu_text_search";
        public static final String MENU_VOICE_SEARCH = "menu_voice_search";
        public static final String MENU_GROUP_TWO = "menu_group_two";
        public static final String MENU_BACK = "menu_back";
        public static final String MENU_BRIGHTNESS = "menu_brightness";
        public static final String MENU_MORE = "menu_more";
        public static final String MENU_BOOK_STORE = "menu_book_store";
        public static final String MENU_CART = "menu_cart";
        public static final String MENU_SELECTOR = "menu_selector";
    }

    static public AppConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new AppConfig(context);
        }
        return globalInstance;
    }

    private AppConfig(Context context) {
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

    private JSONObject getDeviceObject() {
        try {
            if (backend != null && backend.hasKey(DeviceSettingInfo.DEVICE_SETTING_TAG)) {
                Object object = backend.getObject(DeviceSettingInfo.DEVICE_SETTING_TAG);
                if (object != null) {
                    return (JSONObject) object;
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject gitViewObject(final String tag) {
        JSONObject jsonObject = getDeviceObject();
        JSONObject viewObject = null;
        if (jsonObject != null) {
            viewObject = jsonObject.getJSONObject(tag);
        }
        return viewObject;
    }

    public int getDeviceSettingPageViewRow() {
        int row = DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_ROW;
        JSONObject pageViewInfo = gitViewObject(DeviceSettingInfo.PAGE_VIEW_INFO_TAG);
        if (pageViewInfo != null) {
            row = pageViewInfo.getInteger(ROW_TAG);
        }
        return row;
    }

    public int getDeviceSettingPageViewColumn() {
        int column = DeviceSettingInfo.DEVICE_SETTING_PAGE_VIEW_ROW;
        JSONObject pageViewInfo = gitViewObject(DeviceSettingInfo.PAGE_VIEW_INFO_TAG);
        if (pageViewInfo != null) {
            column = pageViewInfo.getInteger(COLUMN_TAG);
        }
        return column;
    }

    public int getDeviceSettingLanguageSettingsPageViewRow() {
        int row = DeviceSettingInfo.DEVICE_SETTING_LANGUAGE_SETTINGS_PAGE_VIEW_ROW;
        JSONObject languageSettingsInfo = gitViewObject(DeviceSettingInfo.LANGUAGE_SETTINGS_PAGE_VIEW_INFO_TAG);
        if (languageSettingsInfo != null) {
            row = languageSettingsInfo.getInteger(ROW_TAG);
        }
        return row;
    }

    public int getDeviceSettingLanguageSettingsPageViewColumn() {
        int column = DeviceSettingInfo.DEVICE_SETTING_LANGUAGE_SETTINGS_PAGE_VIEW_COLUMN;
        JSONObject languageSettingsInfo = gitViewObject(DeviceSettingInfo.LANGUAGE_SETTINGS_PAGE_VIEW_INFO_TAG);
        if (languageSettingsInfo != null) {
            column = languageSettingsInfo.getInteger(COLUMN_TAG);
        }
        return column;
    }

    public int getDeviceSettingWifiSettingsPageViewRow() {
        int row = DeviceSettingInfo.DEVICE_SETTING_WIFI_SETTINGS_PAGE_VIEW_ROW;
        JSONObject languageSettingsInfo = gitViewObject(DeviceSettingInfo.WIFI_SETTINGS_PAGE_VIEW_INFO_TAG);
        if (languageSettingsInfo != null) {
            row = languageSettingsInfo.getInteger(ROW_TAG);
        }
        return row;
    }

    public int getDeviceSettingWifiSettingsPageViewColumn() {
        int column = DeviceSettingInfo.DEVICE_SETTING_WIFI_SETTINGS_PAGE_VIEW_COLUMN;
        JSONObject languageSettingsInfo = gitViewObject(DeviceSettingInfo.WIFI_SETTINGS_PAGE_VIEW_INFO_TAG);
        if (languageSettingsInfo != null) {
            column = languageSettingsInfo.getInteger(COLUMN_TAG);
        }
        return column;
    }

    public float getWifiSettingsPageViewSizeWidth() {
        float width = DeviceSettingInfo.WIFI_SETTINGS_PAGE_VIEW_SIZE_WIDTH;
        JSONObject languageSettingsInfo = gitViewObject(DeviceSettingInfo.WIFI_SETTINGS_PAGE_VIEW_SIZE_TAG);
        if (languageSettingsInfo != null) {
            width = languageSettingsInfo.getFloat(WIDTH_TAG);
        }
        return width;
    }

    public float getWifiSettingsPageViewSizeHeight() {
        float height = DeviceSettingInfo.WIFI_SETTINGS_PAGE_VIEW_SIZE_HEIGHT;
        JSONObject languageSettingsInfo = gitViewObject(DeviceSettingInfo.WIFI_SETTINGS_PAGE_VIEW_SIZE_TAG);
        if (languageSettingsInfo != null) {
            height = languageSettingsInfo.getFloat(HEIGHT_TAG);
        }
        return height;
    }

    private JSONObject getMenuObject() {
        try {
            if (backend != null && backend.hasKey(MainMenuInfo.MAIN_MENU)) {
                Object object = backend.getObject(MainMenuInfo.MAIN_MENU);
                if (object != null) {
                    return (JSONObject) object;
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean getMainMenuItem(final String menuItem) {
        boolean state = true;
        JSONObject mainMenu = getMenuObject();
        if (mainMenu != null) {
            state = mainMenu.getBooleanValue(menuItem);
        } else {
            if (MENU_ITEM_DEFAULT_VALUES.containsKey(menuItem)) {
                state = MENU_ITEM_DEFAULT_VALUES.get(menuItem);
            }
        }
        return state;
    }

    public int getRecyclerViewRow(final String tag, final int defaultRow) {
        int row = defaultRow;
        JSONObject jsonObject = gitViewObject(tag);
        if (jsonObject != null) {
            row = jsonObject.getInteger(ROW_TAG);
        }
        return row;
    }

    public int getRecyclerViewColumn(final String tag, final int defaultColumn) {
        int column = defaultColumn;
        JSONObject jsonObject = gitViewObject(tag);
        if (jsonObject != null) {
            column = jsonObject.getInteger(COLUMN_TAG);
        }
        return column;
    }
}
