package com.onyx.android.libsetting.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.sdk.utils.CompatibilityUtil;

/**
 * Class to detect device feature,
 * For example:
 * Wifi/Bluetooth/touch/audio etc.
 * Created by solskjaer49 on 2016/12/14 18:20.
 */

public class DeviceFeatureUtil {
    public static boolean hasWifi(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI);
    }

    public static boolean hasBluetooth(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    public static boolean hasTouch(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);
    }

    // TODO: 2016/12/16 api for 4.0-4.4?
    public static boolean hasAudio(Context context) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.LOLLIPOP)) {
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT);
        }
        return true;
    }

    // TODO: 2016/12/16 need design
    public static boolean hasFrontLight(Context context) {
        return SettingConfig.sharedInstance(context).hasFrontLight();
    }

    public static boolean hasNaturalLight(Context context) {
        return SettingConfig.sharedInstance(context).hasNaturalLight();
    }
}
