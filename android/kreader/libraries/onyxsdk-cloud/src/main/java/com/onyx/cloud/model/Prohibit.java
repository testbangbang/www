package com.onyx.cloud.model;

import android.os.Build;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 11/21/15.
 */
public class Prohibit extends BaseObject {
    static public final int STOP = 1;

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

    public int shouldStop;

    static public final Prohibit current() {
        Prohibit hwInfo = new Prohibit();
        hwInfo.fingerprint = Build.FINGERPRINT;
        hwInfo.model = Build.MODEL;
        hwInfo.brand = Build.BRAND;
        return hwInfo;
    }

}
