/**
 * 
 */
package com.onyx.android.sdk.device;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author joy
 *
 */
public interface IDeviceFactory
{
    
    public static enum TouchType { None, IR, Capacitive, Unknown }

    public static interface IDeviceController
    {
        DeviceInfo.DeviceBrand getDeviceBrand();
        
        File getStorageRootDirectory();
        File getExternalStorageDirectory();
        File getRemovableSDCardDirectory();
        boolean isFileOnRemovableSDCard(File file);
        
        /**
         * because we've modified power management policy of the device, so normal WakeLock may not working for the device
         * use this WakeLock to prevent device from sleeping/standby
         * 
         * @param context
         * @param tag
         * @return
         */
        WakeLock newWakeLock(Context context, String tag);

        // IsSmallScreen doesn't follow name convention, kept for backward compatibility reason
        boolean IsSmallScreen();
        boolean isSmallScreen();

        TouchType getTouchType(Context context);
        boolean hasWifi(Context context);
        boolean hasAudio(Context context);
        boolean hasBluetooth(Context context);
        boolean hasFrontLight(Context context);
        boolean has5WayButton(Context context);
        boolean hasPageButton(Context context);
        
        void wifiLock(Context context, String className);
        void wifiUnlock(Context context, String className);
        void wifiLockClear(Context context);
        Map<String, Integer> getWifiLockMap(Context context);
        void setWifiLockTimeout(Context context, long ms);
        
        int getFrontLightBrightnessMinimum(Context context);
        int getFrontLightBrightnessMaximum(Context context);
        int getFrontLightBrightnessDefault(Context context);

        boolean openFrontLight(Context context);
        boolean closeFrontLight(Context context);
        boolean setLedColor(final String ledColor, final int on);

        void useBigPen(boolean use);
        void stopTpd();
        void startTpd();
        void enableTpd(boolean enable);

        /**
         * device's brightness of front light, 0 when light is off
         * 
         * @param context
         * @return
         */
        int getFrontLightDeviceValue(Context context);
        /**
         * set device's brightness of front light
         * 
         * @param context
         * @param value
         * @return
         */
        boolean setFrontLightDeviceValue(Context context, int value);

        /**
         * saved value of front light being set by user
         * 
         * @param context
         * @return
         */
        int getFrontLightConfigValue(Context context);
        boolean setFrontLightConfigValue(Context context, int value);
        List<Integer> getFrontLightValueList(Context context);

        boolean isEInkScreen();

        EpdController.EPDMode getEpdMode();
        boolean setEpdMode(Context context, EpdController.EPDMode mode);
        boolean setEpdMode(View view, EpdController.EPDMode mode);
        
        EpdController.UpdateMode getViewDefaultUpdateMode(View view);
        boolean setViewDefaultUpdateMode(View view, EpdController.UpdateMode mode);
        void resetViewUpdateMode(View view);

        EpdController.UpdateMode getSystemDefaultUpdateMode();
        boolean enableScreenUpdate(View view, boolean enable);
        boolean setSystemDefaultUpdateMode(EpdController.UpdateMode mode);
        boolean setSystemUpdateModeAndScheme(EpdController.UpdateMode mode, EpdController.UpdateScheme scheme, int count);
        boolean clearSystemUpdateModeAndScheme();
        boolean applyApplicationFastMode(final String application, boolean enable, boolean clear);
        boolean setDisplayScheme(int scheme);
        void waitForUpdateFinished();

        void invalidate(View view, EpdController.UpdateMode mode);
        void postInvalidate(View view, EpdController.UpdateMode mode);
        void refreshScreen(View view, EpdController.UpdateMode mode);
        void refreshScreenRegion(View view, int left, int top, int width, int height, EpdController.UpdateMode mode);
        void screenshot(View view, int r, final String path);

        public boolean supportDFB();
        public void setStrokeColor(int color);
        public void setStrokeStyle(int style);
        public void setPainterStyle(boolean antiAlias, Paint.Style strokeStyle, Paint.Join joinStyle, Paint.Cap capStyle);

        public void moveTo(float x, float y, float width);
        public void lineTo(float x, float y, EpdController.UpdateMode mode);
        public void quadTo(float x, float y, EpdController.UpdateMode mode);
        public void enterScribbleMode(View view);
        public void leaveScribbleMode(View view);

        public float startStroke(float baseWidth, float x, float y, float pressure, float size, float time);
        public float addStrokePoint(float baseWidth, float x, float y, float pressure, float size, float time);
        public float finishStroke(float baseWidth, float x, float y, float pressure, float size, float time);


        public void enablePost(View view, int enable);
        public void applyGammaCorrection(boolean apply, int value);


