/**
 * 
 */
package com.onyx.android.sdk.api.device;

import android.content.Context;
import com.onyx.android.sdk.device.BaseDevice;
import com.onyx.android.sdk.device.Device;

import java.util.List;

/**
 * @author Joy
 *
 */
public class FrontLightController
{

    public static int getBrightnessMinimum(Context context)
    {
        return Device.currentDevice().getFrontLightBrightnessMinimum(context);
    }
    
    public static int getBrightnessMaximum(Context context)
    {
        return Device.currentDevice().getFrontLightBrightnessMaximum(context);
    }

    public static boolean turnOn(Context context)
    {
        return Device.currentDevice().openFrontLight(context);
    }
    public static boolean turnOff(Context context)
    {
        return Device.currentDevice().closeFrontLight(context);
    }
    
    public static boolean isLightOn(Context context)
    {
        BaseDevice dev = Device.currentDevice();
        return dev.getFrontLightDeviceValue(context) > dev.getFrontLightBrightnessMinimum(context);
    }

    public static List<Integer> getFrontLightValueList(Context context) {
        return Device.currentDevice().getFrontLightValueList(context);
    }

    public static List<Integer> getNaturalLightValueList(Context context) {
        return Device.currentDevice().getNaturalLightValueList(context);
    }
    
    /**
     * value is valid only when light is on
     * 
     * @param context
     * @return
     */
    public static int getBrightness(Context context)
    {
        return Device.currentDevice().getFrontLightConfigValue(context);
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
        BaseDevice dev = Device.currentDevice();
        if (dev.setFrontLightDeviceValue(context, level)) {
            return dev.setFrontLightConfigValue(context, level);
        }
        
        return false;
    }

    public static boolean setNaturalBrightness(Context context, int level)
    {
        BaseDevice dev = Device.currentDevice();
        if (dev.setNaturalLightConfigValue(context, level)) {

            return dev.setNaturalLightConfigValue(context, level);
        }

        return false;
    }
}
