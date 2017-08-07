package com.onyx.android.sdk.data.model;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.onyx.android.sdk.utils.DeviceInfoUtil;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 8/17/15.
 */
public class Device extends BaseData {

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
    public String accountId;
    public String deviceUniqueId;
    public Map<String, String> installationMap = new HashMap<String, String>();
    public String name;
    public String details;

    static public Device updateCurrentDeviceInfo(final Context context) {
        if (currentDevice != null) {
            updateDeviceUniqueId(context, currentDevice);
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
        currentDevice.buildId = Build.ID;
        currentDevice.fingerprint = Build.FINGERPRINT;
        currentDevice.details = DeviceInfoUtil.deviceInfo();
        updateDeviceUniqueId(context, currentDevice);
        return currentDevice;
    }

    static private void updateDeviceUniqueId(Context context, Device device) {
        if (device == null) {
            return;
        }
        if (StringUtils.isNotBlank(device.macAddress) && StringUtils.isNotBlank(device.deviceUniqueId)) {
            return;
        }

        device.deviceUniqueId = device.macAddress = NetworkUtil.getMacAddress(context);
    }
}
