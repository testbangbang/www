/**
 *
 */
package com.onyx.android.sdk.device;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;

import com.onyx.android.sdk.data.util.ReflectUtil;
import com.onyx.android.sdk.device.EpdController.EPDMode;
import com.onyx.android.sdk.device.EpdController.UpdateMode;

/**
 * @author joy
 *
 */
public class RK2906Factory implements IDeviceFactory
{
    private final static String TAG = "RK2906Factory";

    public static class RK2906Controller extends IDeviceFactory.DefaultController
    {
        private final static int DEFAULT_VIEW_MODE = 0;
        private final static String UNKNOWN = "unknown";
        private final static String DEVICE_ID = "ro.deviceid";
        private final static String SYSTEM_PROPERTIES_QUILIFIED_NAME = "android.os.SystemProperties";

        private static RK2906Controller sInstance = null;

        private static Method sMethodViewRequestEpdMode = null;
        private static int sViewNull = DEFAULT_VIEW_MODE;
        private static int sViewFull = DEFAULT_VIEW_MODE;
        private static int sViewA2 = DEFAULT_VIEW_MODE;
        private static int sViewAuto = DEFAULT_VIEW_MODE;
        private static int sViewPart = DEFAULT_VIEW_MODE;

        private EPDMode mCurrentMode = EPDMode.AUTO;

        private static Method sMethodDialogRequestFullWhenHidden; 

        private static Constructor<?> sDeviceControllerConstructor = null;

        private static Method sMethodIsTouchable;
        private static Method sMethodGetTouchType;
        private static Method sMethodHasWifi;
        private static Method sMethodHasAudio;
        private static Method sMethodHasFrontLight;
        private static Method sMethodHas5WayButton;
        private static Method sMethodHasPageButton;
        private static Method sMethodOpenFrontLight;
        private static Method sMethodCloseFrontLight;
        private static Method sMethodGetFrontLightValue;
        private static Method sMethodSetFrontLightValue;
        private static Method sMethodWifiLock;
        private static Method sMethodWifiUnlock;
        private static Method sMethodWifiLockClear;
        private static Method sMethodGetWifiLockMap;
        private static Method sMethodSetWifiLockTimeout;
        
        private static int sTouchTypeUnknown = 0;
        private static int sTouchTypeIR = 1;
        private static int sTouchTypeCapacitive = 2;

        private RK2906Controller()
        {
        }

        public static RK2906Controller createController()
        {
            if (sInstance == null) {
                Class<View> class_view = View.class;
                sMethodViewRequestEpdMode = ReflectUtil.getMethodSafely(class_view, "requestEpdMode", int.class);
                sViewNull = ReflectUtil.getStaticIntFieldSafely(class_view, "EPD_NULL");
                sViewFull = ReflectUtil.getStaticIntFieldSafely(class_view, "EPD_FULL");
                sViewA2 = ReflectUtil.getStaticIntFieldSafely(class_view, "EPD_A2");
                sViewAuto = ReflectUtil.getStaticIntFieldSafely(class_view, "EPD_AUTO");
                sViewPart = ReflectUtil.getStaticIntFieldSafely(class_view, "EPD_PART");
                
                Class<Dialog> class_dialog = Dialog.class;
                sMethodDialogRequestFullWhenHidden = ReflectUtil.getMethodSafely(class_dialog, "requestFullWhenHidden", boolean.class);

                try {
                    Class<?> class_device_controller = Class.forName("android.hardware.DeviceController");
                    sTouchTypeUnknown = ReflectUtil.getStaticIntFieldSafely(class_device_controller, "TOUCH_TYPE_UNKNOWN");
                    sTouchTypeIR = ReflectUtil.getStaticIntFieldSafely(class_device_controller, "TOUCH_TYPE_IR");
                    sTouchTypeCapacitive = ReflectUtil.getStaticIntFieldSafely(class_device_controller, "TOUCH_TYPE_CAPACITIVE");
                    
                    sDeviceControllerConstructor = ReflectUtil.getConstructorSafely(class_device_controller, Context.class);
                    sMethodIsTouchable = ReflectUtil.getMethodSafely(class_device_controller, "isTouchable");
                    sMethodGetTouchType = ReflectUtil.getMethodSafely(class_device_controller, "getTouchType");
                    sMethodHasWifi = ReflectUtil.getMethodSafely(class_device_controller, "hasWifi");
                    sMethodHasAudio = ReflectUtil.getMethodSafely(class_device_controller, "hasAudio");
                    sMethodHasFrontLight = ReflectUtil.getMethodSafely(class_device_controller, "hasFrontLight");

                    sMethodHas5WayButton = ReflectUtil.getMethodSafely(class_device_controller, "has5WayButton");
                    sMethodHasPageButton = ReflectUtil.getMethodSafely(class_device_controller, "hasPageButton");

                    sMethodOpenFrontLight = ReflectUtil.getMethodSafely(class_device_controller, "openFrontLight");
                    sMethodCloseFrontLight = ReflectUtil.getMethodSafely(class_device_controller, "closeFrontLight");
                    sMethodGetFrontLightValue = ReflectUtil.getMethodSafely(class_device_controller, "getFrontLightValue");
                    sMethodSetFrontLightValue = ReflectUtil.getMethodSafely(class_device_controller, "setFrontLightValue", int.class);

                    sMethodWifiLock = ReflectUtil.getMethodSafely(class_device_controller, "wifiLock", String.class);
                    sMethodWifiUnlock = ReflectUtil.getMethodSafely(class_device_controller, "wifiUnlock", String.class);
                    sMethodWifiLockClear = ReflectUtil.getMethodSafely(class_device_controller, "wifiLockClear");
                    sMethodGetWifiLockMap = ReflectUtil.getMethodSafely(class_device_controller, "getWifiLockMap");
                    sMethodSetWifiLockTimeout = ReflectUtil.getMethodSafely(class_device_controller, "setWifiLockTimeout", long.class);
                }
                catch (ClassNotFoundException e) {
                    Log.w(TAG, e);
                }

                sInstance = new RK2906Controller();
                return sInstance;
            }

            return sInstance;
        }
        
