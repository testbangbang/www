package com.onyx.android.dr.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.text.TextUtils;

import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.PowerSettingTimeoutCategory;
import com.onyx.android.sdk.BuildConfig;
import com.onyx.android.sdk.data.DeviceType;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.utils.RawResourceUtil;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by solskjaer49 on 2016/12/6 12:09.
 */

public class SettingConfig {
    static private String TAG = SettingConfig.class.getSimpleName();

    static private final String CUSTOM_SETTING_TTS_PACKAGE_NAME_TAG = "setting_tts_package_name";
    static private final String CUSTOM_SETTING_BLUETOOTH_PACKAGE_NAME_TAG = "setting_bluetooth_package_name";
    static private final String DEFAULT_ANDROID_SETTING_PACKAGE_NAME = "com.android.settings";
    static private final String DEFAULT_ANDROID_SETTING_CLASS_NAME = "com.android.settings.Settings";
    static private final String CUSTOM_SETTING_TTS_CLASS_NAME_TAG = "setting_tts_class_name";
    static private final String CUSTOM_SETTING_BLUETOOTH_CLASS_NAME_TAG = "setting_bluetooth_class_name";
    static private final String DEFAULT_SETTING_TTS_CLASS_NAME = "com.android.settings.TextToSpeechSettings";
    static private final String DEFAULT_SETTING_BLUETOOTH_CLASS_NAME = "com.android.settings.bluetooth.BluetoothSettings";
    static private final String SYSTEM_SCREEN_OFF_VALUES_TAG = "screen_screen_off_values";
    static private final String SYSTEM_AUTO_POWER_OFF_VALUES_TAG = "power_off_timeout_values";
    static private final String SYSTEM_WIFI_INACTIVITY_VALUES_TAG = "network_inactivity_timeout_values";

    static private final String SYSTEM_WAKE_UP_FRONT_LIGHT_KEY_TAG = "system_wake_up_front_light_key";
    static private final String SYSTEM_SCREEN_OFF_KEY_TAG = "system_screen_off_key";
    static private final String SYSTEM_AUTO_POWER_OFF_KEY_TAG = "system_power_off_key";
    static private final String SYSTEM_WIFI_INACTIVITY_KEY_TAG = "system_wifi_inactivity_key";

    private static SettingConfig globalInstance;
    static private final boolean useDebugConfig = false;
    private ArrayList<GObject> backendList = new ArrayList<>();
    static private
    @DeviceType.DeviceTypeDef
    int currentDeviceType;
    private static final List<Integer> DEFAULT_SCREEN_OFF_VALUES = new ArrayList<>();
    private static final List<Integer> DEFAULT_SYSTEM_WIFI_INACTIVITY_VALUES = new ArrayList<>();
    private static final List<Integer> DEFAULT_SYSTEM_AUTO_POWER_OFF_VALUES = new ArrayList<>();

