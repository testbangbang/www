/**
 * 
 */
package com.onyx.android.sdk.device;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.onyx.android.sdk.device.EpdController.EPDMode;
import com.onyx.android.sdk.device.EpdController.UpdateMode;
import com.onyx.android.sdk.device.EpdController.UpdateScheme;

/**
 * @author joy
 *
 */
public class RK2818Factory implements IDeviceFactory
{
    private final static String TAG = "RK2818Controller";
    
    public static class RK2818Controller extends IDeviceFactory.DefaultController
    {
        private final static int DEFAULT_MODE = 0; // which standing for A2 according to RK2818 specification
        
        private static RK2818Controller sInstance = null;
        
        @SuppressWarnings("rawtypes")
        private static Class sClassEpdManager = null;
        @SuppressWarnings("rawtypes")
        private static Constructor sConstructor = null;
        
        private static Method sMethodSetMode = null;
        
        private static int sModeFull = DEFAULT_MODE;
        private static int sModeAuto = DEFAULT_MODE;
        private static int sModeText = DEFAULT_MODE;
        private static int sModeAutoPart = DEFAULT_MODE;
        private static int sModeAutoBlackWhite = DEFAULT_MODE;
        
        private Context mContext = null;
        private Object mEpdManagerInstance = null;
        
        private RK2818Controller()
        {
        }
        
        @SuppressWarnings("unchecked")
        public static RK2818Controller createController()
        {
            if (sInstance == null) {
                try {
                    sClassEpdManager = Class.forName("android.hardware.EpdManager");
                    sConstructor = sClassEpdManager.getConstructor(Context.class);
                    sMethodSetMode = sClassEpdManager.getMethod("setMode", int.class);
                    sModeFull = sClassEpdManager.getField("FULL").getInt(null);
                    sModeAuto = sClassEpdManager.getField("AUTO").getInt(null);
                    sModeText = sClassEpdManager.getField("TEXT").getInt(null);
                    sModeAutoPart = sClassEpdManager.getField("AUTO_PART").getInt(null);
                    sModeAutoBlackWhite = sClassEpdManager.getField("AUTO_BLACK_WHITE").getInt(null);
                    
                    sInstance = new RK2818Controller();
                    return sInstance;
                }
                catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (NoSuchMethodException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (NoSuchFieldException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            return null;
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
        public EPDMode getEpdMode()
        {
            // TODO Auto-generated method stub
            return null;
        }
        
        @Override
        public boolean setEpdMode(Context context, EpdController.EPDMode mode)
        {
            int m = sModeText;
            if (mode == EPDMode.FULL) {
                m = sModeFull;
            }
            else if (mode == EPDMode.AUTO) {
                m = sModeAuto;
            }
            else if (mode == EPDMode.TEXT) {
                m = sModeText;
            }
            else if (mode == EPDMode.AUTO_PART) {
                m = sModeAutoPart;
            }
            else if (mode == EPDMode.AUTO_BLACK_WHITE) {
                m = sModeAutoBlackWhite;
            }
            
            try {
                if (mContext != context) {
                    mContext = context;
                    mEpdManagerInstance = sConstructor.newInstance(context);
                }
                
                Boolean res = (Boolean)sMethodSetMode.invoke(mEpdManagerInstance, m);
                return res.booleanValue();
            }
            catch (InstantiationException e) {
                Log.w(TAG, e);
            }
            catch (IllegalArgumentException e) {
                Log.w(TAG, e);
            }
            catch (IllegalAccessException e) {
                Log.w(TAG, e);
            }
            catch (InvocationTargetException e) {
                Log.w(TAG, e);
            }
            
            return false;
        }

        @Override
        public boolean setEpdMode(View view, EPDMode mode)
        {
            return this.setEpdMode(view.getContext(), mode);
        }

        @Override
        public void invalidate(View view, UpdateMode mode)
        {
            if (mode == UpdateMode.GC) {
                this.setEpdMode(view.getContext(), EPDMode.FULL);
            }
            view.invalidate();
        }

        @Override
        public void postInvalidate(View view, UpdateMode mode)
        {
            if (mode == UpdateMode.GC) {
                this.setEpdMode(view.getContext(), EPDMode.FULL);
            }
            view.postInvalidate();
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
            return Platforms.RK2818;
        }
    }

    @Override
    public String name()
    {
        return "RK2818";
    }

    @Override
    public boolean isPresent()
    {
        return Build.MANUFACTURER.contentEquals("unknown") &&
                Build.MODEL.contentEquals("ebook") &&
                Build.DEVICE.contentEquals("ebook");
    }

    @Override
    public IDeviceController createController()
    {
        return RK2818Controller.createController();
    }
}