        @Override
        public File getStorageRootDirectory()
        {
            return this.getExternalStorageDirectory();
        }

        @Override
        public File getExternalStorageDirectory()
        {
            return new File("/mnt/storage");
        }

        @Override
        public File getRemovableSDCardDirectory()
        {
            File storage_root = this.getExternalStorageDirectory();

            // if system has an emulated SD card(/mnt/sdcard) provided by device's NAND flash,
            // then real SD card will be mounted as a child directory(/mnt/sdcard/extsd) in it, which names "extsd" here
            final String SDCARD_MOUNTED_FOLDER = "sdcard";
            File extsd = new File(storage_root, SDCARD_MOUNTED_FOLDER);
            return extsd;
        }

        @Override
        public boolean isFileOnRemovableSDCard(File file)
        {
            return file.getAbsolutePath().startsWith(getRemovableSDCardDirectory().getAbsolutePath());
        }
        
        @Override
        public WakeLock newWakeLock(Context context, String tag)
        {
            PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            return pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, tag);
        }

        @Override
        public TouchType getTouchType(Context context)
        {
            Boolean succ = (Boolean)this.invokeDeviceControllerMethod(context,
                    sDeviceControllerConstructor, sMethodIsTouchable);
            if (succ == null || !succ.booleanValue()) {
                return TouchType.None;
            }

            Integer n = (Integer)this.invokeDeviceControllerMethod(context, 
                    sDeviceControllerConstructor, sMethodGetTouchType);
            if (n.intValue() == sTouchTypeUnknown) {
                return TouchType.Unknown;
            }
            else if (n.intValue() == sTouchTypeIR) {
                return TouchType.IR;
            }
            else if (n.intValue() == sTouchTypeCapacitive) {
                return TouchType.Capacitive;
            }
            else {
                assert(false);
                return TouchType.Unknown;
            }
        }

        @Override
        public boolean hasWifi(Context context)
        {
            Boolean has = (Boolean)this.invokeDeviceControllerMethod(context, 
                    sDeviceControllerConstructor, sMethodHasWifi);
            if (has == null) {
                return false;
            }
            
            return has.booleanValue();
        }

        @Override
        public boolean hasAudio(Context context)
        {
            Boolean has = (Boolean)this.invokeDeviceControllerMethod(context, 
                    sDeviceControllerConstructor, sMethodHasAudio);
            if (has == null) {
                return false;
            }
            
            return has.booleanValue();
        }

        @Override
        public boolean hasFrontLight(Context context)
        {
            Boolean has = (Boolean)this.invokeDeviceControllerMethod(context, 
                    sDeviceControllerConstructor, sMethodHasFrontLight);
            if (has == null) {
                return false;
            }
            
            return has.booleanValue();
        }
        
