package com.onyx.android.dr.device;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.BuildConfig;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.utils.RawResourceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by suicheng on 2017/2/17.
 */
public class DeviceConfig {
    static private String TAG = DeviceConfig.class.getSimpleName();
    static public final boolean useDebugConfig = false;

    static public final String CLOUD_CONTENT_DEFAULT_HOST = "http://oa.o-in.me:9058/";
    static public final String CLOUD_CONTENT_DEFAULT_API = "http://oa.o-in.me:9058/api/";

    static private DeviceConfig globalInstance;
    static private Locale currentLocale = null;

    static private Map<String, String> defaultCustomizedIconAppsMap;

    private GObject backend;

    static public final String ROW_TAG = "row";
    static public final String COLUMN_TAG = "column";

    static public final String APP_FILTERS = "app_filters";
    static public final String TEST_APPS = "test_apps";
    static public final String CUSTOMIZED_ICON_APPS_MAPS = "customized_icon_apps_maps";

    static public final String VERIFY_DICTIONARY_TAG = "verify_dictionary";
    static public final String VERIFY_BOOKS_TAG = "verify_books";
    static public final String VERIFY_TTS_TAG = "verify_tts";
    static public final String CLOUD_CONTENT_HOST = "cloud_content_host";
    static public final String CLOUD_CONTENT_API = "cloud_content_api";
    static public final String CLOUD_CONTENT_IMPORT_JSON_FILE_PATH = "cloud_content_import_json_path";
    static public final String LEAN_CLOUD_APPLICATION_ID = "leanCloudAppId";
    static public final String LEAN_CLOUD_CLIENT_KEY = "leanCloudClientKey";

    static public final String CLOUD_INDEX_SERVER_USE = "cloud_index_server_use";
    static public final String CLOUD_MAIN_INDEX_SERVER_HOST = "cloud_main_index_server_host";
    static public final String CLOUD_MAIN_INDEX_SERVER_API = "cloud_main_index_server_api";

    static public final String MAIN_TAB_MENU_TAG = "main_tab_menu_tag";

    static public DeviceConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new DeviceConfig(context);
        }
        currentLocale = context.getResources().getConfiguration().locale;
        return globalInstance;
    }

    static public void forceUpdate(Context context) {
        globalInstance = new DeviceConfig(context);
        currentLocale = context.getResources().getConfiguration().locale;
    }

    private DeviceConfig(Context context) {
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

    /**
     * apps should not displayed in application tab. like phone, contacts not suitable for eReader.
     *
     * @return
     */
    public List<String> getAppFilters() {
        if (backend.hasKey(APP_FILTERS)) {
            return backend.getList(APP_FILTERS);
        }
        return null;
    }

    /**
     * Test apps, once finished, it will never displayed in app tab.
     *
     * @return
     */
    public List<String> getTestApps() {
        if (backend.hasKey(TEST_APPS)) {
            return backend.getList(TEST_APPS);
        }
        return null;
    }

    /**
     * Customized Icon apps,e.g Google play.
     *
     * @return Customized Apps And Icon Maps.
     */
    public Map<String, String> getCustomizedIconApps() {
        if (defaultCustomizedIconAppsMap == null) {
            defaultCustomizedIconAppsMap = new HashMap<>();
            defaultCustomizedIconAppsMap.put("com.android.vending", "app_play");
            defaultCustomizedIconAppsMap.put("com.android.browser", "app_browser");
            defaultCustomizedIconAppsMap.put("com.android.calendar", "app_calendar");
            defaultCustomizedIconAppsMap.put("com.android.calculator2", "app_calculator");
            defaultCustomizedIconAppsMap.put("com.android.deskclock", "app_deskclock");
            defaultCustomizedIconAppsMap.put("com.onyx.android.dict", "app_dict");
            defaultCustomizedIconAppsMap.put("com.onyx.dict", "app_dict");
            defaultCustomizedIconAppsMap.put("com.android.providers.downloads.ui", "app_download");
            defaultCustomizedIconAppsMap.put("com.android.email", "app_email");
            defaultCustomizedIconAppsMap.put("com.android.gallery", "app_gallery");
            defaultCustomizedIconAppsMap.put("com.android.music", "app_music");
            defaultCustomizedIconAppsMap.put("com.onyx.android.scribbler", "app_scribbler");
            defaultCustomizedIconAppsMap.put("com.neverland.oreader", "app_oreader");
            defaultCustomizedIconAppsMap.put("com.android.quicksearchbox", "app_search");
            defaultCustomizedIconAppsMap.put("com.android.settings", "app_setting");
            defaultCustomizedIconAppsMap.put("com.android.soundrecorder", "app_recorder");
            defaultCustomizedIconAppsMap.put("com.google.android.gms", "app_google_setting");
            defaultCustomizedIconAppsMap.put("com.onyx.android.note", "app_note");
            defaultCustomizedIconAppsMap.put("com.onyx.calculator", "app_calculator");
        }
        if (backend.hasKey(CUSTOMIZED_ICON_APPS_MAPS)) {
            defaultCustomizedIconAppsMap.putAll((Map<String, String>) (backend.getObject(CUSTOMIZED_ICON_APPS_MAPS)));
        }
        return defaultCustomizedIconAppsMap;
    }

    public String getCloudContentHost() {
        return backend.getString(CLOUD_CONTENT_HOST, CLOUD_CONTENT_DEFAULT_HOST);
    }

    public String getCloudContentApi() {
        return backend.getString(CLOUD_CONTENT_API, CLOUD_CONTENT_DEFAULT_API);
    }

    public String getCloudContentImportJsonFilePath() {
        return backend.getString(CLOUD_CONTENT_IMPORT_JSON_FILE_PATH, "");
    }

    public String getLeanCloudApplicationId() {
        return backend.getString(LEAN_CLOUD_APPLICATION_ID);
    }

    public String getLeanCloudClientKey() {
        return backend.getString(LEAN_CLOUD_CLIENT_KEY);
    }

    public String getCloudMainIndexServerHost() {
        return backend.getString(CLOUD_MAIN_INDEX_SERVER_HOST, "");
    }

    public String getCloudMainIndexServerApi() {
        return backend.getString(CLOUD_MAIN_INDEX_SERVER_API, "");
    }

    public boolean isUseCloudIndexServer() {
        return backend.getBoolean(CLOUD_INDEX_SERVER_USE);
    }

    public static class DeviceSettingInfo {
        public static final String DEVICE_SETTING = "device_setting";
    }

    private JSONObject getDeviceObject() {
        try {
            if (backend != null && backend.hasKey(DeviceSettingInfo.DEVICE_SETTING)) {
                Object object = backend.getObject(DeviceSettingInfo.DEVICE_SETTING);
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

    public static class MainMenuInfo {
        public static final String MAIN_MENU = "main_menu";
        public static final String MENU_GRADED_BOOKS = "menu_graded_books";
        public static final String MENU_MY_BOOKS = "menu_my_books";
        public static final String MENU_REAL_TIME_ARTICLES = "menu_real_time_articles";
        public static final String MENU_SCHOOL_BASED_MATERIALS = "menu_school_based_materials";
        public static final String MENU_PROFESSIONAL_MATERIALS = "menu_professional_materials";
        public static final String MENU_DICT = "menu_dict";
        public static final String MENU_NOTES = "menu_notes";
        public static final String MENU_LISTEN_AND_SAY = "menu_listen_and_say";
        public static final String MENU_APPLICATION = "menu_application";
        public static final String MENU_SETTINGS = "menu_settings";
        public static final String MENU_ARTICLE_PUSH = "menu_article_push";
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
        }
        return state;
    }
}
