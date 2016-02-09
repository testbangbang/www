package com.onyx.kreader.plugins.adobe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import com.onyx.kreader.utils.DeviceUtils;
import com.onyx.kreader.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 4/5/14
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReaderDeviceInfo {

    static private Map<String, String> infoMap = new HashMap<String, String>();

    private final static String PREFS_DEVICE_ID = "com.onyx.reader.adobe.drm.DEVICE_ID";

    public static void init(Context context) {
        infoMap.put("resourceURL", "/system/adobe/resources/");
        infoMap.put("userResourceURL", "/flash/adobe/resources/");
        infoMap.put("deviceName", Build.MODEL);
        infoMap.put("deviceSerial", getDeviceSerial(context));
        infoMap.put("applicationPrivateStorage", context.getFilesDir().getAbsolutePath());
    }

    public static String infoEntry(final String key) {
        String info = infoMap.get(key);
        if (StringUtils.isNullOrEmpty(info)) {
            return "";
        }
        return info;
    }

    private static String getDeviceSerial(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String uuid = prefs.getString(PREFS_DEVICE_ID, null);
        if (uuid == null || uuid.length() <= 0) {
            uuid = DeviceUtils.getDeviceSerial(context);
            if (!StringUtils.isNonBlank(uuid)) {
                prefs.edit().putString(PREFS_DEVICE_ID, uuid).commit();
            }
        }
        return uuid;
    }
}
