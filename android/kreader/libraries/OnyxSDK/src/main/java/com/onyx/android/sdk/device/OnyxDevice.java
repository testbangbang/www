package com.onyx.android.sdk.device;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.PowerManager;
import android.view.View;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Joy on 2016/5/10.
 */
public interface OnyxDevice {

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
    PowerManager.WakeLock newWakeLock(Context context, String tag);

    // IsSmallScreen doesn't follow name convention, kept for backward compatibility reason
    boolean IsSmallScreen();

    boolean isSmallScreen();

    DeviceInfo.TouchType getTouchType(Context context);

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
