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
public class DeviceInfo
{
    @SuppressWarnings("unused")
    private final static String TAG = "DeviceInfo";

    public enum DeviceBrand { Standard, Artatech, ArtatechPlay, CasaDeLiBro, MacCentre }

    public enum TouchType { None, IR, Capacitive, Unknown }
    
    private static DeviceInfo sInstance = new DeviceInfo();

    
    public static final BaseDevice currentDevice;
    
    static {
        currentDevice = sInstance.detectDevice();
    }
    
    /**
     * never return null
     * 
     * @return
     */
    public synchronized static BaseDevice detectDevice()
    {
        if (Build.HARDWARE.contains("freescale")) {
            return new IMX6Device();
        }

        return new BaseDevice();
    }
}
