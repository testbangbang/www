/**
 * 
 */
package com.onyx.android.sdk.device;

import com.onyx.android.sdk.device.EpdController.UpdateMode;
import com.onyx.android.sdk.device.EpdController.UpdateScheme;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.onyx.android.sdk.device.EpdController.EPDMode;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.View;

/**
 * singleton class
 * 
 * @author joy
 *
 */
public final class IMX508Factory  implements IDeviceFactory
{
    private static String TAG = "IMX508Factory";
    
    public static class IMX508Controller extends IDeviceFactory.DefaultController
    {
        private static IMX508Controller sInstance = null;

        private static int sPolicyAutomatic = 0;
        private static int sPolicyGUIntervally = 0;

        private static int sModeDW = 0;
        private static int sModeGU = 0;
        private static int sModeGC = 0;

        /**
         * static int View.getWindowRotation()
         */
        private static Method sMethodGetWindowRotation = null;
        /**
         * static void View.setWindowRotation(int rotation, boolean alwaysSendConfiguration, int animFlags)
         */
        private static Method sMethodSetWindowRotation = null;
        /**
         * View.setUpdatePolicy(int updatePolicy, int guInterval)
         */
        private static Method sMethodSetUpdatePolicy = null;
        /**
         * View.postInvalidate(int updateMode)
         */
        private static Method sMethodPostInvalidate = null;
        /**
         * View.invalidate(int updateMode)
         */
        private static Method sMethodInvalidate = null;
        
        private IMX508Controller()
        {
        }

        public int getWindowRotation()
        {
            if (sMethodGetWindowRotation != null) {
                try {
                    return (Integer)sMethodGetWindowRotation.invoke(null);
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, e);
                } catch (IllegalAccessException e) {
                    Log.w(TAG, e);
                } catch (InvocationTargetException e) {
                    Log.w(TAG, e);
                }
            }

            return Surface.ROTATION_0;
        }

        public boolean setWindowRotation(int rotation)
        {
            if (sMethodSetWindowRotation != null) {
                try {
                    final int Surface_FLAGS_ORIENTATION_ANIMATION_DISABLE = 0x000000001;
                    sMethodSetWindowRotation.invoke(null, rotation, true, Surface_FLAGS_ORIENTATION_ANIMATION_DISABLE);
                    return true;
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, e);
                } catch (IllegalAccessException e) {
                    Log.w(TAG, e);
                } catch (InvocationTargetException e) {
                    Log.w(TAG, e);
                }
            }

