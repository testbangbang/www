/**
 * 
 */
package com.onyx.android.sdk.api.device;

import android.content.Context;
import com.onyx.android.sdk.device.BaseDevice;
import com.onyx.android.sdk.device.Device;

import java.util.Collections;
import java.util.List;

/**
 * @author Joy
 *
 */
public class FrontLightController
{

    public final static int FRONT_LIGHT_TYPE = 0;
    public final static int NATURAL_LIGHT_TYPE = 1;

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

    public static int getWarmLightDeviceValue(Context context) {
        return Device.currentDevice().getWarmLightDeviceValue(context);
    }

    public static int getColdLightDeviceValue(Context context) {
        return Device.currentDevice().getColdLightDeviceValue(context);
    }

    public static void adjustBrightnessBySliding(Context context, boolean increase, int lightType) {
        switch (lightType) {
            case FRONT_LIGHT_TYPE:
                adjustFrontLight(context, increase);
                break;
            case NATURAL_LIGHT_TYPE:
                adjustNaturalLight(context, increase);
                break;
        }
    }

    private static void adjustNaturalLight(Context context, boolean increase) {
        List<Integer> listValue = FrontLightController.getNaturalLightValueList(context);
        Collections.sort(listValue);
        int minValue = listValue.get(0);
        int maxValue = listValue.get(listValue.size() - 1);
        int deviceCold = FrontLightController.getColdLightDeviceValue(context);
        int deviceWarm = FrontLightController.getWarmLightDeviceValue(context);

        int diff = deviceCold - deviceWarm;
        if (diff > 0) { //cold higher
            if (increase) {
                if (deviceCold < maxValue) {
                    deviceCold += 1;
                }
            } else {
                if (deviceCold > minValue) {
                    deviceCold -= 1;
                }
            }
            deviceWarm = deviceCold - diff;
        } else { //warm higher or equal
            if (increase) {
                if (deviceWarm < maxValue) {
                    deviceWarm += 1;
                }
            } else {
                if (deviceWarm > minValue) {
                    deviceWarm -= 1;
                }
            }
            deviceCold = deviceWarm + diff;
        }
        if (deviceCold <= minValue) {
            deviceCold = minValue;
        }
        if (deviceWarm <= minValue) {
            deviceWarm = minValue;
        }
        if (deviceCold >= maxValue) {
            deviceCold = maxValue;
        }
        if (deviceWarm >= maxValue) {
            deviceWarm = maxValue;
        }
        FrontLightController.setColdLightDeviceValue(context, deviceCold);
        FrontLightController.setWarmLightDeviceValue(context, deviceWarm);
    }

    private static void adjustFrontLight(Context context, boolean increase) {
        int brightness = getBrightness(context);
        List<Integer> lightValueList = getFrontLightValueList(context);
        Collections.sort(lightValueList);
        int index = getIndex(brightness, lightValueList);
        index = increase ? index + 1 : index - 1;
        if (index > 0 && index < (lightValueList.size() - 1)) {
            int result = lightValueList.get(index);
            setBrightness(context, result);
            setBrightnessConfigValue(context, result);
        }
    }

    private static int getIndex(int val, List<Integer> list) {
        int index = Collections.binarySearch(list, val);
        if (index == -1) {
            index = 0;
        } else if (index < 0) {
            if (Math.abs(index) <= list.size()) {
                index = Math.abs(index) - 2;
            } else {
                index = list.size() - 1;
            }
        }
        return index;
    }
}