        @Override
        public boolean has5WayButton(Context context)
        {
            Boolean has = (Boolean)this.invokeDeviceControllerMethod(context, 
                    sDeviceControllerConstructor, sMethodHas5WayButton);
            if (has == null) {
                return false;
            }
            
            return has.booleanValue();
        }
        
        @Override
        public boolean hasPageButton(Context context)
        {
            Boolean has = (Boolean)this.invokeDeviceControllerMethod(context,
                    sDeviceControllerConstructor, sMethodHasPageButton);
            if (has == null) {
                return false;
            }
            
            return has.booleanValue();
        }

        @Override
        public void wifiLock(Context context, String className)
        {
            this.invokeDeviceControllerMethod(context, sDeviceControllerConstructor,
                    sMethodWifiLock);
        }

        @Override
        public void wifiUnlock(Context context, String className)
        {
            this.invokeDeviceControllerMethod(context, sDeviceControllerConstructor,
                    sMethodWifiUnlock);
            
        }

        @Override
        public void wifiLockClear(Context context)
        {
            this.invokeDeviceControllerMethod(context, sDeviceControllerConstructor, 
                    sMethodWifiLockClear);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map<String, Integer> getWifiLockMap(Context context)
        {
            return (Map<String, Integer>)this.invokeDeviceControllerMethod(context,
                    sDeviceControllerConstructor,
                    sMethodGetWifiLockMap);
        }

        @Override
        public void setWifiLockTimeout(Context context, long ms)
        {
            this.invokeDeviceControllerMethod(context, sDeviceControllerConstructor,
                    sMethodSetWifiLockTimeout, Long.valueOf(ms));
            
        }
        
        @Override
        public int getFrontLightBrightnessMinimum(Context context)
        {
            return 0;
        }
        
        @Override
        public int getFrontLightBrightnessMaximum(Context context)
        {
            return 255;
        }
        
        @Override
        public boolean openFrontLight(Context context)
        {
            Boolean succ = (Boolean)this.invokeDeviceControllerMethod(context,
                    sDeviceControllerConstructor, sMethodOpenFrontLight);
            if (succ == null) {
                return false;
            }
            
            return succ.booleanValue();
        }
        
        @Override
        public boolean closeFrontLight(Context context)
        {
            Boolean succ = (Boolean)this.invokeDeviceControllerMethod(context,
                    sDeviceControllerConstructor, sMethodCloseFrontLight);
            if (succ == null) {
                return false;
            }
            
            return succ.booleanValue();
        }
        
        @Override
        public int getFrontLightDeviceValue(Context context)
        {
            Integer value = (Integer)this.invokeDeviceControllerMethod(context,
                    sDeviceControllerConstructor, sMethodGetFrontLightValue);
            if (value == null) {
                return 0;
            }
            return value.intValue();
        }
        
        @Override
        public boolean setFrontLightDeviceValue(Context context, int value)
        {
            return this.invokeDeviceControllerMethod(context, sDeviceControllerConstructor,
                    sMethodSetFrontLightValue, Integer.valueOf(value)) != null;
        }
        
        @Override
        public int getFrontLightConfigValue(Context context)
        {
            int res = 0;
            int light_value;
            try {
                light_value = Settings.System.getInt(context.getContentResolver(), 
                        Settings.System.SCREEN_BRIGHTNESS);
            } catch (SettingNotFoundException snfe) {
                final int DEFAULT = this.getFrontLightBrightnessMinimum(context) + 20;
                light_value = DEFAULT;
            }

            res = light_value;
            return res;
        }
        
        @Override
        public boolean setFrontLightConfigValue(Context context, int value)
        {
            return Settings.System.putInt(context.getContentResolver(), 
                    Settings.System.SCREEN_BRIGHTNESS, value);
        }

        @Override
        public List<Integer> getFrontLightValueList(Context context) {
            Integer intValues[] = {0,5,10,15,20,25,30,35,40,45,50,55,60,65,70,75,80,85,90,95,100,105,110,115,120,125,130,135,140,145,150,155,160,175,180,185,190,195,200,205,210,215,220,225,230,235,240,245,250};
            return Arrays.asList(intValues);
        }

        @Override
        public boolean isEInkScreen()
        {
            return true;
        }

        @Override
        public EpdController.EPDMode getEpdMode()
        {
            return mCurrentMode;
        }

        @Override
        public boolean setEpdMode(View view, EPDMode mode)
        {
            if (sMethodViewRequestEpdMode == null) {
                return false;
            }
            
            int m = this.convertFromEpdMode(mode);
            if (ReflectUtil.invokeMethodSafely(sMethodViewRequestEpdMode,
                    view, Integer.valueOf(m)) != null) {
                mCurrentMode = mode;
                return true;
            }
            return false;
        }

        @Override
        public void invalidate(View view, UpdateMode mode)
        {
            if (sMethodViewRequestEpdMode == null) {
                return;
            }
            
            int m = this.convertFromUpdateMode(mode);
            ReflectUtil.invokeMethodSafely(sMethodViewRequestEpdMode, view, Integer.valueOf(m));
            view.invalidate();
        }

        @Override
        public void postInvalidate(View view, UpdateMode mode)
        {
            if (sMethodViewRequestEpdMode == null) {
                return;
            }
            
            int m = this.convertFromUpdateMode(mode);
            ReflectUtil.invokeMethodSafely(sMethodViewRequestEpdMode, view, Integer.valueOf(m));
            view.postInvalidate();
        }
        
        public void setDialogRequestFullWhenHidden(Dialog dlg, boolean full)
        {
            if (sMethodDialogRequestFullWhenHidden == null) {
                return;
            }
            
            ReflectUtil.invokeMethodSafely(sMethodDialogRequestFullWhenHidden, dlg, Boolean.valueOf(full));
        }
        
        /**
         * helper method to do trivial argument and exception check, return null if failed
         * 
         * @param context
         * @param constructor
         * @param method
         * @return
         */
        private Object invokeDeviceControllerMethod(Context context, Constructor<?> constructor, Method method, Object... args)
        {
            if (constructor == null || method == null) {
                return null;
            }

            Object obj = ReflectUtil.newInstance(constructor, context);
            if (obj == null) {
                return null;
            }
            
            return ReflectUtil.invokeMethodSafely(method,  obj, args);
        }
        
        private int convertFromEpdMode(EPDMode mode)
        {
            int m = sViewNull;
            switch (mode) {
            case FULL:
                m = sViewFull;
                break;
            case AUTO:
                m = sViewAuto;
                break;
            case TEXT:
                m = sViewPart;
                break;
            case AUTO_PART:
                m = sViewPart;
                break;
            case AUTO_BLACK_WHITE:
                m = sViewA2;
                break;
            case AUTO_A2:
                m = sViewA2;
                break;
            default:
                assert(false);
                break;
            }
            
            return m;
        }
        
        private int convertFromUpdateMode(UpdateMode mode)
        {
            int m = sViewNull;
            switch (mode) {
            case GU:
                m = sViewPart;
                break;
            case GU_FAST:
                m = sViewPart;
                break;
            case GC:
                m = sViewFull;
                break;
            case DW:
                m = sViewA2;
                break;
            default:
                assert(false);
                break;
            }

            return m;
        }

        @Override
        public String getEncryptedDeviceID() {
            Class<?> classSystemProperties = null;
            try {
                classSystemProperties = Class.forName(SYSTEM_PROPERTIES_QUILIFIED_NAME);
                Log.i(TAG, "Class: " + SYSTEM_PROPERTIES_QUILIFIED_NAME + " found!");
            } catch (ClassNotFoundException e) {
                Log.w(TAG, "Class: " + SYSTEM_PROPERTIES_QUILIFIED_NAME + " not found!");
                return null;
            }
            Method methodGet = null;
            String methodName = "get";
            try {
                methodGet = classSystemProperties.getMethod(methodName, String.class, String.class);
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "Method: " + methodName + " not found!");
                return null;
            }
            String result = null;
            try {
                result = (String)methodGet.invoke(null, DEVICE_ID, UNKNOWN);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Log.w(TAG, "invoke " + SYSTEM_PROPERTIES_QUILIFIED_NAME + "."+methodName + " exception, illegal argument!");
                return null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Log.w(TAG, "invoke " + SYSTEM_PROPERTIES_QUILIFIED_NAME + "." + methodName + " exception, illegal access!");
                return null;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                Log.w(TAG, "invoke " + SYSTEM_PROPERTIES_QUILIFIED_NAME + "." + methodName + " exception, invocation target exception!");
                return null;
            }
            return result;
        }

        @Override
        public String getPlatform() {
            return Platforms.RK2906;
        }
    }

    @Override
    public String name()
    {
        return "RK2906";
    }

    @Override
    public boolean isPresent()
    {
        return Build.HARDWARE.contains("rk29");
    }

    @Override
    public IDeviceController createController()
    {
        return RK2906Controller.createController();
    }
}