            return false;
        }

        public boolean setUpdatePolicy(View view, EpdController.UpdatePolicy policy, int guInterval)
        {
            int dst_mode_value = getPolicyValue(policy);

            try {
                assert(sMethodPostInvalidate != null);
                Log.d(TAG, "dst mode: " + dst_mode_value);
                sMethodSetUpdatePolicy.invoke(view, dst_mode_value, guInterval);
                return true;
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }

            return false;
        }
        
        @Override
        public File getExternalStorageDirectory()
        {
            return android.os.Environment.getExternalStorageDirectory();
        }

        @Override
        public File getRemovableSDCardDirectory()
        {
            File storage_root = getExternalStorageDirectory();

            // if system has an emulated SD card(/mnt/sdcard) provided by device's NAND flash, 
            // then real SD card will be mounted as a child directory(/mnt/sdcard/extsd) in it, which names "extsd" here
            final String SDCARD_MOUNTED_FOLDER = "extsd";
            File extsd = new File(storage_root, SDCARD_MOUNTED_FOLDER);
            return extsd;
        }

        @Override
        public boolean isFileOnRemovableSDCard(File file)
        {
            return file.getAbsolutePath().startsWith(getRemovableSDCardDirectory().getAbsolutePath());
        }
        
        @Override
        public TouchType getTouchType(Context context)
        {
            return TouchType.IR;
        }
        
        @Override
        public boolean hasWifi(Context context)
        {
            return true;
        }
        
        @Override
        public boolean hasAudio(Context context)
        {
            // TODO Auto-generated method stub
            return false;
        }
        
        @Override
        public boolean hasFrontLight(Context context)
        {
            // TODO Auto-generated method stub
            return false;
        }
        
        @Override
        public boolean isEInkScreen()
        {
            return true;
        }
        
        @Override
        public EpdController.EPDMode getEpdMode()
        {
            return EpdController.EPDMode.AUTO;
        }
        
        @Override
        public boolean setEpdMode(Context context, EpdController.EPDMode mode)
        {
            return false;
        }

        @Override
        public boolean setEpdMode(View view, EPDMode mode)
        {
            return false;
        }

        @Override
        public void invalidate(View view, EpdController.UpdateMode mode)
        {
            int dst_mode_value = getUpdateMode(mode);

            try {
                assert(sMethodInvalidate != null);
                Log.d(TAG, "dst mode: " + dst_mode_value);
                sMethodInvalidate.invoke(view, dst_mode_value);
                return;
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }

            view.invalidate();
        }

        @Override
        public void postInvalidate(View view, EpdController.UpdateMode mode)
        {
            int dst_mode_value = getUpdateMode(mode);

            try {
                assert(sMethodPostInvalidate != null);
                Log.d(TAG, "dst mode: " + dst_mode_value);
                sMethodPostInvalidate.invoke(view, dst_mode_value);
                return;
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }

            view.postInvalidate();
        }

        public static IMX508Controller createController()
        {
            if (sInstance == null) {
                Class<View> cls = View.class;

                try {
                    // signature of "public static int getWindowRotation()"
                    sMethodGetWindowRotation = cls.getMethod("getWindowRotation");
                    // signature of "public static void setWindowRotation(int rotation, boolean alwaysSendConfiguration, int animFlags)"
                    sMethodSetWindowRotation = cls.getMethod("setWindowRotation", int.class, boolean.class, int.class);
                } catch (SecurityException e1) {
                    Log.w(TAG, e1);
                } catch (NoSuchMethodException e1) {
                    Log.w(TAG, e1);
                }

                try {
                    Field fld_policy_automic = cls.getField("EINK_ONYX_AUTO_MASK");
                    int value_policy_automic = fld_policy_automic.getInt(null);
                    Field fld_policy_gu_intervally = cls.getField("EINK_ONYX_GC_MASK");
                    int value_policy_gu_intervally = fld_policy_gu_intervally.getInt(null);

                    Field fld_mode_regional = cls.getField("EINK_AUTO_MODE_REGIONAL");
                    int value_mode_regional = fld_mode_regional.getInt(null);
                    Field fld_mode_nowait = cls.getField("EINK_WAIT_MODE_NOWAIT");
                    int value_mode_nowait = fld_mode_nowait.getInt(null);
                    Field fld_mode_wait = cls.getField("EINK_WAIT_MODE_WAIT");
                    int value_mode_wait = fld_mode_wait.getInt(null);
                    Field fld_mode_waveform_du = cls.getField("EINK_WAVEFORM_MODE_DU");
                    int value_mode_waveform_du = fld_mode_waveform_du.getInt(null);
                    Field fld_mode_waveform_gc16 = cls.getField("EINK_WAVEFORM_MODE_GC16");
                    int value_mode_waveform_gc16 = fld_mode_waveform_gc16.getInt(null);
                    Field fld_mode_update_partial = cls.getField("EINK_UPDATE_MODE_PARTIAL");
                    int value_mode_update_partial = fld_mode_update_partial.getInt(null);
                    Field fld_mode_update_full = cls.getField("EINK_UPDATE_MODE_FULL");
                    int value_mode_update_full = fld_mode_update_full.getInt(null);

                    sPolicyAutomatic = value_policy_automic;
                    sPolicyGUIntervally = value_policy_gu_intervally;

                    sModeDW = value_mode_regional | value_mode_nowait | value_mode_waveform_du | value_mode_update_partial;
                    sModeGU = value_mode_regional | value_mode_nowait | value_mode_waveform_gc16 | value_mode_update_partial;
                    sModeGC = value_mode_regional | value_mode_wait | value_mode_waveform_gc16 | value_mode_update_full;

                    // signature of "public void setUpdatePolicy(int updatePolicy, int guInterval)"
                    sMethodSetUpdatePolicy = cls.getMethod("setUpdatePolicy", int.class, int.class);
                    // signature of "public void postInvalidate(int updateMode)"
                    sMethodPostInvalidate = cls.getMethod("postInvalidate", int.class);
                    // signature of "public void invalidate(int updateMode)"
                    sMethodInvalidate = cls.getMethod("invalidate", int.class);

                    Log.d(TAG, "init device ok.");

                    sInstance = new IMX508Controller();
                    return sInstance;
                } catch (SecurityException e) {
                    Log.w(TAG, e);
                } catch (NoSuchFieldException e) {
                    Log.w(TAG, e);
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, e);
                } catch (IllegalAccessException e) {
                    Log.w(TAG, e);
                } catch (NoSuchMethodException e) {
                    Log.w(TAG, e);
                }

                Log.d(TAG, "init device failed.");

                return null;
            }
            
            return sInstance;
        }

        private int getUpdateMode(EpdController.UpdateMode mode)
        {
            // default use GC update mode
            int dst_mode = sModeGC;

            switch (mode) {
            case DW:
                dst_mode = sModeDW;
                break;
            case GU:
                dst_mode = sModeGU;
                break;
            case GC:
                dst_mode = sModeGC;
                break;
            default:
                assert(false);
                break;
            }

            return dst_mode;
        }

        private static int getPolicyValue(EpdController.UpdatePolicy policy)
        {
            int dst_value = sModeGU;
            switch (policy) {
            case Automatic:
                dst_value |= sPolicyAutomatic; 
                break;
            case GUIntervally:
                dst_value |= sPolicyGUIntervally;
                break;
            default:
                assert(false);
                break;
            }

            return dst_value;
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

        @Override
        public String getPlatform() {
            return Platforms.IMX508;
        }
    }

    @Override
    public String name()
    {
        return "IMX508";
    }

    @Override
    public boolean isPresent()
    {
        return Build.MANUFACTURER.contentEquals("unknown") &&
                Build.MODEL.contentEquals("imx50_rdp") &&
                Build.DEVICE.contentEquals("imx50_rdp");
    }

    @Override
    public IDeviceController createController()
    {
        return IMX508Controller.createController();
    }
}
