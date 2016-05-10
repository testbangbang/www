/**
 * 
 */
package com.onyx.android.sdk.device;

import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

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

    
    public static final OnyxDevice currentDevice;
    
    static {
        currentDevice = sInstance.detectDevice();
    }
    
    /**
     * never return null
     * 
     * @return
     */
    public synchronized static OnyxDevice detectDevice()
    {
        if (Build.HARDWARE.contains("freescale")) {
            return new IMX6Device();
        }

        return new DefaultDevice();
    }
}
