package com.onyx.android.libsetting.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Class to detect device feature,
 * For example:
 * Wifi/Bluetooth/touch etc.
 * Created by solskjaer49 on 2016/12/14 18:20.
 */

public class DeviceFeatureUtil {
    public static boolean hasWifi(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI);
    }

    public static boolean hasBluetooth(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }
}
