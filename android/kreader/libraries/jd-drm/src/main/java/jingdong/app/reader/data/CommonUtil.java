package com.jingdong.app.reader.data;

import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

public final class CommonUtil {
    private static final String TAG = CommonUtil.class.getSimpleName();

    public static String getDeviceId(final Context context) {
        try {
            if (isHaveDeviceIdGranted(context)) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return tm == null ? null : tm.getDeviceId();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isHaveDeviceIdGranted(final Context context) {
        return isHaveGranted(context, "android.permission.READ_PHONE_STATE");
    }

    public static boolean isHaveWifiConnectGranted(final Context context) {
        String permission = "android.permission.ACCESS_WIFI_STATE";
        return isHaveGranted(context, permission);
    }

    private static boolean isHaveGranted(final Context context, String permissionStr) {
        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(permissionStr, context.getPackageName()));
        return permission;
    }
}
