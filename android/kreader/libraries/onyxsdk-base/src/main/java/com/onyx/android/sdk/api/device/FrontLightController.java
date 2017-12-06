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

    public final static int FRONT_LIGHT_TYPE = 0;
    public final static int WARN_LIGHT_TYPE = 1;
    public final static int COLD_LIGHT_TYPE = 2;

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
            return true;
        }
        
        return false;
    }

    public static boolean setBrightnessConfigValue(Context context, int value)
    {
        BaseDevice dev = Device.currentDevice();
        if (dev.setFrontLightConfigValue(context, value)) {
            return true;
        }
        return false;
    }

    public static boolean setNaturalBrightness(Context context, int level)
    {
        BaseDevice dev = Device.currentDevice();
        if (dev.setNaturalLightConfigValue(context, level)) {
            return true;
        }

        return false;
    }

    public static int getWarmLightConfigValue(Context context) {
        return Device.currentDevice().getWarmLightConfigValue(context);
    }

    public static int getColdLightConfigValue(Context context) {
        return Device.currentDevice().getColdLightConfigValue(context);
    }

    public static boolean setWarmLightConfigValue(Context context, int value) {
        return Device.currentDevice.setWarmLightConfigValue(context, value);
    }

    public static boolean setColdLightConfigValue(Context context, int value) {
        return Device.currentDevice.setColdLightConfigValue(context, value);
    }

    public static boolean setWarmLightDeviceValue(Context context, int level) {
        BaseDevice dev = Device.currentDevice();
        if (dev.setWarmLightDeviceValue(context, level)) {
            return true;
        }
        return false;
    }

    public static boolean setColdLightDeviceValue(Context context, int level) {
        BaseDevice dev = Device.currentDevice();
        if (dev.setColdLightDeviceValue(context, level)) {
            return true;
        }
        return false;
    }

    public static void adjustBrightnessBySliding(Context context, boolean increase, int lightType) {
        int brightness = 0;
        List<Integer> lightValueList = null;
        switch (lightType) {
            case FRONT_LIGHT_TYPE:
                brightness = getBrightness(context);
                lightValueList = getFrontLightValueList(context);
                break;
            case WARN_LIGHT_TYPE:
                brightness = getWarmLightConfigValue(context);
                lightValueList = getNaturalLightValueList(context);
                break;
            case COLD_LIGHT_TYPE:
                brightness = getColdLightConfigValue(context);
                lightValueList = getNaturalLightValueList(context);
                break;
        }

        if (lightValueList != null && lightValueList.contains(brightness)) {
            int index = lightValueList.indexOf(brightness);
            index = increase ? index + 1 : index - 1;
            if (index >= 0 && index < lightValueList.size()) {
                int result = lightValueList.get(index);
                adjustBrightness(context, result, lightType);
            }
        }
    }

    private static void adjustBrightness(Context context, int value, int lightType) {
        switch (lightType) {
            case FRONT_LIGHT_TYPE:
                setBrightness(context, value);
                setBrightnessConfigValue(context, value);
                break;
            case WARN_LIGHT_TYPE:
                setWarmLightDeviceValue(context, value);
                setWarmLightConfigValue(context, value);
                break;
            case COLD_LIGHT_TYPE:
                setColdLightDeviceValue(context, value);
                setColdLightConfigValue(context, value);
                break;
        }
    }
}