        // is the unique id for the device, encrypted
        String getEncryptedDeviceID();

        public void led(Context context, boolean on);

        public void setVCom(Context context, int mv, String path);
        public void updateWaveform(Context context, String path, String target);
        public int getVCom(Context context, String path);
        public String readSystemConfig(Context context, String key);
        public boolean saveSystemConfig(Context context, String key, String mv);
        public Point getWindowWidthAndHeight(Context context);
        public void updateMetadataDB(Context context, String path, String target);
        public void hideSystemStatusBar(Context context);
        public void showSystemStatusBar(Context context);

        public String getPlatform();

        /**
         * Stop the boot animation
         */
        public void stopBootAnimation();
    }

    static class DefaultController implements IDeviceFactory.IDeviceController
    {
        private static final String TAG = "DefaultController";
        private final int ICE_CREAM_SANDWICH = 14;
        private final String SHOW_STATUS_BAR_ACTION = "show_status_bar";
        private final String HIDE_STATUS_BAR_ACTION = "hide_status_bar";

        @Override
        public DeviceInfo.DeviceBrand getDeviceBrand()
        {
            //Log.d(TAG, "Model: " + Build.MODEL + ", MANUFACTURER: " + Build.MANUFACTURER + ", BRAND: " + Build.BRAND);
            
            if (Build.BRAND.equalsIgnoreCase("Onyx") || Build.BRAND.equalsIgnoreCase("Onyx-Intl") ) {
                return DeviceInfo.DeviceBrand.Standard;
            }
            else if (Build.BRAND.equalsIgnoreCase("Artatech")) {
                return DeviceInfo.DeviceBrand.Artatech;
            }
            else if (Build.BRAND.equalsIgnoreCase("Artatech-Play")) {
                return DeviceInfo.DeviceBrand.ArtatechPlay;
            }
            else if (Build.BRAND.equalsIgnoreCase("Tagus")) {
                return DeviceInfo.DeviceBrand.CasaDeLiBro;
            }
            else if (Build.BRAND.equalsIgnoreCase("MacCentre")) {
                return DeviceInfo.DeviceBrand.MacCentre;
            }

            assert(false);
            String startup = LauncherConfig.getStartupActivityQualifiedName();
            if (startup.equals("com.onyx.android.launcher.LauncherArtatechActivity")) {
                return DeviceInfo.DeviceBrand.Artatech;
            }
            else if (startup.equals("com.onyx.android.launcher.LauncherMCActivity")) {
                return DeviceInfo.DeviceBrand.MacCentre;
            }

            return DeviceInfo.DeviceBrand.Standard;
        }
        
        @Override
        public File getStorageRootDirectory()
        {
            return android.os.Environment.getExternalStorageDirectory();
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
            if (extsd.exists()) {
                return extsd;
            }
            else {
                return storage_root;
            }
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
            return pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, tag);
        }

        @Override
        public boolean IsSmallScreen() {
            return isSmallScreen();
        }

        @Override
        public boolean isSmallScreen() {
            return false;
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
            return true;
        }

        @Override
        public boolean hasBluetooth(Context context)
        {
            return true;
        }

        @Override
        public boolean hasFrontLight(Context context)
        {
            return true;
        }
        
        @Override
        public boolean has5WayButton(Context context)
        {
            return false;
        }

        public void useBigPen(boolean use) {
        }

        public void stopTpd() {
        }

        public void startTpd() {
        }

        public void enableTpd(boolean enable) {
        }

        @Override
        public boolean hasPageButton(Context context)
        {
            return false;
        }
        
        @Override
        public int getFrontLightBrightnessMinimum(Context context)
        {
            return 0;
        }
        
        @Override
        public int getFrontLightBrightnessMaximum(Context context)
        {
            return 0;
        }

        public int getFrontLightBrightnessDefault(Context context){
            return 0;
        }
        
        @Override
        public boolean openFrontLight(Context context)
        {
            return false;
        }
        
        @Override
        public boolean closeFrontLight(Context context)
        {
            return false;
        }

        public boolean setLedColor(final String ledColor, final int on) {
            return false;
        }
        
        @Override
        public int getFrontLightDeviceValue(Context context)
        {
            return 0;
        }

        @Override
        public List<Integer> getFrontLightValueList(Context context) {
            return new ArrayList<Integer>();
        }

        @Override
        public boolean setFrontLightDeviceValue(Context context, int value)
        {
            return false;
        }
        
        @Override
        public int getFrontLightConfigValue(Context context)
        {
            return 0;
        }
        
        @Override
        public boolean setFrontLightConfigValue(Context context, int value)
        {
            return false;
        }

