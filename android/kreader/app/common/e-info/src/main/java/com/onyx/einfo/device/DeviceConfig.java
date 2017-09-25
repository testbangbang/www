package com.onyx.einfo.device;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.einfo.BuildConfig;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.data.model.common.DeviceInfoShowConfig;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.RawResourceUtil;

import java.util.ArrayList;
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
    static private Map<String, String> defaultCustomizedProductCoverMap;

    private GObject backend;

    static public final String APP_FILTERS = "app_filters";
    static public final String TEST_APPS = "test_apps";
    static public final String CUSTOMIZED_ICON_APPS_MAPS = "customized_icon_apps_maps";
    static public final String CUSTOMIZED_COVER_PRODUCTS_MAPS = "customized_cover_products_maps";

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

    static public final String DEVICE_QR_CODE_SHOW_CONFIG = "qr_code_show_config";
    static public final String DEVICE_INFO_SHOW_CONFIG = "info_show_config";

    static public final String HOME_ACTIVITY_PKG_NAME = "home_activity_pkg";
    static public final String HOME_ACTIVITY_CLS_NAME = "home_activity_cls";

    static public final String BOOKS_EXCLUDE_DIR_TAG = "books_exclude_dir";
    static public final String DEVICE_SUPPORT_COLOR = "support_color";

    static public final String CONTENT_MENU_ITEM_LIST = "content_menu_item_list";

    static public final String MEDIA_SCAN_SUPPORT = "media_scan_support";
    static public final String GALLERY_DIR = "gallery_dir";
    static public final String MUSIC_DIR = "music_dir";

    static public final String APPS_IGNORE_LIST = "app_ignore_list";
    static public final String CONTENT_READ_ONLY_TAG = "content_read_only";
    static public final String AUDIO_TAG = "audio_enable";
    static public final String SUPPORT_SCREEN_SAVER = "support_screen_saver";

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

        Log.i(TAG, "Using default model.");
        backend = objectFromDefaultOnyxConfig(context);
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

    public Map<String, String> getCustomizedProductCovers() {
        if (defaultCustomizedProductCoverMap == null) {
            defaultCustomizedProductCoverMap = new HashMap<>();
            defaultCustomizedProductCoverMap.put("default", "cover_default");
            defaultCustomizedProductCoverMap.put("mp3", "cover_mp3");
            defaultCustomizedProductCoverMap.put("wav", "cover_wav");
            defaultCustomizedProductCoverMap.put("apk", "cover_apk");
        }
        if (backend.hasKey(CUSTOMIZED_COVER_PRODUCTS_MAPS)) {
            defaultCustomizedProductCoverMap.putAll((Map<String, String>) (backend.getObject(CUSTOMIZED_COVER_PRODUCTS_MAPS)));
        }
        return defaultCustomizedProductCoverMap;
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
        return backend.getBoolean(CLOUD_INDEX_SERVER_USE, false);
    }

    public DeviceInfoShowConfig getQrCodeShowConfig() {
        if (!backend.hasKey(DEVICE_QR_CODE_SHOW_CONFIG)) {
            return null;
        }
        return JSONObjectParseUtils.parseObject(backend.getString(DEVICE_QR_CODE_SHOW_CONFIG), DeviceInfoShowConfig.class);
    }

    public DeviceInfoShowConfig getInfoShowConfig() {
        if (!backend.hasKey(DEVICE_INFO_SHOW_CONFIG)) {
            return null;
        }
        return JSONObjectParseUtils.parseObject(backend.getString(DEVICE_INFO_SHOW_CONFIG), DeviceInfoShowConfig.class);
    }

    public final String getHomeActivityPackageName() {
        if (backend.hasKey(HOME_ACTIVITY_PKG_NAME)) {
            return backend.getString(HOME_ACTIVITY_PKG_NAME);
        }
        return null;
    }

    public final String getHomeActivityClassName() {
        if (backend.hasKey(HOME_ACTIVITY_CLS_NAME)) {
            return backend.getString(HOME_ACTIVITY_CLS_NAME);
        }
        return null;
    }

    public List<String> getBookExcludeDirectories() {
        if (backend.hasKey(BOOKS_EXCLUDE_DIR_TAG)) {
            return backend.getList(BOOKS_EXCLUDE_DIR_TAG);
        }
        return null;
    }

    public boolean isDeviceSupportColor() {
        return backend.getBoolean(DEVICE_SUPPORT_COLOR, false);
    }

    public List<String> getContentMenuItemList() {
        if (backend.hasKey(CONTENT_MENU_ITEM_LIST)) {
            return backend.getList(CONTENT_MENU_ITEM_LIST);
        }
        return new ArrayList<>();
    }

    public boolean supportMediaScan() {
        return backend.getBoolean(MEDIA_SCAN_SUPPORT, true);
    }

    public List<String> getMusicDir() {
        if (backend.hasKey(MUSIC_DIR)) {
            return backend.getList(MUSIC_DIR);
        }
        return null;
    }

    public List<String> getGalleryDir() {
        if (backend.hasKey(GALLERY_DIR)) {
            return backend.getList(GALLERY_DIR);
        }
        return null;
    }

    public List<String> getMediaDir() {
        List<String> list = new ArrayList<>();
        if (!CollectionUtils.isNullOrEmpty(getMusicDir())) {
            list.addAll(getMusicDir());
        }
        if (!CollectionUtils.isNullOrEmpty(getGalleryDir())) {
            list.addAll(getGalleryDir());
        }
        return list;
    }

    public final boolean hasAudio() {
        return backend.getBoolean(AUDIO_TAG, true);
    }

    public final boolean isContentReadOnly() {
        return backend.getBoolean(CONTENT_READ_ONLY_TAG, false);
    }

    public boolean supportScreenSaver() {
        return backend.getBoolean(SUPPORT_SCREEN_SAVER, false);
    }

    public Map<String, JSONArray> getAppsIgnoreListMap() {
        if (backend.hasKey(APPS_IGNORE_LIST)) {
            return (Map<String, JSONArray>) (backend.getObject(APPS_IGNORE_LIST));
        }
        return new HashMap<>();
    }
}
