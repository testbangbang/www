/**
 * 
 */
package com.onyx.android.sdk.device;

import android.os.Build;

/**
 * EPD work differently according to devices 
 * 
 * @author joy
 *
 */
public class Device
{
    @SuppressWarnings("unused")
    private final static String TAG = "Device";
    
    public static final BaseDevice currentDevice;
    
    static {
        currentDevice = Device.detectDevice();
    }

    public static BaseDevice currentDevice() {
        return currentDevice;
    }
    
    /**
     * never return null
     * 
     * @return
     */
    public synchronized static BaseDevice detectDevice()
    {
        if (Build.HARDWARE.contains("freescale")) {
            return IMX6Device.createDevice();
        }

        return new BaseDevice();
    }
}
