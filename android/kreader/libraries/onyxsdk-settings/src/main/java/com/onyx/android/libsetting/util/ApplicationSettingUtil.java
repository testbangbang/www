package com.onyx.android.libsetting.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.onyx.android.sdk.utils.CompatibilityUtil;

/**
 * Created by solskjaer49 on 2016/12/12 11:37.
 */

public class ApplicationSettingUtil {
    private static final String ADB_KEY = CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1) ?
            Settings.Global.ADB_ENABLED : Settings.Secure.ADB_ENABLED;

    private static final String INSTALL_NON_MARKET_APPS_KEY = CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1) ?
            Settings.Global.INSTALL_NON_MARKET_APPS : Settings.Secure.INSTALL_NON_MARKET_APPS;

    public static boolean isEnableADB(Context context) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            return Settings.Global.getInt(context.getContentResolver(), ADB_KEY, 0) != 0;
        } else {
            return Settings.Secure.getInt(context.getContentResolver(), ADB_KEY, 0) != 0;
        }
    }

    public static void setADBEnabled(Context context, Boolean enabled) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            Settings.Global.putInt(context.getContentResolver(), ADB_KEY, enabled.compareTo(false));
        } else {
            Settings.Secure.putInt(context.getContentResolver(), ADB_KEY, enabled.compareTo(false));
        }
    }

    public static boolean isNonMarketAppsAllowed(Context context) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            return Settings.Global.getInt(context.getContentResolver(), INSTALL_NON_MARKET_APPS_KEY, 0) != 0;
        } else {
            return Settings.Secure.getInt(context.getContentResolver(), INSTALL_NON_MARKET_APPS_KEY, 0) != 0;
        }
    }

    public static void setNonMarketAppsAllowed(Context context, Boolean enabled) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            Settings.Global.putInt(context.getContentResolver(), INSTALL_NON_MARKET_APPS_KEY, enabled.compareTo(false));
        } else {
            Settings.Secure.putInt(context.getContentResolver(), INSTALL_NON_MARKET_APPS_KEY, enabled.compareTo(false));
        }
    }
}
