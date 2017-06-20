package com.onyx.android.sdk.api.device;

import com.onyx.android.sdk.device.Device;

import java.io.File;

/**
 * Created by joy on 2/20/17.
 */

public class DeviceEnvironment {

    public static File getStorageRootDirectory() {
        return Device.currentDevice().getStorageRootDirectory();
    }

    public static File getExternalStorageDirectory() {
        return Device.currentDevice().getExternalStorageDirectory();
    }

    public static File getRemovableSDCardDirectory() {
        return Device.currentDevice().getRemovableSDCardDirectory();
    }

}
