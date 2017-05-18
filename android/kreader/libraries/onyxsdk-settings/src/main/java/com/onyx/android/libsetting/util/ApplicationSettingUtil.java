package com.onyx.android.libsetting.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.onyx.android.sdk.utils.CompatibilityUtil;

/**
 * Created by solskjaer49 on 2016/12/12 11:37.
 */

public class ApplicationSettingUtil {
    private static final String ADB_KEY = CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1) ? Settings.Global.ADB_ENABLED : Settings.Secure.ADB_ENABLED;

    public static boolean isEnableADB(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), ADB_KEY, 0) != 0;
    }

    public static void setADBEnabled(Context context, Boolean enabled) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            Settings.Global.putInt(context.getContentResolver(), ADB_KEY, enabled.compareTo(false));
        } else {
            Settings.Secure.putInt(context.getContentResolver(), ADB_KEY, enabled.compareTo(false));
        }
    }

    public static boolean isNonMarketAppsAllowed(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(),
                Settings.Secure.INSTALL_NON_MARKET_APPS, 0) != 0;
    }

    public static void setNonMarketAppsAllowed(Context context, Boolean enabled) {
        Settings.Secure.putInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS,
                enabled.compareTo(false));
    }
}
