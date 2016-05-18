/**
 * 
 */
package com.onyx.android.sdk.device;

import com.onyx.android.sdk.device.IDeviceFactory.IDeviceController;

import android.content.Context;

/**
 * @author Joy
 *
 */
public class FrontLightController
{

    public static int getBrightnessMinimum(Context context)
    {
        return DeviceInfo.currentDevice.getFrontLightBrightnessMinimum(context);
    }
    
    public static int getBrightnessMaximum(Context context)
    {
        return DeviceInfo.currentDevice.getFrontLightBrightnessMaximum(context);
    }

    public static boolean turnOn(Context context)
    {
        return DeviceInfo.currentDevice.openFrontLight(context);
    }
    public static boolean turnOff(Context context)
    {
        return DeviceInfo.currentDevice.closeFrontLight(context);
    }
    
    public static boolean isLightOn(Context context)
    {
        IDeviceController dev = DeviceInfo.currentDevice;
        return dev.getFrontLightDeviceValue(context) > dev.getFrontLightBrightnessMinimum(context);
    }
    
    /**
     * value is valid only when light is on
     * 
     * @param context
     * @return
     */
    public static int getBrightness(Context context)
    {
        return DeviceInfo.currentDevice.getFrontLightConfigValue(context);
    }
    
    /**
     * after set brightness, front light will be turned on simultaneously.
     * 
     * @param context
     * @param level
     * @return
     */
    public static boolean setBrightness(Context context, int level)
    {
        IDeviceController dev = DeviceInfo.currentDevice;
        if (dev.setFrontLightDeviceValue(context, level)) {
            return dev.setFrontLightConfigValue(context, level);
        }
        
        return false;
    }
}