    static {
        DEFAULT_SYSTEM_WIFI_INACTIVITY_VALUES.add(3600000);
        DEFAULT_SYSTEM_WIFI_INACTIVITY_VALUES.add(7200000);
        DEFAULT_SYSTEM_WIFI_INACTIVITY_VALUES.add(-1);

        DEFAULT_SCREEN_OFF_VALUES.add(60000);
        DEFAULT_SCREEN_OFF_VALUES.add(120000);
        DEFAULT_SCREEN_OFF_VALUES.add(300000);
        DEFAULT_SCREEN_OFF_VALUES.add(600000);
        DEFAULT_SCREEN_OFF_VALUES.add(1800000);
        DEFAULT_SCREEN_OFF_VALUES.add(-1);

        DEFAULT_SYSTEM_AUTO_POWER_OFF_VALUES.add(3600000);
        DEFAULT_SYSTEM_AUTO_POWER_OFF_VALUES.add(7200000);
        DEFAULT_SYSTEM_AUTO_POWER_OFF_VALUES.add(-1);
    }


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
        backendList.add(objectFromManufactureAndModel(context));
        backendList.add(objectFromBrand(context));
        backendList.add(objectFromDefaultOnyxConfig(context));
        backendList.add(objectFromManufactureBasedDefaultConfig(context));
        backendList.add(objectFromNonManufactureBasedDefaultConfig(context));
        backendList.removeAll(Collections.singleton(null));
    }

    private GObject objectFromManufactureAndModel(Context context) {
        final String name = Build.MANUFACTURER + "_" + Build.MODEL;
        return objectFromRawResource(context, name);
    }

    private GObject objectFromBrand(Context context) {
        return objectFromRawResource(context, Build.BRAND);
    }

    private GObject objectFromDefaultOnyxConfig(Context context) {
        return objectFromRawResource(context, "onyx");
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
        if (hardware.startsWith(Constants.RK_PREFIX)) {
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
                name = Constants.IMX6_BASED_CONFIG_NAME;
                break;
            case DeviceType.RK:
                name = Constants.RK3026_BASED_CONFIG_NAME;
                break;
        }
        return objectFromRawResource(context, name);
    }

    private GObject objectFromDebugModel(Context context) {
        if (BuildConfig.DEBUG && useDebugConfig) {
            return objectFromRawResource(context, Constants.DEBUG_CONFIG_NAME);
        }
        return null;
    }

    private GObject objectFromModel(Context context) {
        final String name = Build.MODEL;
        return objectFromRawResource(context, name);
    }

    private GObject objectFromNonManufactureBasedDefaultConfig(Context context) {
        return objectFromRawResource(context, Constants.NON_MANUFACTURE_BASED_CONFIG_NAME);
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
        List<Integer> list = getData(SYSTEM_SCREEN_OFF_VALUES_TAG, List.class);
        if (list == null) {
            return DEFAULT_SCREEN_OFF_VALUES;
        }
        return list;
    }

    private List<Integer> getSystemAutoPowerOffValues() {
        List<Integer> list = getData(SYSTEM_AUTO_POWER_OFF_VALUES_TAG, List.class);
        if (list == null) {
            list = DEFAULT_SYSTEM_AUTO_POWER_OFF_VALUES;
        }
        return list;
    }

    private List<Integer> getSystemWifiInactivityTimeoutValues() {
        List<Integer> list = getData(SYSTEM_WIFI_INACTIVITY_VALUES_TAG, List.class);
        if (list == null) {
            return DEFAULT_SYSTEM_WIFI_INACTIVITY_VALUES;
        }
        return list;
    }

    public String getSystemScreenOffKey() {
        String key = getData(SYSTEM_SCREEN_OFF_KEY_TAG, String.class);
        if (TextUtils.isEmpty(key)) {
            key = Settings.System.SCREEN_OFF_TIMEOUT;
        }
        return key;
    }

    public String getSystemAutoPowerOffKey() {
        String key = getData(SYSTEM_AUTO_POWER_OFF_KEY_TAG, String.class);
        if (StringUtils.isNullOrEmpty(key)) {
            key = SYSTEM_AUTO_POWER_OFF_KEY_TAG;
        }
        return key;
    }

    public String getSystemWifiInactivityKey() {
        return getData(SYSTEM_WIFI_INACTIVITY_KEY_TAG, String.class);
    }

    public String getSystemWakeUpFrontLightKey() {
        return getData(SYSTEM_WAKE_UP_FRONT_LIGHT_KEY_TAG, String.class);
    }

    public Intent getTTSSettingIntent() {
        Intent intent = new Intent();
        String pkgName = getData(CUSTOM_SETTING_TTS_PACKAGE_NAME_TAG, String.class);
        if (TextUtils.isEmpty(pkgName)) {
            pkgName = DEFAULT_ANDROID_SETTING_PACKAGE_NAME;
        }
        String className = getData(CUSTOM_SETTING_TTS_CLASS_NAME_TAG, String.class);
        if (TextUtils.isEmpty(className)) {
            className = DEFAULT_SETTING_TTS_CLASS_NAME;
        }
        intent.setClassName(pkgName, className);
        if (CommonUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            intent = buildDefaultSettingIntent();
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.tts.TextToSpeechSettings");
        }
        return intent;
    }

    public Intent getTimeZoneSettingIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.ZonePicker");
        return intent;
    }

    public Intent getBatteryStatusIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.fuelgauge.PowerUsageSummary");
        return intent;
    }

    public Intent getApplicationManagementIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.applications.ManageApplications");
        return intent;
    }

    public Intent getFactoryResetIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.MasterClear");
        return intent;
    }

    private Intent buildDefaultSettingIntent() {
        Intent intent = new Intent();
        intent.setClassName(DEFAULT_ANDROID_SETTING_PACKAGE_NAME, DEFAULT_ANDROID_SETTING_CLASS_NAME);
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
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, DEFAULT_SETTING_BLUETOOTH_CLASS_NAME);
        return intent;
    }

    public Intent getVPNSettingIntent() {
        Intent intent = buildDefaultSettingIntent();
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "com.android.settings.vpn2.VpnSettings");
        return intent;
    }
}
