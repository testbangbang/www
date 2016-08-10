package com.onyx.cloud.model;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 8/17/15.
 */
public class Device extends BaseObject {

    static private Device currentDevice;

    public int width;
    public int height;
    public String model;
    public String brand;
    public String system;
    public String channel;
    public String buildId;
    public String fingerprint;
    public Map<String, String> hwinfo;
    public String timezone;
    public String macAddress;
    public String deviceUniqueId;
    public Map<String, String> installationMap = new HashMap<String, String>();
    public String name;

    static public Device updateCurrentDeviceInfo(final Context context) {
        if (currentDevice != null) {
            return currentDevice;
        }

        final WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        final WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null || windowManager == null) {
            return null;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        currentDevice = new Device();
        currentDevice.width = metrics.widthPixels;
        currentDevice.height = metrics.heightPixels;
        currentDevice.model = Build.MODEL;
        currentDevice.brand = Build.BRAND;
        currentDevice.fingerprint = Build.FINGERPRINT;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            currentDevice.macAddress = wifiInfo.getMacAddress();
            currentDevice.deviceUniqueId = currentDevice.macAddress;
        }
        return currentDevice;
    }

}
