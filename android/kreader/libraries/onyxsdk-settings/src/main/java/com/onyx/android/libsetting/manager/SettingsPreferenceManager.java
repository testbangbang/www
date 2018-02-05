package com.onyx.android.libsetting.manager;

import android.content.Context;

import com.onyx.android.sdk.utils.PreferenceManager;

/**
 * Created by suicheng on 2017/3/10.
 */
public class SettingsPreferenceManager extends PreferenceManager {

    public static final String KEY_OTA_AUTO_CHECK = "ota_auto_check";
    public static final String KEY_OTA_CHECKING_POLICY = "ota_checking_policy";
    public static final String KEY_FORCE_AUTO_TIME_SLEEP = "force_auto_sleep_time";

    public static boolean isCheckFirmwareWhenWifiConnected(Context context) {
        return getBooleanValue(context, KEY_OTA_AUTO_CHECK, false);
    }

    public static void setFirmwareCheckWhenWifiConnected(Context context, boolean check) {
        setBooleanValue(context, KEY_OTA_AUTO_CHECK, check);
    }

    public static void setForceAutoSleepTime(Context content, int time) {
        setIntValue(content, KEY_FORCE_AUTO_TIME_SLEEP, time);
    }

    public static int getForceAutoSleepTime(Context context) {
        return getIntValue(context, KEY_FORCE_AUTO_TIME_SLEEP, -1);
    }
}
