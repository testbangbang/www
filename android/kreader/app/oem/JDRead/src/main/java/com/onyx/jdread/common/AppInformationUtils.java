package com.onyx.jdread.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by huxiaomao on 17/3/27.
 */

public class AppInformationUtils {
    private static final String TAG = AppInformationUtils.class.getSimpleName();
    public static final String CPA_PROPERTIES = "cpa.properties";
    private static final String PERMISSION_READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";

    public static String getAppVersionName() {
        PackageInfo packageInfo = getPackageInfo();
        String versionName = "";
        if (packageInfo != null && StringUtils.isNotBlank(packageInfo.versionName)) {
            int index = packageInfo.versionName.indexOf(" ");
            if (index >= 0) {
                versionName = packageInfo.versionName.substring(0, index);
            } else {
                versionName = packageInfo.versionName;
            }
        }
        return versionName;
    }

    private static PackageInfo getPackageInfo() {
        try {
            Context cxt = JDReadApplication.getInstance();
            PackageInfo packageInfo = cxt.getPackageManager().getPackageInfo(
                    cxt.getPackageName(), 0);
            return packageInfo;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getPropertiesValue(String key) {
        InputStream asset = null;
        try {
            asset = JDReadApplication.getInstance().getAssets().open(CPA_PROPERTIES);
            Properties properties = new Properties();
            properties.load(asset);
            return properties.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                FileUtils.closeQuietly(asset);
            } catch (Exception e2) {
            }
        }
        return null;
    }

    public static String getScreenSize() {
        Display display = getDefaultDisplay();
        return display.getWidth() + "*" + display.getHeight();
    }

    public static Display getDefaultDisplay() {
        return ((WindowManager) JDReadApplication.getInstance().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay();

    }

    public static String getJDSingleTag() {
        return "";
    }

    public static String readDeviceUUID() {
        StringBuilder deviceUUID = new StringBuilder();

        String deviceId = getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            deviceId = deviceId.trim().replaceAll("-", "");
        }
        String wifiMAC = getMacAddress();

        if (!TextUtils.isEmpty(wifiMAC)) {
            wifiMAC = wifiMAC.trim().replaceAll("-|\\.|:", "");
        }
        if (!TextUtils.isEmpty(deviceId)) {
            deviceUUID.append(deviceId);
        }
        deviceUUID.append("-");
        if (!TextUtils.isEmpty(wifiMAC)) {
            deviceUUID.append(wifiMAC);
        }

        String deviceUUIDStr = deviceUUID.toString();
        return deviceUUIDStr;
    }

    public static String getMacAddress() {
        WifiManager wifi = (WifiManager) JDReadApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public static String getDeviceId() {
        try {
            if (isHaveDeviceIdGranted()) {
                TelephonyManager telephonyManager = (TelephonyManager) JDReadApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
                return telephonyManager == null ? null : telephonyManager.getDeviceId();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isHaveGranted(String permissionStr) {
        PackageManager packageManager = JDReadApplication.getInstance().getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                packageManager.checkPermission(permissionStr, JDReadApplication.getInstance().getPackageName()));
        return permission;
    }

    public static boolean isHaveDeviceIdGranted() {
        return isHaveGranted(PERMISSION_READ_PHONE_STATE);
    }

}