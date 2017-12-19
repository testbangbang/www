package com.onyx.android.libsetting;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.onyx.android.libsetting.data.PowerSettingTimeoutCategory;
import com.onyx.android.libsetting.data.SettingCategory;
import com.onyx.android.libsetting.model.SettingItem;
import com.onyx.android.libsetting.util.Constant;
import com.onyx.android.libsetting.view.activity.StorageSettingActivity;
import com.onyx.android.sdk.data.DeviceType;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.utils.CompatibilityUtil;
import com.onyx.android.sdk.utils.RawResourceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_APPLICATION_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_BLUETOOTH_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_CALIBRATION_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_DATE_TIME_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_ERROR_REPORT_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_FIRMWARE_UPDATE_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_LANG_INPUT_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_NETWORK_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_POWER_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_PRODUCTION_TEST_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_PRODUCT_DETAIL_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_SECURITY_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_STORAGE_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_USER_SETTING_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_WIFI_TAG;

/**
 * Created by solskjaer49 on 2016/12/6 12:09.
 */

public class SettingConfig {
    static private String TAG = SettingConfig.class.getSimpleName();

    static class Custom {
        static private final String TTS_PACKAGE_NAME_TAG = "setting_tts_package_name";
        static private final String BLUETOOTH_PACKAGE_NAME_TAG = "setting_bluetooth_package_name";
        static private final String TTS_CLASS_NAME_TAG = "setting_tts_class_name";
        static private final String BLUETOOTH_CLASS_NAME_TAG = "setting_bluetooth_class_name";
        static private final String ICON_MAPS_TAG = "customized_setting_icon_maps";
        static private final String TITTLE_MAPS_TAG = "customized_setting_tittle_maps";
        static private final String ITEM_LIST_TAG = "setting_item_list";

        static private final String SCREEN_OFF_VALUES_TAG = "screen_screen_off_values";
        static private final String AUTO_POWER_OFF_VALUES_TAG = "power_off_timeout_values";
        static private final String WIFI_INACTIVITY_VALUES_TAG = "network_inactivity_timeout_values";

        static private final String WAKE_UP_FRONT_LIGHT_KEY_TAG = "system_wake_up_front_light_key";
        static private final String SCREEN_OFF_KEY_TAG = "system_screen_off_key";
        static private final String AUTO_POWER_OFF_KEY_TAG = "system_power_off_key";
        static private final String WIFI_INACTIVITY_KEY_TAG = "system_wifi_inactivity_key";
        static private final String ENABLE_KEY_BINDING_TAG = "enable_key_binding_key";
        static private final String HIDE_VPN_SETTING_TAG = "hide_vpn_setting";

        static private final String HAS_FRONT_LIGHT_TAG = "has_front_light";
        static private final String HAS_NATURAL_LIGHT_TAG = "has_natural_light";

        static private final String USE_SYSTEM_STORAGE_PAGE_TAG = "use_system_storage_page";

        static private final String HIDE_DRM_SETTING = "hide_drm_setting";

        static private final String CALIBRATION_PACKAGE_NAME_TAG = "calibration_package_name";
        static private final String CALIBRATION_CLASS_NAME_TAG = "calibration_class_name";

        static  private  final String TEST_APPS_TAG = "test_apps";

        static private final String ENABLE_AUTO_WIFI_RESCAN_TAG = "enable_auto_wifi_scan";
        static private final String CUSTOM_ROW_COUNT_TAG = "custom_row_count";
        static private final String USE_EDU_CONFIG = "use_edu_config";
        static private final String PRESET_CUSTOM_WIFI_SSID = "preset_custom_wifi_ssid";
        static private final String PRESET_CUSTOM_WIFI_PASSWORD = "preset_custom_wifi_password";
    }

