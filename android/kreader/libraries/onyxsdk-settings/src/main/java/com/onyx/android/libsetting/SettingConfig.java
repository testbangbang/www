package com.onyx.android.libsetting;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.text.TextUtils;

import com.onyx.android.libsetting.data.DeviceType;
import com.onyx.android.libsetting.data.PowerSettingTimeoutCategory;
import com.onyx.android.libsetting.data.SettingCategory;
import com.onyx.android.libsetting.model.SettingItem;
import com.onyx.android.libsetting.util.CommonUtil;
import com.onyx.android.libsetting.util.Constant;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.utils.RawResourceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_APPLICATION_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_DATE_TIME_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_ERROR_REPORT_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_LANG_INPUT_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_NETWORK_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_POWER_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_SECURITY_TAG;
import static com.onyx.android.libsetting.data.SettingCategory.SETTING_ITEM_USER_SETTING_TAG;

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
    }

    static class Default {
        static private final String ANDROID_SETTING_PACKAGE_NAME = "com.android.settings";
        static private final String ANDROID_SETTING_CLASS_NAME = "com.android.settings.Settings";
        static private final String TTS_CLASS_NAME = "com.android.settings.TextToSpeechSettings";
        static private final String BLUETOOTH_CLASS_NAME = "com.android.settings.bluetooth.BluetoothSettings";
        static private final String ZONE_PICKER_CLASS_NAME = "com.android.settings.ZonePicker";
        static private final String POWER_USAGE_SUMMARY_CLASS_NAME = "com.android.settings.fuelgauge.PowerUsageSummary";
        static private final String APPLICATION_MANAGEMENT_CLASS_NAME = "com.android.settings.applications.ManageApplications";
        static private final String FACTORY_RESET_CLASS_NAME = "com.android.settings.MasterClear";
        static private final String VPN_SETTING_CLASS_NAME = "com.android.settings.vpn2.VpnSettings";
        static private final String DEVICE_INFO_CLASS_NAME = "com.android.settings.DeviceInfoSettings";
    }


    private static SettingConfig globalInstance;
    static private final boolean useDebugConfig = false;
    private ArrayList<GObject> backendList = new ArrayList<>();
    static private
    @DeviceType.DeviceTypeDef
    int currentDeviceType;

    static private List<String> settingItemTAGList;
    static private Map<String, String> settingIconsMap;
    static private Map<String, String> settingTittleMap;


    /**
     * New backend logic,to avoid duplicate property copy in json.
     * First,put model json in list(if have).
     * Second,put manufacture-based default json in list.
     * Third,put non-manufacture-based default json in list.
     * If debug property has,put it in before model.
     * <p>
     * model json:contain all custom properties which only effect on this model.(Do not copy the common properties to the json file!).
     * manufacture based json:contain all default properties which will be different by manufacture.
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
        if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.M)) {
            currentDeviceType = DeviceType.IMX7;
        } else {
            currentDeviceType = DeviceType.IMX6;
        }
    }

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
                name = Constant.IMX6_BASED_CONFIG_NAME;
                break;
            case DeviceType.RK:
                name = Constant.RK3026_BASED_CONFIG_NAME;
                break;
        }
        return objectFromRawResource(context, name);
    }

    private GObject objectFromDebugModel(Context context) {
        if (BuildConfig.DEBUG && useDebugConfig) {
            return objectFromRawResource(context, Constant.DEBUG_CONFIG_NAME);
        }
        return null;
    }

    private GObject objectFromModel(Context context) {
        final String name = Build.MODEL;
        return objectFromRawResource(context, name);
    }

    private GObject objectFromNonManufactureBasedDefaultConfig(Context context) {
        return objectFromRawResource(context, Constant.NON_MANUFACTURE_BASED_CONFIG_NAME);
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
        return getData(Custom.SCREEN_OFF_VALUES_TAG, List.class);
    }

    private List<Integer> getSystemAutoPowerOffValues() {
        return getData(Custom.AUTO_POWER_OFF_VALUES_TAG, List.class);
    }

    private List<Integer> getSystemWifiInactivityTimeoutValues() {
        return getData(Custom.WIFI_INACTIVITY_VALUES_TAG, List.class);
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
        if (TextUtils.isEmpty(pkgName)) {
            pkgName = Default.ANDROID_SETTING_PACKAGE_NAME;
        }
        String className = getData(Custom.TTS_CLASS_NAME_TAG, String.class);
        if (TextUtils.isEmpty(className)) {
            className = Default.TTS_CLASS_NAME;
        }
        intent.setClassName(pkgName, className);
        if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            intent = buildDefaultSettingIntent();
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.TTS_CLASS_NAME);
        }
        return intent;
    }

    public Intent getTimeZoneSettingIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.ZONE_PICKER_CLASS_NAME);
        return intent;
    }

    public Intent getBatteryStatusIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.POWER_USAGE_SUMMARY_CLASS_NAME);
        return intent;
    }

    public Intent getApplicationManagementIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.APPLICATION_MANAGEMENT_CLASS_NAME);
        return intent;
    }

    public Intent getFactoryResetIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.FACTORY_RESET_CLASS_NAME);
        return intent;
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
        return null;
    }

    public Intent getKeyBindingIntent() {
        return null;
    }

    public Intent getBluetoothSettingIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.BLUETOOTH_CLASS_NAME);
        return intent;
    }

    public Intent getVPNSettingIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.VPN_SETTING_CLASS_NAME);
        return intent;
    }

    public Intent getDeviceInfoIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, Default.DEVICE_INFO_CLASS_NAME);
        return intent;
    }

    private List<String> getSettingItemTAGList() {
        if (settingItemTAGList == null) {
            settingItemTAGList = new ArrayList<>();
            List<String> rawResourceList = getData(Custom.ITEM_LIST_TAG, List.class);
            if (rawResourceList == null) {
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
            if (rawResourceMap != null) {
                settingIconsMap.putAll(rawResourceMap);
            }
        }
        return settingIconsMap;
    }

    private Map<String, String> getSettingTittleMap() {
        if (settingTittleMap == null) {
            buildDefaultSettingsTittleMap();
            Map<String, String> rawResourceMap = (Map<String, String>) (getData(Custom.TITTLE_MAPS_TAG, Map.class));
            if (rawResourceMap != null) {
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
        settingItemTAGList.add(SETTING_ITEM_SECURITY_TAG);
        settingItemTAGList.add(SETTING_ITEM_ERROR_REPORT_TAG);
    }

    private void buildDefaultSettingsIconsMap() {
        settingIconsMap = new HashMap<>();
        settingIconsMap.put(SETTING_ITEM_NETWORK_TAG, "ic_setting_network");
        settingIconsMap.put(SETTING_ITEM_USER_SETTING_TAG, "ic_user_setting");
        settingIconsMap.put(SETTING_ITEM_POWER_TAG, "ic_setting_power");
        settingIconsMap.put(SETTING_ITEM_LANG_INPUT_TAG, "ic_setting_language");
        settingIconsMap.put(SETTING_ITEM_DATE_TIME_TAG, "ic_setting_date");
        settingIconsMap.put(SETTING_ITEM_APPLICATION_TAG, "ic_setting_application");
        settingIconsMap.put(SETTING_ITEM_SECURITY_TAG, "ic_security");
        settingIconsMap.put(SETTING_ITEM_ERROR_REPORT_TAG, "ic_error_report");
    }

    private void buildDefaultSettingsTittleMap() {
        settingTittleMap = new HashMap<>();
        settingTittleMap.put(SETTING_ITEM_NETWORK_TAG, "setting_network");
        settingTittleMap.put(SETTING_ITEM_USER_SETTING_TAG, "setting_user_setting");
        settingTittleMap.put(SETTING_ITEM_POWER_TAG, "setting_power");
        settingTittleMap.put(SETTING_ITEM_LANG_INPUT_TAG, "setting_lang_input");
        settingTittleMap.put(SETTING_ITEM_DATE_TIME_TAG, "setting_date_time");
        settingTittleMap.put(SETTING_ITEM_APPLICATION_TAG, "setting_application");
        settingTittleMap.put(SETTING_ITEM_SECURITY_TAG, "setting_security");
        settingTittleMap.put(SETTING_ITEM_ERROR_REPORT_TAG, "setting_error_report");
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
        return getData(Custom.ENABLE_KEY_BINDING_TAG, Boolean.class);
    }
}