        @Override
        public boolean isEInkScreen()
        {
            return false;
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
        public boolean setEpdMode(View view, EpdController.EPDMode mode)
        {
            return false;
        }

        @Override
        public EpdController.UpdateMode getViewDefaultUpdateMode(View view)
        {
            return EpdController.UpdateMode.GU;
        }

        @Override
        public boolean setViewDefaultUpdateMode(View view, EpdController.UpdateMode mode)
        {
            return false;
        }

        @Override
        public void resetViewUpdateMode(View view) {
        }

        @Override
        public EpdController.UpdateMode getSystemDefaultUpdateMode()
        {
            return EpdController.UpdateMode.GU;
        }

        @Override
        public boolean setSystemDefaultUpdateMode(EpdController.UpdateMode mode)
        {
            return false;
        }

        public boolean applyApplicationFastMode(final String application, boolean enable, boolean clear) {
            return false;
        }

        public boolean setDisplayScheme(int scheme) {
            return false;
        }

        public void waitForUpdateFinished() {
        }

        @Override
        public void invalidate(View view, EpdController.UpdateMode mode)
        {
            view.invalidate();
        }

        @Override
        public boolean enableScreenUpdate(View view, boolean enable) {
            return false;
        }


        @Override
        public void refreshScreen(View view, EpdController.UpdateMode mode)
        {
        }

        @Override
        public void refreshScreenRegion(View view, int left, int top, int width, int height, EpdController.UpdateMode mode) {
        }

        public void screenshot(View view, int r, final String path) {
        }

        public boolean supportDFB() {
            return false;
        }

        @Override
        public void setStrokeColor(int color) {
        }

        public void setStrokeStyle(int style) {
        }

        public void setPainterStyle(boolean antiAlias, Paint.Style strokeStyle, Paint.Join joinStyle, Paint.Cap capStyle) {
        }

        @Override
        public void moveTo(float x, float y, float width) {
        }

        @Override
        public void lineTo(float x, float y, EpdController.UpdateMode mode) {
        }

        @Override
        public void quadTo(float x, float y, EpdController.UpdateMode mode) {
        }

        public float startStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
            return baseWidth;
        }
        public float addStrokePoint(float baseWidth, float x, float y, float pressure, float size, float time) {
            return baseWidth;
        }

        public float finishStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
            return baseWidth;
        }

        public void enterScribbleMode(View view) {
        }
        public void leaveScribbleMode(View view) {
        }

        @Override
        public void enablePost(View view, int enable) {
        }

        public void applyGammaCorrection(boolean apply, int value) {
        }

        @Override
        public void postInvalidate(View view, EpdController.UpdateMode mode)
        {
            view.postInvalidate();
        }

        @Override
        public boolean setSystemUpdateModeAndScheme(EpdController.UpdateMode mode, EpdController.UpdateScheme scheme, int count)
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

        public void wifiLock(Context context, String className)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void wifiUnlock(Context context, String className)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void wifiLockClear(Context context)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public Map<String, Integer> getWifiLockMap(Context context)
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setWifiLockTimeout(Context context, long ms)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public String getEncryptedDeviceID() {
            return null;
        }

        @Override
        public void led(Context context, boolean on) {

        }

        @Override
        public void setVCom(Context context, int mv, String path) {

        }

        @Override
        public void updateWaveform(Context context, String path, String target) {

        }

        @Override
        public int getVCom(Context context, String path) {
			return 0;
        }

        @Override
        public String readSystemConfig(Context context, String key) {
            return "";
        }

        @Override
        public boolean saveSystemConfig(Context context, String key, String mv) {
            return false;
        }

        @Override
        public void updateMetadataDB(Context context, String path, String target) {}

        @Override
		public Point getWindowWidthAndHeight(Context context) {
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Point point = new Point();
			point.x = wm.getDefaultDisplay().getWidth();
			point.y = wm.getDefaultDisplay().getHeight(); 
			return point;
		}

        @Override
        public void hideSystemStatusBar(Context context) {
            showOrHideSystemStatusBar(context, HIDE_STATUS_BAR_ACTION);
        }

        @Override
        public void showSystemStatusBar(Context context) {
            showOrHideSystemStatusBar(context, SHOW_STATUS_BAR_ACTION);
        }

        private void showOrHideSystemStatusBar(Context context, String action) {
            if (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH) {
                Intent intent = new Intent(action);
                context.sendBroadcast(intent);
            }
        }

        @Override
        public String getPlatform() {
            return "";
        }

        @Override
        public void stopBootAnimation() {
        }
    }

    String name();
    boolean isPresent();

    IDeviceController createController();
}