    static class Default {
        static private final String ANDROID_SETTING_PACKAGE_NAME = "com.android.settings";
        static private final String ANDROID_SETTING_CLASS_NAME = "com.android.settings.Settings";
        static private final String TTS_CLASS_NAME = "com.android.settings.TextToSpeechSettings";
        static private final String ZONE_PICKER_CLASS_NAME = "com.android.settings.ZonePicker";
        static private final String POWER_USAGE_SUMMARY_CLASS_NAME = "com.android.settings.fuelgauge.PowerUsageSummary";
        static private final String FACTORY_RESET_CLASS_NAME = "com.android.settings.MasterClear";
        static private final String DEVICE_INFO_CLASS_NAME = "com.android.settings.DeviceInfoSettings";
        static private final String CALIBRATION_PACKAGE_NAME = "com.onyx.android.tscalibration";
        static private final String CALIBRATION_CLASS_NAME = "com.onyx.android.tscalibration.MainActivity";



        static private final String TTS_ACTION = "com.android.settings.TTS_SETTINGS";
        static private final String MASTER_CLEAR_ACTION = "android.settings.MASTER_CLEAR";
        static private final String TIME_ZONE_PICKER_ACTION = "android.settings.TIME_ZONE_SETTING";
        static private final String PRE_N_VPN_SETTING_ACTION = "android.net.vpn.SETTINGS";

        static public final String ONYX_PRESET_WIFI_SSID= "onyx-ap";
        static public final String ONYX_PRESET_WIFI_PASSWORD = "";
    }

    private boolean enableNetworkLatencyConfig = false;
    private boolean enableSystemSettings = false;
    private static SettingConfig globalInstance;
    static private final boolean useDebugConfig = false;
    private ArrayList<GObject> backendList = new ArrayList<>();
    static private
    @DeviceType.DeviceTypeDef
    int currentDeviceType;

    static private List<String> settingItemTAGList;
    static private Map<String, String> settingIconsMap;
    static private Map<String, String> settingTittleMap;

    public String getErrorReportAction() {
        return errorReportAction;
    }

    public SettingConfig setErrorReportAction(String errorReportAction) {
        this.errorReportAction = errorReportAction;
        return this;
    }

    private String errorReportAction;


    /**
     * New backend logic,to avoid duplicate property copy in json.
     * First,put model json in list(if have).
     * Second,put manufacture-based default json in list.
     * Third,put non-manufacture-based default json in list.
     * If debug property has,put it in before model.
     * <p>
     * model json:contain all custom properties which only effect on this model.(Do not copy the common properties to the json file!).
     * manufacture based json:contain all default properties which will be different by manufacture.
     *                       (Special case:imx6 platform has 2 different sdk version(ics and kitkat),
     *                       all imx6 special items should implement both json.
     * non manufacture based json:contain all default properties which are not depend on manufacture.
     *
     * @param context
     */
    private SettingConfig(Context context) {
        backendList.add(objectFromDebugModel(context));
        backendList.add(objectFromModel(context));
        backendList.add(objectFromManufactureBasedDefaultConfig(context));
        backendList.add(objectFromNonManufactureBasedDefaultConfig(context));
        backendList.removeAll(Collections.singleton(null));
    }

