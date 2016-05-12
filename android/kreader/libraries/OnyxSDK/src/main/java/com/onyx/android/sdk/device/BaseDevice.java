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
public class BaseDevice {
    private static final String TAG = "BaseDevice";
    private final int ICE_CREAM_SANDWICH = 14;
    private final String SHOW_STATUS_BAR_ACTION = "show_status_bar";
    private final String HIDE_STATUS_BAR_ACTION = "hide_status_bar";

    public File getStorageRootDirectory() {
        return android.os.Environment.getExternalStorageDirectory();
    }

    public File getExternalStorageDirectory() {
        return android.os.Environment.getExternalStorageDirectory();
    }

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

    public boolean isFileOnRemovableSDCard(File file) {
        return file.getAbsolutePath().startsWith(getRemovableSDCardDirectory().getAbsolutePath());
    }

    public PowerManager.WakeLock newWakeLock(Context context, String tag) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, tag);
    }

    public void useBigPen(boolean use) {
    }

    public void stopTpd() {
    }

    public void startTpd() {
    }

    public void enableTpd(boolean enable) {
    }

    public int getFrontLightBrightnessMinimum(Context context) {
        return 0;
    }

    public int getFrontLightBrightnessMaximum(Context context) {
        return 0;
    }

    public int getFrontLightBrightnessDefault(Context context) {
        return 0;
    }

    public boolean openFrontLight(Context context) {
        return false;
    }

    public boolean closeFrontLight(Context context) {
        return false;
    }

    public boolean setLedColor(final String ledColor, final int on) {
        return false;
    }

    public int getFrontLightDeviceValue(Context context) {
        return 0;
    }

    public List<Integer> getFrontLightValueList(Context context) {
        return new ArrayList<Integer>();
    }

    public boolean setFrontLightDeviceValue(Context context, int value) {
        return false;
    }

    public int getFrontLightConfigValue(Context context) {
        return 0;
    }

    public boolean setFrontLightConfigValue(Context context, int value) {
        return false;
    }

    public EpdController.EPDMode getEpdMode() {
        return EpdController.EPDMode.AUTO;
    }

    public boolean setEpdMode(Context context, EpdController.EPDMode mode) {
        return false;
    }

    public boolean setEpdMode(View view, EpdController.EPDMode mode) {
        return false;
    }

    public EpdController.UpdateMode getViewDefaultUpdateMode(View view) {
        return EpdController.UpdateMode.GU;
    }

    public boolean setViewDefaultUpdateMode(View view, EpdController.UpdateMode mode) {
        return false;
    }

    public void resetViewUpdateMode(View view) {
    }

    public EpdController.UpdateMode getSystemDefaultUpdateMode() {
        return EpdController.UpdateMode.GU;
    }

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

    public void invalidate(View view, EpdController.UpdateMode mode) {
        view.invalidate();
    }

    public boolean enableScreenUpdate(View view, boolean enable) {
        return false;
    }


    public void refreshScreen(View view, EpdController.UpdateMode mode) {
    }

    public void refreshScreenRegion(View view, int left, int top, int width, int height, EpdController.UpdateMode mode) {
    }

    public void screenshot(View view, int r, final String path) {
    }

    public boolean supportDFB() {
        return false;
    }

    public void setStrokeColor(int color) {
    }

    public void setStrokeStyle(int style) {
    }

    public void setPainterStyle(boolean antiAlias, Paint.Style strokeStyle, Paint.Join joinStyle, Paint.Cap capStyle) {
    }

    public void moveTo(float x, float y, float width) {
    }

    public void lineTo(float x, float y, EpdController.UpdateMode mode) {
    }

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

    public void enablePost(View view, int enable) {
    }

    public void applyGammaCorrection(boolean apply, int value) {
    }

    public void postInvalidate(View view, EpdController.UpdateMode mode) {
        view.postInvalidate();
    }

    public boolean setSystemUpdateModeAndScheme(EpdController.UpdateMode mode, EpdController.UpdateScheme scheme, int count) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean clearSystemUpdateModeAndScheme() {
        // TODO Auto-generated method stub
        return false;
    }

    public void wifiLock(Context context, String className) {
        // TODO Auto-generated method stub

    }

    public void wifiUnlock(Context context, String className) {
        // TODO Auto-generated method stub

    }

    public void wifiLockClear(Context context) {
        // TODO Auto-generated method stub

    }

    public Map<String, Integer> getWifiLockMap(Context context) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setWifiLockTimeout(Context context, long ms) {
        // TODO Auto-generated method stub

    }

    public String getEncryptedDeviceID() {
        return null;
    }

    public void led(Context context, boolean on) {

    }

    public void setVCom(Context context, int mv, String path) {

    }

    public void updateWaveform(Context context, String path, String target) {

    }

    public int getVCom(Context context, String path) {
        return 0;
    }

    public String readSystemConfig(Context context, String key) {
        return "";
    }

    public boolean saveSystemConfig(Context context, String key, String mv) {
        return false;
    }

    public void updateMetadataDB(Context context, String path, String target) {
    }

    public Point getWindowWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        point.x = wm.getDefaultDisplay().getWidth();
        point.y = wm.getDefaultDisplay().getHeight();
        return point;
    }

    public void hideSystemStatusBar(Context context) {
        showOrHideSystemStatusBar(context, HIDE_STATUS_BAR_ACTION);
    }

    public void showSystemStatusBar(Context context) {
        showOrHideSystemStatusBar(context, SHOW_STATUS_BAR_ACTION);
    }

    private void showOrHideSystemStatusBar(Context context, String action) {
        if (Build.VERSION.SDK_INT >= ICE_CREAM_SANDWICH) {
            Intent intent = new Intent(action);
            context.sendBroadcast(intent);
        }
    }

    public void stopBootAnimation() {
    }
}
