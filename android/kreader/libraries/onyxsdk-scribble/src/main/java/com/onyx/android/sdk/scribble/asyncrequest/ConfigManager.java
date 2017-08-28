package com.onyx.android.sdk.scribble.asyncrequest;

import android.content.Context;

import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.scribble.utils.MappingConfig;

/**
 * Created by lxm on 2017/8/28.
 */

public class ConfigManager {

    private static ConfigManager ourInstance;

    private DeviceConfig deviceConfig;
    private MappingConfig mappingConfig;

    static public ConfigManager sharedInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new ConfigManager(context);
        }
        return ourInstance;
    }

    private ConfigManager(Context context) {
        deviceConfig = DeviceConfig.sharedInstance(context, "note");
        mappingConfig = MappingConfig.sharedInstance(context, "note");
    }

    public DeviceConfig getDeviceConfig() {
        return deviceConfig;
    }

    public MappingConfig getMappingConfig() {
        return mappingConfig;
    }
}