    static public SettingConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            getCurrentDeviceType();
            globalInstance = new SettingConfig(context);
        }
        return globalInstance;
    }

    static private void getCurrentDeviceType() {
        String hardware = Build.HARDWARE;
        if (hardware.startsWith(Constant.RK_PREFIX)) {
            currentDeviceType = DeviceType.RK;
            return;
        }
        currentDeviceType = DeviceType.IMX6;
    }

    @Nullable
    private <T> T getData(String dataKey, Class<T> clazz) {
        GObject backend = new GObject();
        for (GObject object : backendList) {
            if (object.hasKey(dataKey)) {
                backend = object;
                break;
            }
        }
        return backend.getBackend().getObject(dataKey, clazz);
    }

    private GObject objectFromManufactureBasedDefaultConfig(Context context) {
        String name = "";
        switch (currentDeviceType) {
            case DeviceType.IMX6:
                name = CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.KITKAT) ? Constant.IMX6_KIT_KAT_BASED_CONFIG_NAME :
                        Constant.IMX6_ICS_BASED_CONFIG_NAME;
                break;
            case DeviceType.RK:
                name = CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.M) ? Constant.RK32XX_BASED_CONFIG_NAME :
                        Constant.RK3026_BASED_CONFIG_NAME;
                break;
        }
        return objectFromRawResource(context, buildJsonConfigName(name));
    }

    private GObject objectFromDebugModel(Context context) {
        if (BuildConfig.DEBUG && useDebugConfig) {
            return objectFromRawResource(context, buildJsonConfigName(Constant.DEBUG_CONFIG_NAME));
        }
        return null;
    }

    private GObject objectFromModel(Context context) {
        final String name = Build.MODEL;
        return objectFromRawResource(context, buildJsonConfigName(name));
    }

    private GObject objectFromNonManufactureBasedDefaultConfig(Context context) {
        return objectFromRawResource(context, buildJsonConfigName(Constant.NON_MANUFACTURE_BASED_CONFIG_NAME));
    }

    private String buildJsonConfigName(String target) {
        return Constant.SETTING_JSON_PREFIX + target;
    }

    private GObject objectFromRawResource(Context context, final String name) {
        GObject object;
        try {
            int res = context.getResources().getIdentifier(name.toLowerCase(), "raw", context.getPackageName());
            object = RawResourceUtil.objectFromRawResource(context, res);
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Integer> getTimeoutValues(@PowerSettingTimeoutCategory.PowerSettingTimeoutCategoryDef
                                                  int item) {
        switch (item) {
            case PowerSettingTimeoutCategory.SCREEN_TIMEOUT:
                return getSystemScreenOffValues();
            case PowerSettingTimeoutCategory.POWER_OFF_TIMEOUT:
                return getSystemAutoPowerOffValues();
            case PowerSettingTimeoutCategory.WIFI_INACTIVITY_TIMEOUT:
                return getSystemWifiInactivityTimeoutValues();
            default:
                return new ArrayList<>();
        }
    }

    private List<Integer> getSystemScreenOffValues() {
        List<Integer> result = getData(Custom.SCREEN_OFF_VALUES_TAG, List.class);
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    private List<Integer> getSystemAutoPowerOffValues() {
        List<Integer> result = getData(Custom.AUTO_POWER_OFF_VALUES_TAG, List.class);
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    private List<Integer> getSystemWifiInactivityTimeoutValues() {
        List<Integer> result = getData(Custom.WIFI_INACTIVITY_VALUES_TAG, List.class);
        if (result == null) {
            return new ArrayList<>();
        }
        return result;
    }

    public String getSystemScreenOffKey() {
        String key = getData(Custom.SCREEN_OFF_KEY_TAG, String.class);
        if (TextUtils.isEmpty(key)) {
            key = Settings.System.SCREEN_OFF_TIMEOUT;
        }
        return key;
    }

    public String getSystemAutoPowerOffKey() {
        return getData(Custom.AUTO_POWER_OFF_KEY_TAG, String.class);
    }

    public String getSystemWifiInactivityKey() {
        return getData(Custom.WIFI_INACTIVITY_KEY_TAG, String.class);
    }

    public String getSystemWakeUpFrontLightKey() {
        return getData(Custom.WAKE_UP_FRONT_LIGHT_KEY_TAG, String.class);
    }

    public Intent getTTSSettingIntent() {
        Intent intent = new Intent();
        String pkgName = getData(Custom.TTS_PACKAGE_NAME_TAG, String.class);
        String className = getData(Custom.TTS_CLASS_NAME_TAG, String.class);
        if (TextUtils.isEmpty(pkgName) && TextUtils.isEmpty(className)) {
            intent = new Intent(Default.TTS_ACTION);
        } else {
            if (TextUtils.isEmpty(pkgName)) {
                pkgName = Default.ANDROID_SETTING_PACKAGE_NAME;
            }
            if (TextUtils.isEmpty(className)) {
                className = Default.TTS_CLASS_NAME;
            }
            intent.setClassName(pkgName, className);
        }
        return intent;
    }

    public Intent getTimeZoneSettingIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.ZONE_PICKER_CLASS_NAME);
        return CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.KITKAT) ?
                new Intent(Default.TIME_ZONE_PICKER_ACTION) : intent;
    }

    public Intent getBatteryStatusIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.POWER_USAGE_SUMMARY_CLASS_NAME);
        return new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
    }

    public Intent getApplicationManagementIntent() {
        return new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
    }

    public Intent getFactoryResetIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.FACTORY_RESET_CLASS_NAME);
        return CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.KITKAT) ?
                new Intent(Default.MASTER_CLEAR_ACTION) : intent;
    }

    private Intent buildDefaultSettingIntent() {
        Intent intent = new Intent();
        intent.setClassName(Default.ANDROID_SETTING_PACKAGE_NAME, Default.ANDROID_SETTING_CLASS_NAME);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        return intent;
    }

    public Intent getDRMSettingIntent() {
        return null;
    }

    public Intent getCalibrationIntent() {
        Intent intent = new Intent();
        String pkgName = Default.CALIBRATION_PACKAGE_NAME;
        String className = Default.CALIBRATION_CLASS_NAME;
        String customPkgName = getData(Custom.CALIBRATION_PACKAGE_NAME_TAG, String.class);
        String customClassName = getData(Custom.CALIBRATION_CLASS_NAME_TAG, String.class);
        intent.setClassName(TextUtils.isEmpty(customPkgName) ? pkgName : customPkgName,
                TextUtils.isEmpty(customClassName) ? className : customClassName);
        return intent;
    }

    public Intent getBluetoothSettingIntent() {
        return new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
    }

    public Intent getVPNSettingIntent() {
        return CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.N) ?
                new Intent(Settings.ACTION_VPN_SETTINGS) :
                new Intent(Default.PRE_N_VPN_SETTING_ACTION).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public Intent getDeviceInfoIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.DEVICE_INFO_CLASS_NAME);
        return CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.M) ?
                new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS) : intent;
    }

    public Intent getStorageSettingIntent(Context context) {
        return isUseSystemStoragePage() ? new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS) :
                new Intent(context, StorageSettingActivity.class);
    }

    private List<String> getSettingItemTAGList() {
        if (settingItemTAGList == null) {
            settingItemTAGList = new ArrayList<>();
            List<String> rawResourceList = getData(Custom.ITEM_LIST_TAG, List.class);
            if (rawResourceList == null || rawResourceList.size() == 0) {
                buildDefaultSettingTAGList();
            } else {
                settingItemTAGList.addAll(rawResourceList);
            }
        }
        return settingItemTAGList;
    }

    /**
     * Customized Icon For Setting Item.
     *
     * @return Customized Setting And Icon Maps.
     */
    private Map<String, String> getSettingIconMaps() {
        if (settingIconsMap == null) {
            buildDefaultSettingsIconsMap();
            Map<String, String> rawResourceMap = (Map<String, String>) (getData(Custom.ICON_MAPS_TAG, Map.class));
            if (rawResourceMap != null && rawResourceMap.size() != 0) {
                settingIconsMap.putAll(rawResourceMap);
            }
        }
        return settingIconsMap;
    }

    private Map<String, String> getSettingTittleMap() {
        if (settingTittleMap == null) {
            buildDefaultSettingsTittleMap();
            Map<String, String> rawResourceMap = (Map<String, String>) (getData(Custom.TITTLE_MAPS_TAG, Map.class));
            if (rawResourceMap != null && rawResourceMap.size() != 0) {
                settingTittleMap.putAll(rawResourceMap);
            }
        }
        return settingTittleMap;
    }

    private void buildDefaultSettingTAGList() {
        settingItemTAGList = new ArrayList<>();
        settingItemTAGList.add(SETTING_ITEM_NETWORK_TAG);
        settingItemTAGList.add(SETTING_ITEM_USER_SETTING_TAG);
        settingItemTAGList.add(SETTING_ITEM_POWER_TAG);
        settingItemTAGList.add(SETTING_ITEM_LANG_INPUT_TAG);
        settingItemTAGList.add(SETTING_ITEM_DATE_TIME_TAG);
        settingItemTAGList.add(SETTING_ITEM_APPLICATION_TAG);
        settingItemTAGList.add(SETTING_ITEM_STORAGE_TAG);
        settingItemTAGList.add(SETTING_ITEM_SECURITY_TAG);
        settingItemTAGList.add(SETTING_ITEM_ERROR_REPORT_TAG);
        settingItemTAGList.add(SETTING_ITEM_PRODUCTION_TEST_TAG);
    }

    private void buildDefaultSettingsIconsMap() {
        settingIconsMap = new HashMap<>();
        settingIconsMap.put(SETTING_ITEM_NETWORK_TAG, "ic_setting_network");
        settingIconsMap.put(SETTING_ITEM_USER_SETTING_TAG, "ic_user_setting");
        settingIconsMap.put(SETTING_ITEM_POWER_TAG, "ic_setting_power");
        settingIconsMap.put(SETTING_ITEM_LANG_INPUT_TAG, "ic_setting_language");
        settingIconsMap.put(SETTING_ITEM_DATE_TIME_TAG, "ic_setting_date");
        settingIconsMap.put(SETTING_ITEM_APPLICATION_TAG, "ic_setting_application");
        settingIconsMap.put(SETTING_ITEM_STORAGE_TAG, "ic_setting_storage");
        settingIconsMap.put(SETTING_ITEM_SECURITY_TAG, "ic_security");
        settingIconsMap.put(SETTING_ITEM_ERROR_REPORT_TAG, "ic_error_report");
        settingIconsMap.put(SETTING_ITEM_PRODUCTION_TEST_TAG, "ic_production_test");
        settingIconsMap.put(SETTING_ITEM_WIFI_TAG, "ic_setting_network");
        settingIconsMap.put(SETTING_ITEM_BLUETOOTH_TAG, "ic_setting_bluetooth");
        settingIconsMap.put(SETTING_ITEM_FIRMWARE_UPDATE_TAG, "ic_setting_ota");
        settingIconsMap.put(SETTING_ITEM_PRODUCT_DETAIL_TAG, "ic_setting_product_detail");
        settingIconsMap.put(SETTING_ITEM_CALIBRATION_TAG, "ic_setting_calibration");
    }

    private void buildDefaultSettingsTittleMap() {
        settingTittleMap = new HashMap<>();
        settingTittleMap.put(SETTING_ITEM_NETWORK_TAG, "setting_network");
        settingTittleMap.put(SETTING_ITEM_USER_SETTING_TAG, "setting_user_setting");
        settingTittleMap.put(SETTING_ITEM_POWER_TAG, "setting_power");
        settingTittleMap.put(SETTING_ITEM_LANG_INPUT_TAG, "setting_lang_input");
        settingTittleMap.put(SETTING_ITEM_DATE_TIME_TAG, "setting_date_time");
        settingTittleMap.put(SETTING_ITEM_APPLICATION_TAG, "setting_application");
        settingTittleMap.put(SETTING_ITEM_STORAGE_TAG, "setting_storage");
        settingTittleMap.put(SETTING_ITEM_SECURITY_TAG, "setting_security");
        settingTittleMap.put(SETTING_ITEM_ERROR_REPORT_TAG, "setting_error_report");
        settingTittleMap.put(SETTING_ITEM_PRODUCTION_TEST_TAG, "setting_production_test");
        settingTittleMap.put(SETTING_ITEM_WIFI_TAG, "setting_wifi");
        settingTittleMap.put(SETTING_ITEM_BLUETOOTH_TAG, "setting_bluetooth");
        settingTittleMap.put(SETTING_ITEM_FIRMWARE_UPDATE_TAG, "setting_ota");
        settingTittleMap.put(SETTING_ITEM_PRODUCT_DETAIL_TAG, "setting_product_detail");
        settingTittleMap.put(SETTING_ITEM_CALIBRATION_TAG, "setting_calibration");
    }

    public List<SettingItem> getSettingItemList(Context context) {
        List<String> settingItemStringList = new ArrayList<>();
        List<SettingItem> settingItemList = new ArrayList<>();
        settingItemStringList.addAll(getSettingItemTAGList());
        for (String tag : settingItemStringList) {
            settingItemList.add(new SettingItem(SettingCategory.translate(tag),
                    RawResourceUtil.getDrawableIdByName(context, getSettingIconMaps().get(tag)),
                    context.getString(RawResourceUtil.getStringIdByName(context, getSettingTittleMap().get(tag)))));
        }
        return settingItemList;
    }

    public boolean isEnableKeyBinding() {
        Boolean result = getData(Custom.ENABLE_KEY_BINDING_TAG, Boolean.class);
        if (result == null) {
            return false;
        }
        return result;
    }

    public boolean hasFrontLight() {
        Boolean result = getData(Custom.HAS_FRONT_LIGHT_TAG, Boolean.class);
        if (result == null) {
            return false;
        }
        return result;
    }

    public boolean hasNaturalLight() {
        Boolean result = getData(Custom.HAS_NATURAL_LIGHT_TAG, Boolean.class);
        if (result == null) {
            return false;
        }
        return result;
    }

    public boolean hideVPNSettings(){
        Boolean result = getData(Custom.HIDE_VPN_SETTING_TAG, Boolean.class);
        if (result == null) {
            return false;
        }
        return result;
    }

    private boolean isUseSystemStoragePage(){
        Boolean result = getData(Custom.USE_SYSTEM_STORAGE_PAGE_TAG, Boolean.class);
        if (result == null) {
            return true;
        }
        return result;
    }

    public boolean hideDRMSettings(){
        Boolean result = getData(Custom.HIDE_DRM_SETTING, Boolean.class);
        if (result == null) {
            return false;
        }
        return result;
    }

    public List<String> getTestApps() {
        List<String> result = getData(Custom.TEST_APPS_TAG, List.class);
        if (result == null) {
            return null;
        }
        return result;
    }

    public boolean isEnableSystemSettings() {
        return enableSystemSettings;
    }

    public boolean isEnableNetworkLatencyConfig() {
        return enableNetworkLatencyConfig;
    }

    public boolean isEnableAutoWifiReScan(){
        Boolean result = getData(Custom.ENABLE_AUTO_WIFI_RESCAN_TAG, Boolean.class);
        if (result == null) {
            return true;
        }
        return result;
    }

    public int customRowCount(){
        Integer result =  getData(Custom.CUSTOM_ROW_COUNT_TAG,Integer.class);
        if (result == null) {
            return -1;
        }
        return result;
    }

    public boolean isCustomRowCount() {
        return customRowCount() != -1;
    }

    public boolean useEduConfig(){
        Boolean result = getData(Custom.USE_EDU_CONFIG, Boolean.class);
        if (result == null) {
            return false;
        }
        return result;
    }

    public String[] getPresetCustomWifiInfo() {
        String[] result = new String[2];
        result[0] = getData(Custom.PRESET_CUSTOM_WIFI_SSID, String.class);
        if (TextUtils.isEmpty(result[0])) {
            result[0] = Default.ONYX_PRESET_WIFI_SSID;
        }
        result[1] = getData(Custom.PRESET_CUSTOM_WIFI_PASSWORD, String.class);
        if (TextUtils.isEmpty(result[1])) {
            result[1] = Default.ONYX_PRESET_WIFI_PASSWORD;
        }
        return result;
    }
}
