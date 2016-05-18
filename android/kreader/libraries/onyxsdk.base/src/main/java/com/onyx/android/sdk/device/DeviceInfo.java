/**
 * 
 */
package com.onyx.android.sdk.device;

import java.util.ArrayList;

import android.os.Build;
import android.util.Log;

import com.onyx.android.sdk.device.IDeviceFactory.IDeviceController;

/**
 * EPD work differently according to devices 
 * 
 * @author joy
 *
 */
public class DeviceInfo
{
    private final static String TAG = "DeviceInfo";

    public static enum DeviceBrand { Standard, Artatech, ArtatechPlay, CasaDeLiBro, MacCentre }
    
    private static class DefaultFactory implements IDeviceFactory
    {

        @Override
        public String name()
        {
            return "Default Device";
        }

        @Override
        public boolean isPresent()
        {
            return true;
        }

        @Override
        public IDeviceController createController()
        {
            return new IDeviceFactory.DefaultController();
        }
        
    }
    
    private static DeviceInfo sInstance = new DeviceInfo();
    
    private IDeviceFactory mDefaultFactory = new DefaultFactory();
    private ArrayList<IDeviceFactory> mDeviceFactories = new ArrayList<IDeviceFactory>();
    
    private IDeviceFactory mPresentDeviceFactory = new DefaultFactory();
    private IDeviceController mPresentDeviceController = null;

    
    public static final IDeviceController currentDevice;
    
    static {
        currentDevice = sInstance.getDeviceController();
    }
    
    private DeviceInfo()
    {
        this.registerDevice(new RK2818Factory());
        this.registerDevice(new RK2906Factory());
        this.registerDevice(new RK3026Factory());
        this.registerDevice(new IMX508Factory());
        this.registerDevice(new F430Factory());
        this.registerDevice(new IMX6Factory());
    }
    
    /**
     * deprecated, use DeviceInfo.currentDevice instead
     * @return
     */
    @Deprecated
    public static DeviceInfo singleton()
    {
        return sInstance;
    }
    
    public synchronized boolean registerDevice(IDeviceFactory factory)
    {
        if (mPresentDeviceController != null) {
            Log.w(TAG, "Device controller already activated: " + mPresentDeviceFactory.name());
        }
        for (IDeviceFactory f : mDeviceFactories) {
            if (f.name().equals(factory.name())) {
                Log.w(TAG, "Device already registered: " + factory.name());
                return false;
            }
        }
        
        mDeviceFactories.add(factory);
        return true;
    }
    
    /**
     * never return null
     * 
     * @return
     */
    public synchronized IDeviceController getDeviceController()
    {
        if (mPresentDeviceController != null) {
            return mPresentDeviceController;
        }
        
        Log.d(TAG, "device info: " + Build.MANUFACTURER + ", " + Build.MODEL + ", " + Build.DEVICE);
        
        for (IDeviceFactory f : mDeviceFactories) {
            if (f.isPresent()) {
                mPresentDeviceFactory = f;
                break;
            }
        }
        if (mPresentDeviceFactory == null) {
            mPresentDeviceFactory = mDefaultFactory;
        }
        
        mPresentDeviceController = mPresentDeviceFactory.createController();
        if (mPresentDeviceController == null) {
            Log.w(TAG, "create device controller failed: " + mPresentDeviceFactory.name());
            
            mPresentDeviceFactory = mDefaultFactory;
            mPresentDeviceController = mDefaultFactory.createController();
        }

        Log.d(TAG, "present device: " + mPresentDeviceFactory.name());

        assert(mPresentDeviceController != null);
        return mPresentDeviceController;
    }
    
    /**
     * deprecated, use IDeviceController.getDeviceBrand() instead
     * @return
     */
    @Deprecated
    public DeviceBrand getDeviceBrand()
    {
        //Log.d(TAG, "MANUFACTURER: " + Build.MANUFACTURER + "BRAND: " + Build.BRAND);
        if (Build.BRAND.equalsIgnoreCase("Onyx") || Build.BRAND.equalsIgnoreCase("Onyx-Intl") ) {
            return DeviceBrand.Standard;
        } else if (Build.BRAND.equalsIgnoreCase("Artatech")) {
            return DeviceBrand.Artatech;
        } else if (Build.BRAND.equalsIgnoreCase("CasaDeLiBro")) {
            return DeviceBrand.CasaDeLiBro;
        } else if (Build.BRAND.equalsIgnoreCase("MacCentre")) {
            return DeviceBrand.MacCentre;
        }

        assert(false);
        String startup = LauncherConfig.getStartupActivityQualifiedName();
        if (startup.equals("com.onyx.android.launcher.LauncherArtatechActivity")) {
            return DeviceBrand.Artatech;
        }  else if (startup.equals("com.onyx.android.launcher.LauncherMCActivity")) {
            return DeviceBrand.MacCentre;
        }
        
        return DeviceBrand.Standard;
    }
}
