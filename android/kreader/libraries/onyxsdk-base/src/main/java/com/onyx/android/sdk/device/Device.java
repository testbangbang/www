/**
 * 
 */
package com.onyx.android.sdk.device;

import android.os.Build;

import com.onyx.android.sdk.utils.ReflectUtil;

import java.lang.reflect.Method;

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
        if (Build.HARDWARE.contains("freescale") && "imx7".equals(getBoardPlatform())) {
            return IMX7Device.createDevice();
        } else if (Build.HARDWARE.contains("freescale")) {
            return IMX6Device.createDevice();
        } else if (Build.HARDWARE.contentEquals("rk30board") && "rk3288".equals(getBoardPlatform())) {
            return RK32XXDevice.createDevice();
        } else if (Build.HARDWARE.contentEquals("rk30board")) {
            return RK3026Device.createDevice();
        }

        return new BaseDevice();
    }

    private static String getBoardPlatform() {
        Method getStringMethod = ReflectUtil.getDeclaredMethodSafely(Build.class, "getString", String.class);
        return (String) ReflectUtil.invokeMethodSafely(getStringMethod, null, "ro.board.platform");
    }
}
