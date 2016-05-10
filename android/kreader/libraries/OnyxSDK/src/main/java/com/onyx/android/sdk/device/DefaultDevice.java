package com.onyx.android.sdk.device;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Joy on 2016/5/10.
 */
public class DefaultDevice implements OnyxDevice {
    private static final String TAG = "DefaultDevice";
    private final int ICE_CREAM_SANDWICH = 14;
    private final String SHOW_STATUS_BAR_ACTION = "show_status_bar";
    private final String HIDE_STATUS_BAR_ACTION = "hide_status_bar";

    @Override
    public DeviceInfo.DeviceBrand getDeviceBrand() {
        //Log.d(TAG, "Model: " + Build.MODEL + ", MANUFACTURER: " + Build.MANUFACTURER + ", BRAND: " + Build.BRAND);

        if (Build.BRAND.equalsIgnoreCase("Onyx") || Build.BRAND.equalsIgnoreCase("Onyx-Intl")) {
            return DeviceInfo.DeviceBrand.Standard;
        } else if (Build.BRAND.equalsIgnoreCase("Artatech")) {
            return DeviceInfo.DeviceBrand.Artatech;
        } else if (Build.BRAND.equalsIgnoreCase("Artatech-Play")) {
            return DeviceInfo.DeviceBrand.ArtatechPlay;
        } else if (Build.BRAND.equalsIgnoreCase("Tagus")) {
            return DeviceInfo.DeviceBrand.CasaDeLiBro;
        } else if (Build.BRAND.equalsIgnoreCase("MacCentre")) {
            return DeviceInfo.DeviceBrand.MacCentre;
        }

        assert (false);
        String startup = LauncherConfig.getStartupActivityQualifiedName();
        if (startup.equals("com.onyx.android.launcher.LauncherArtatechActivity")) {
            return DeviceInfo.DeviceBrand.Artatech;
        } else if (startup.equals("com.onyx.android.launcher.LauncherMCActivity")) {
            return DeviceInfo.DeviceBrand.MacCentre;
        }

        return DeviceInfo.DeviceBrand.Standard;
    }

    @Override
    public File getStorageRootDirectory() {
        return android.os.Environment.getExternalStorageDirectory();
    }

    @Override
    public File getExternalStorageDirectory() {
        return android.os.Environment.getExternalStorageDirectory();
    }

    @Override
    public File getRemovableSDCardDirectory() {
        File storage_root = getExternalStorageDirectory();

        // if system has an emulated SD card(/mnt/sdcard) provided by device's NAND flash,
        // then real SD card will be mounted as a child directory(/mnt/sdcard/extsd) in it, which names "extsd" here
        final String SDCARD_MOUNTED_FOLDER = "extsd";

        File extsd = new File(storage_root, SDCARD_MOUNTED_FOLDER);
        if (extsd.exists()) {
            return extsd;
        } else {
            return storage_root;
        }
    }

    @Override
    public boolean isFileOnRemovableSDCard(File file) {
        return file.getAbsolutePath().startsWith(getRemovableSDCardDirectory().getAbsolutePath());
    }

    @Override
    public PowerManager.WakeLock newWakeLock(Context context, String tag) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
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
    public DeviceInfo.TouchType getTouchType(Context context) {
        return DeviceInfo.TouchType.IR;
    }

    @Override
    public boolean hasWifi(Context context) {
        return true;
    }

    @Override
    public boolean hasAudio(Context context) {
        return true;
    }

    @Override
    public boolean hasBluetooth(Context context) {
        return true;
    }

    @Override
    public boolean hasFrontLight(Context context) {
        return true;
    }

    @Override
    public boolean has5WayButton(Context context) {
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
    public boolean hasPageButton(Context context) {
        return false;
    }

    @Override
    public int getFrontLightBrightnessMinimum(Context context) {
        return 0;
    }

    @Override
    public int getFrontLightBrightnessMaximum(Context context) {
        return 0;
    }

    public int getFrontLightBrightnessDefault(Context context) {
        return 0;
    }

    @Override
    public boolean openFrontLight(Context context) {
        return false;
    }

    @Override
    public boolean closeFrontLight(Context context) {
        return false;
    }

    public boolean setLedColor(final String ledColor, final int on) {
        return false;
    }

    @Override
    public int getFrontLightDeviceValue(Context context) {
        return 0;
    }

    @Override
    public List<Integer> getFrontLightValueList(Context context) {
        return new ArrayList<Integer>();
    }

    @Override
    public boolean setFrontLightDeviceValue(Context context, int value) {
        return false;
    }

    @Override
    public int getFrontLightConfigValue(Context context) {
        return 0;
    }

    @Override
    public boolean setFrontLightConfigValue(Context context, int value) {
        return false;
    }

    @Override
    public boolean isEInkScreen() {
        return false;
    }

    @Override
    public EpdController.EPDMode getEpdMode() {
        return EpdController.EPDMode.AUTO;
    }

    @Override
    public boolean setEpdMode(Context context, EpdController.EPDMode mode) {
        return false;
    }

    @Override
    public boolean setEpdMode(View view, EpdController.EPDMode mode) {
        return false;
    }

    @Override
    public EpdController.UpdateMode getViewDefaultUpdateMode(View view) {
        return EpdController.UpdateMode.GU;
    }

    @Override
    public boolean setViewDefaultUpdateMode(View view, EpdController.UpdateMode mode) {
        return false;
    }

    @Override
    public void resetViewUpdateMode(View view) {
    }

    @Override
    public EpdController.UpdateMode getSystemDefaultUpdateMode() {
        return EpdController.UpdateMode.GU;
    }

    @Override
    public boolean setSystemDefaultUpdateMode(EpdController.UpdateMode mode) {
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
    public void invalidate(View view, EpdController.UpdateMode mode) {
        view.invalidate();
    }

    @Override
    public boolean enableScreenUpdate(View view, boolean enable) {
        return false;
    }


    @Override
    public void refreshScreen(View view, EpdController.UpdateMode mode) {
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
    public void postInvalidate(View view, EpdController.UpdateMode mode) {
        view.postInvalidate();
    }

    @Override
    public boolean setSystemUpdateModeAndScheme(EpdController.UpdateMode mode, EpdController.UpdateScheme scheme, int count) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean clearSystemUpdateModeAndScheme() {
        // TODO Auto-generated method stub
        return false;
    }

    public void wifiLock(Context context, String className) {
        // TODO Auto-generated method stub

    }

    @Override
    public void wifiUnlock(Context context, String className) {
        // TODO Auto-generated method stub

    }

    @Override
    public void wifiLockClear(Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<String, Integer> getWifiLockMap(Context context) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setWifiLockTimeout(Context context, long ms) {
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
    public void updateMetadataDB(Context context, String path, String target) {
    }

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
