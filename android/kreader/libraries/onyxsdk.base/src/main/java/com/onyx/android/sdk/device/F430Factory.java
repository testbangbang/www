/**
 * 
 */
package com.onyx.android.sdk.device;

import java.io.File;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.fndroid.epdControlApi;
import com.onyx.android.sdk.data.util.ReflectUtil;
import com.onyx.android.sdk.device.EpdController.EPDMode;
import com.onyx.android.sdk.device.EpdController.UpdateMode;
import com.onyx.android.sdk.device.EpdController.UpdateScheme;

/**
 * @author joy
 *
 */
public class F430Factory implements IDeviceFactory
{
    private static final String TAG = F430Factory.class.getSimpleName();

    private static class F430DeviceController {
        public static Method sMethodSetBrightness;
    }


    public static class F430Controller extends IDeviceFactory.DefaultController
    {
        private static final String SAVE_SCREEN_BRIGHTNESS = "SAVE_SCREEN_BRIGHTNESS";

        /**
         * Brightness value for fully off
         */
        private static final int BRIGHTNESS_OFF = 0;
        /**
         * Brightness value for fully on
         */
        private static final int BRIGHTNESS_ON = 255;

        private static final int BRIGHTNESS_MINIMUM = BRIGHTNESS_OFF;
        private static final int BRIGHTNESS_MAXIMUM = BRIGHTNESS_ON;
        private static final int BRIGHTNESS_DEFAULT = BRIGHTNESS_MINIMUM;


        @Override
        public File getExternalStorageDirectory()
        {
            return Environment.getExternalStorageDirectory();
        }

        @Override
        public File getRemovableSDCardDirectory()
        {
            return Environment.getExternalStorageDirectory();
        }

        @Override
        public boolean isFileOnRemovableSDCard(File file)
        {
            return file.getAbsolutePath().startsWith(getRemovableSDCardDirectory().getAbsolutePath());
        }

        @Override
        public boolean isSmallScreen() {
            return true;
        }

        @Override
        public TouchType getTouchType(Context context)
        {
            return TouchType.Capacitive;
        }

        @Override
        public boolean hasWifi(Context context)
        {
            return true;
        }

        @Override
        public boolean hasAudio(Context context)
        {
            return true;
        }

        @Override
        public boolean hasFrontLight(Context context)
        {
            return true;
        }

        @Override
        public int getFrontLightBrightnessMinimum(Context context)
        {
            return BRIGHTNESS_MINIMUM;
        }

        @Override
        public int getFrontLightBrightnessMaximum(Context context)
        {
            return BRIGHTNESS_MAXIMUM;
        }

        public int getFrontLightBrightnessDefault(Context context){
            return BRIGHTNESS_DEFAULT;
        }

        @Override
        public boolean openFrontLight(Context context)
        {
            return setFrontLightDeviceValue(context, getFrontLightConfigValue(context));
        }

        @Override
        public boolean closeFrontLight(Context context)
        {
            return Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);
        }

        @Override
        public int getFrontLightDeviceValue(Context context)
        {
            int light_value;
            try {
                light_value = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException snfe) {
                light_value = BRIGHTNESS_DEFAULT;
            }

            return light_value;
        }

        @Override
        public boolean setFrontLightDeviceValue(Context context, int value)
        {
            Boolean res = (Boolean) ReflectUtil.invokeMethodSafely(F430DeviceController.sMethodSetBrightness, null, context, Integer.valueOf(value));
            if (res != null && res.booleanValue()) {
                return Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
            }
            return false;
        }

        @Override
        public int getFrontLightConfigValue(Context context)
        {
            int light_value;
            try {
                light_value = Settings.System.getInt(context.getContentResolver(), SAVE_SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException snfe) {
                light_value = BRIGHTNESS_DEFAULT;
            }
            return light_value;
        }

        @Override
        public boolean setFrontLightConfigValue(Context context, int value)
        {
            return Settings.System.putInt(context.getContentResolver(), SAVE_SCREEN_BRIGHTNESS, value);
        }

        @Override
        public boolean isEInkScreen()
        {
            return true;
        }

        @Override
        public EPDMode getEpdMode()
        {
            int mode = epdControlApi.epdGetwavmode();
            switch (mode) {
            case 1:
                return EPDMode.AUTO;
            case 2:
                return EPDMode.AUTO;
            case 3:
                return EPDMode.AUTO;
            case 4:
                return EPDMode.AUTO_A2;
            default:
                assert(false);
                return EPDMode.AUTO;
            }
        }

        @Override
        public boolean setEpdMode(Context context, EPDMode mode)
        {
            switch (mode) {
            case AUTO:
            case AUTO_PART:
            case TEXT:
                epdControlApi.epdScale16();
                break;
            case AUTO_A2:
            case AUTO_BLACK_WHITE:
                epdControlApi.epdScale2();
                break;
            case FULL:
                epdControlApi.epdRedraw();
                break;
            default:
                epdControlApi.epdScale2();
                break;
            }
            
            return true;
        }
        
        @Override
        public boolean setEpdMode(View view, EPDMode mode)
        {
            switch (mode) {
            case AUTO:
            case AUTO_PART:
            case TEXT:
                epdControlApi.epdScale16();
                break;
            case AUTO_A2:
            case AUTO_BLACK_WHITE:
                epdControlApi.epdScale2();
                break;
            case FULL:
                epdControlApi.epdRedraw();
                break;
            default:
                epdControlApi.epdScale2();
                break;
            }
            
            return true;
        }

        @Override
        public void invalidate(View view, UpdateMode mode)
        {
            switch (mode) {
            case GU:
            case GU_FAST:
                view.invalidate();
                break;
            case GC:
                epdControlApi.epdfulldraw();
                view.invalidate();
                break;
            case DW:
                view.invalidate();
                break;
            default:
                break;
            }
            
            return;
        }

        @Override
        public void postInvalidate(View view, UpdateMode mode)
        {
            switch (mode) {
            case GU:
            case GU_FAST:
                view.postInvalidate();
                break;
            case GC:
                view.postInvalidate();
                epdControlApi.epdRedraw();
                break;
            case DW:
                view.postInvalidate();
                break;
            default:
                break;
            }
            
            return;
        }

        @Override
        public UpdateMode getViewDefaultUpdateMode(View view)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean setViewDefaultUpdateMode(View view, UpdateMode mode)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public UpdateMode getSystemDefaultUpdateMode()
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean setSystemDefaultUpdateMode(UpdateMode mode)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean setSystemUpdateModeAndScheme(UpdateMode mode, UpdateScheme scheme, int count)
        {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean clearSystemUpdateModeAndScheme()
        {
            // TODO Auto-generated method stub
            return false;
        }
        
        public void epdRedraw()
        {
            epdControlApi.epdRedraw();
        }

        @Override
        public String getPlatform() {
            return Platforms.F430;
        }
    }

    @Override
    public String name()
    {
        return "F430";
    }

    @Override
    public boolean isPresent()
    {
        return Build.HARDWARE.equalsIgnoreCase("SP6820A");
    }

    @Override
    public IDeviceController createController()
    {
        try {
            Class<?> class_device_controller = Class.forName("android.hardware.DeviceController");
            F430DeviceController.sMethodSetBrightness = ReflectUtil.getMethodSafely(class_device_controller, "setBrightness",
                    Context.class, int.class);
        } catch (ClassNotFoundException e) {
            Log.w(TAG, e);
        }

        return new F430Controller();
    }

}
