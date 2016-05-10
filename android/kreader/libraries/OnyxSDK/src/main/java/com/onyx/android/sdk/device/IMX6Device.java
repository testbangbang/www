package com.onyx.android.sdk.device;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import com.onyx.android.sdk.utils.ReflectUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Joy on 2016/5/10.
 */
public class IMX6Device extends BaseDevice {

    private static final String TAG = IMX6Device.class.getSimpleName();

    private static IMX6Device sInstance = null;

    private static int sPolicyAutomatic = 0;
    private static int sPolicyGUIntervally = 0;

    private static int sModeDW = 0;
    private static int sModeGU = 0;
    private static int sModeGC = 0;
    private static int sModeAnimation = 0;
    private static int sModeAnimationQuality = 0;
    private static int sModeGC4 = 0;

    private static final int sSchemeSNAPSHOT = 0;
    private static final int sSchemeQUEUE = 1;
    private static final int sSchemeQUEUE_AND_MERGE = 2;


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
     * View.refreshScreen(int updateMode)
     */
    private static Method sMethodRefreshScreen = null;
    private static Method sMethodRefreshScreenRegion = null;
    private static Method sMethodScreenshot = null;

    private static Method sMethodMoveTo = null;
    private static Method sMethodSetStrokeColor = null;
    private static Method sMethodSetStrokeStyle = null;
    private static Method sMethodSetPainterStyle = null;
    private static Method sMethodLineTo = null;
    private static Method sMethodQuadTo = null;
    private static Method sMethodEnablePost = null;
    private static Method sMethodApplyGammaCorrection = null;
    private static Method sMethodStartStroke = null;
    private static Method sMethodAddStrokePoint = null;
    private static Method sMethodFinishStroke = null;

    /**
     * View.postInvalidate(int updateMode)
     */
    private static Method sMethodPostInvalidate = null;
    /**
     * View.invalidate(int updateMode)
     */
    private static Method sMethodInvalidate = null;


    /**
     * View.getDefaultUpdateMode()
     */
    private static Method sMethodGetViewDefaultUpdateMode = null;

    /**
     * View.resetUpdateMode()
     */
    private static Method sMethodResetViewUpdateMode = null;

    /**
     * View.setDefaultUpdateMode(int updateMode)
     */
    private static Method sMethodSetViewDefaultUpdateMode = null;

    /**
     * View.getGlobalUpdateMode()
     */
    private static Method sMethodGetSystemDefaultUpdateMode = null;

    /**
     * View.setGlobalUpdateMode(int updateMode)
     */
    private static Method sMethodSetSystemDefaultUpdateMode = null;

    /**
     * View.setFirstDrawUpdateMode(int updateMode)
     */
    private static Method sMethodSetFirstDrawUpdateMode = null;

    /**
     * View.setWaveformAndScheme(int mode, int scheme)
     */
    private static Method sMethodSetSystemUpdateModeAndScheme = null;

    private static Method sMethodApplyApplicationFastMode = null;

    /**
     * View.resetWaveformAndScheme()
     */
    private static Method sMethodClearSystemUpdateModeAndScheme = null;
    private static Method sMethodEnableScreenUpdate = null;
    private static Method sMethodSetDisplayScheme = null;
    private static Method sMethodWaitForUpdateFinished = null;


    private static Method sMethodIsTouchable;
    private static Method sMethodGetTouchType;
    private static Method sMethodHasWifi;
    private static Method sMethodHasAudio;
    private static Method sMethodHasFrontLight;

    private static Method sMethodOpenFrontLight;
    private static Method sMethodCloseFrontLight;
    private static Method sMethodGetFrontLightValue;
    private static Method sMethodSetFrontLightValue;
    private static Method sMethodGetFrontLightConfigValue;
    private static Method sMethodSetFrontLightConfigValue;
    private static Method sMethodLed;
    private static Method sMethodSetLedColor;
    private static Method sMethodSetVCom;
    private static Method sMethodGetVCom;
    private static Method sMethodSetWaveform;
    private static Method sMethodReadSystemConfig;
    private static Method sMethodSaveSystemConfig;
    private static Method sMethodUpdateMetadataDB;

    private static int sTouchTypeUnknown = 0;
    private static int sTouchTypeIR = 1;
    private static int sTouchTypeCapacitive = 2;

    private static Method sMethodHasBluetooth;
    private static Method sMethodHas5WayButton;
    private static Method sMethodHasPageButton;
    private static Method sMethodUseBigPen;
    private static Method sMethodStopTpd;
    private static Method sMethodStartTpd;
    private static Method sMethodEnableTpd;

    public IMX6Device() {
    }

    public int getWindowRotation() {
        if (sMethodGetWindowRotation != null) {
            try {
                return (Integer) sMethodGetWindowRotation.invoke(null);
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

    public boolean setWindowRotation(int rotation) {
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

    public boolean setUpdatePolicy(View view, EpdController.UpdatePolicy policy, int guInterval) {
        int dst_mode_value = getPolicyValue(policy);

        try {
            assert (sMethodSetUpdatePolicy != null);
            sMethodSetUpdatePolicy.invoke(view, dst_mode_value, guInterval);
            return true;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        return false;
    }

    @Override
    public File getStorageRootDirectory() {
        return new File("/mnt");
    }

    @Override
    public File getExternalStorageDirectory() {
        // TODO or Environment.getExternalStorageDirectory()?
        return new File("/mnt/sdcard");
    }

    @Override
    public File getRemovableSDCardDirectory() {
        // if system has an emulated SD card(/mnt/sdcard) provided by device's NAND flash,
        // then real SD card will be mounted as a child directory(/mnt/sdcard/extsd) in it, which names "extsd" here
        //final String SDCARD_MOUNTED_FOLDER = "extsd";
        return new File("/mnt/extsd");
    }

    @Override
    public boolean isFileOnRemovableSDCard(File file) {
        return file.getAbsolutePath().startsWith(getRemovableSDCardDirectory().getAbsolutePath());
    }


    @Override
    public boolean isEInkScreen() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public EpdController.EPDMode getEpdMode() {
        return EpdController.EPDMode.AUTO;
    }

    @Override
    public boolean setEpdMode(Context context, EpdController.EPDMode mode) {
        setSystemUpdateModeAndScheme(getEpdMode(mode), EpdController.UpdateScheme.QUEUE_AND_MERGE, Integer.MAX_VALUE);
        return false;
    }

    @Override
    public void invalidate(View view, EpdController.UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);

        try {
            assert (sMethodInvalidate != null);
            sMethodInvalidate.invoke(view, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        view.invalidate();
    }


    @Override
    public void postInvalidate(View view, EpdController.UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);

        try {
            assert (sMethodPostInvalidate != null);
            Log.d(TAG, "dst mode: " + dst_mode_value);
            sMethodPostInvalidate.invoke(view, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }

        view.postInvalidate();
    }

    @Override
    public void refreshScreen(View view, EpdController.UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);
        try {
            assert (sMethodRefreshScreen != null);
            sMethodRefreshScreen.invoke(view, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    public void refreshScreenRegion(View view, int left, int top, int width, int height, EpdController.UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);
        try {
            assert (sMethodRefreshScreenRegion != null);
            sMethodRefreshScreenRegion.invoke(view, left, top, width, height, dst_mode_value);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void screenshot(View view, int rotation, final String path) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodScreenshot, view, rotation, path);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setStrokeColor(int color) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetStrokeColor, null, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStrokeStyle(int style) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetStrokeStyle, null, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPainterStyle(boolean antiAlias, Paint.Style strokeStyle, Paint.Join joinStyle, Paint.Cap capStyle) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetPainterStyle, null, antiAlias, strokeStyle, joinStyle, capStyle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveTo(float x, float y, float width) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMoveTo, null, x, y, width);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean supportDFB() {
        return (sMethodLineTo != null);
    }

    public void lineTo(float x, float y, EpdController.UpdateMode mode) {
        int value = getUpdateMode(mode);
        try {
            ReflectUtil.invokeMethodSafely(sMethodLineTo, null, x, y, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quadTo(float x, float y, EpdController.UpdateMode mode) {
        int value = getUpdateMode(mode);
        try {
            ReflectUtil.invokeMethodSafely(sMethodQuadTo, null, x, y, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float startStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        try {
            Float value = (Float) ReflectUtil.invokeMethodSafely(sMethodStartStroke, null, baseWidth, x, y, pressure, size, time);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseWidth;
    }

    public float addStrokePoint(float baseWidth, float x, float y, float pressure, float size, float time) {
        try {
            Float value = (Float) ReflectUtil.invokeMethodSafely(sMethodAddStrokePoint, null, baseWidth, x, y, pressure, size, time);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseWidth;
    }

    public float finishStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        try {
            Float value = (Float) ReflectUtil.invokeMethodSafely(sMethodFinishStroke, null, baseWidth, x, y, pressure, size, time);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseWidth;
    }

    public void enterScribbleMode(View view) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodEnablePost, view, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leaveScribbleMode(View view) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodEnablePost, view, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enablePost(View view, int enable) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodEnablePost, view, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyGammaCorrection(boolean apply, int value) {
        ReflectUtil.invokeMethodSafely(sMethodApplyGammaCorrection, null, apply, value);
    }

    @Override
    public boolean enableScreenUpdate(View view, boolean enable) {
        try {
            sMethodEnableScreenUpdate.invoke(view, enable);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return true;
    }

    public boolean setDisplayScheme(int scheme) {
        ReflectUtil.invokeMethodSafely(sMethodSetDisplayScheme, null, scheme);
        return true;
    }

    public void waitForUpdateFinished() {
        ReflectUtil.invokeMethodSafely(sMethodWaitForUpdateFinished, null);
    }

    public static IMX6Device createController() {
        if (sInstance == null) {
            sInstance = new IMX6Device();

            Class<View> cls = View.class;

            // signature of "public static int getWindowRotation()"
            sMethodGetWindowRotation = ReflectUtil.getMethodSafely(cls, "getWindowRotation");
            // signature of "public static void setWindowRotation(int rotation, boolean alwaysSendConfiguration, int animFlags)"
            sMethodSetWindowRotation = ReflectUtil.getMethodSafely(cls, "setWindowRotation", int.class, boolean.class, int.class);

            int value_policy_automic = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_ONYX_AUTO_MASK");
            int value_policy_gu_intervally = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_ONYX_GC_MASK");
            int value_mode_regional = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_AUTO_MODE_REGIONAL");
            int value_mode_nowait = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAIT_MODE_NOWAIT");
            int value_mode_wait = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAIT_MODE_WAIT");

            int value_mode_waveform_du = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAVEFORM_MODE_DU");
            int value_mode_waveform_animation = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAVEFORM_MODE_ANIM");
            int value_mode_waveform_gc4 = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAVEFORM_MODE_GC4");
            int value_mode_waveform_gc16 = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_WAVEFORM_MODE_GC16");
            int value_mode_update_partial = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_UPDATE_MODE_PARTIAL");
            int value_mode_update_full = ReflectUtil.getStaticIntFieldSafely(cls, "EINK_UPDATE_MODE_FULL");

            sPolicyAutomatic = value_policy_automic;
            sPolicyGUIntervally = value_policy_gu_intervally;

            sModeDW = value_mode_regional | value_mode_nowait | value_mode_waveform_du | value_mode_update_partial;
            sModeGU = value_mode_regional | value_mode_nowait | value_mode_waveform_gc16 | value_mode_update_partial;
            sModeGC = value_mode_regional | value_mode_wait | value_mode_waveform_gc16 | value_mode_update_full;
            sModeAnimation = value_mode_regional | value_mode_nowait | value_mode_waveform_animation | value_mode_update_partial;
            sModeAnimationQuality = ReflectUtil.getStaticIntFieldSafely(cls, "UI_A2_QUALITY_MODE");
            sModeGC4 = value_mode_regional | value_mode_nowait | value_mode_waveform_gc4 | value_mode_update_partial;

            Class<?> deviceControllerClass = ReflectUtil.classForName("android.hardware.DeviceController");

            // new added methods, separating for compatibility
            sMethodOpenFrontLight = ReflectUtil.getMethodSafely(deviceControllerClass, "openFrontLight", Context.class);
            sMethodCloseFrontLight = ReflectUtil.getMethodSafely(deviceControllerClass, "closeFrontLight", Context.class);
            sMethodGetFrontLightValue = ReflectUtil.getMethodSafely(deviceControllerClass, "getFrontLightValue", Context.class);
            sMethodSetFrontLightValue = ReflectUtil.getMethodSafely(deviceControllerClass, "setFrontLightValue", Context.class, int.class);
            sMethodGetFrontLightConfigValue = ReflectUtil.getMethodSafely(deviceControllerClass, "getFrontLightConfigValue", Context.class);
            sMethodSetFrontLightConfigValue = ReflectUtil.getMethodSafely(deviceControllerClass, "setFrontLightConfigValue", Context.class, int.class);
            sMethodUseBigPen = ReflectUtil.getMethodSafely(deviceControllerClass, "useBigPen", boolean.class);
            sMethodStopTpd = ReflectUtil.getMethodSafely(deviceControllerClass, "stopTpd");
            sMethodStartTpd = ReflectUtil.getMethodSafely(deviceControllerClass, "startTpd");
            sMethodEnableTpd = ReflectUtil.getMethodSafely(cls, "enableOnyxTpd", int.class);

            sMethodLed = ReflectUtil.getMethodSafely(deviceControllerClass, "led", boolean.class);
            sMethodSetLedColor = ReflectUtil.getMethodSafely(deviceControllerClass, "setLedColor", String.class, int.class);

            sMethodIsTouchable = ReflectUtil.getMethodSafely(deviceControllerClass, "isTouchable");
            sMethodGetTouchType = ReflectUtil.getMethodSafely(deviceControllerClass, "getTouchType");
            sMethodHasWifi = ReflectUtil.getMethodSafely(deviceControllerClass, "hasWifi");
            sMethodHasAudio = ReflectUtil.getMethodSafely(deviceControllerClass, "hasAudio");
            sMethodHasFrontLight = ReflectUtil.getMethodSafely(deviceControllerClass, "hasFrontLight");

            // signature of "public void setUpdatePolicy(int updatePolicy, int guInterval)"
            sMethodSetUpdatePolicy = ReflectUtil.getMethodSafely(cls, "setUpdatePolicy", int.class, int.class);
            // signature of "public void postInvalidate(int updateMode)"
            sMethodPostInvalidate = ReflectUtil.getMethodSafely(cls, "postInvalidate", int.class);
            // signature of "public void refreshScreen(int updateMode)"
            sMethodRefreshScreen = ReflectUtil.getMethodSafely(cls, "refreshScreen", int.class);
            sMethodRefreshScreenRegion = ReflectUtil.getMethodSafely(cls, "refreshScreen", int.class, int.class, int.class, int.class, int.class);
            sMethodScreenshot = ReflectUtil.getMethodSafely(cls, "screenshot", int.class, String.class);

            sMethodSetStrokeColor = ReflectUtil.getMethodSafely(cls, "setStrokeColor", int.class);
            sMethodSetStrokeStyle = ReflectUtil.getMethodSafely(cls, "setStrokeStyle", int.class);
            sMethodSetPainterStyle = ReflectUtil.getMethodSafely(cls, "setPainterStyle", boolean.class, Paint.Style.class, Paint.Join.class, Paint.Cap.class);
            sMethodMoveTo = ReflectUtil.getMethodSafely(cls, "moveTo", float.class, float.class, float.class);
            sMethodLineTo = ReflectUtil.getMethodSafely(cls, "lineTo", float.class, float.class, int.class);
            sMethodQuadTo = ReflectUtil.getMethodSafely(cls, "quadTo", float.class, float.class, int.class);
            sMethodEnablePost = ReflectUtil.getMethodSafely(cls, "enablePost", int.class);
            sMethodApplyGammaCorrection = ReflectUtil.getMethodSafely(cls, "applyGammaCorrection", boolean.class, int.class);

            sMethodStartStroke = ReflectUtil.getMethodSafely(cls, "startStroke", float.class, float.class, float.class, float.class, float.class, float.class);
            sMethodAddStrokePoint = ReflectUtil.getMethodSafely(cls, "addStrokePoint", float.class, float.class, float.class, float.class, float.class, float.class);
            sMethodFinishStroke = ReflectUtil.getMethodSafely(cls, "finishStroke", float.class, float.class, float.class, float.class, float.class, float.class);

            // signature of "public void invalidate(int updateMode)"
            sMethodInvalidate = ReflectUtil.getMethodSafely(cls, "invalidate", int.class);
            // signature of "public void invalidate(int updateMode)"
            sMethodSetViewDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "setDefaultUpdateMode", int.class);
            // signature of "public void invalidate(int updateMode)"
            sMethodGetViewDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "getDefaultUpdateMode");
            sMethodResetViewUpdateMode = ReflectUtil.getMethodSafely(cls, "resetUpdateMode");

            // signature of "public void invalidate(int updateMode)"
            sMethodGetSystemDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "getGlobalUpdateMode");
            // signature of "public void invalidate(int updateMode)"
            sMethodSetSystemDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "setGlobalUpdateMode", int.class);
            // signature of "public void setFirstDrawUpdateMode(int updateMode)"
            sMethodSetFirstDrawUpdateMode = ReflectUtil.getMethodSafely(cls, "setFirstDrawUpdateMode", int.class);
            // signature of "public void setWaveformAndScheme(int mode, int scheme)"
            sMethodSetSystemUpdateModeAndScheme = ReflectUtil.getMethodSafely(cls, "setWaveformAndScheme", int.class, int.class, int.class);
            // signature of "public void resetWaveformAndScheme()"
            sMethodClearSystemUpdateModeAndScheme = ReflectUtil.getMethodSafely(cls, "resetWaveformAndScheme");
            sMethodApplyApplicationFastMode = ReflectUtil.getMethodSafely(cls, "applyApplicationFastMode", String.class, boolean.class, boolean.class);
            sMethodEnableScreenUpdate = ReflectUtil.getMethodSafely(cls, "enableScreenUpdate", boolean.class);
            sMethodSetDisplayScheme = ReflectUtil.getMethodSafely(cls, "setDisplayScheme", int.class);
            sMethodWaitForUpdateFinished = ReflectUtil.getMethodSafely(cls, "waitForUpdateFinished");

            sMethodSetVCom = ReflectUtil.getMethodSafely(deviceControllerClass, "setVCom", Context.class, int.class, String.class);
            sMethodGetVCom = ReflectUtil.getMethodSafely(deviceControllerClass, "getVCom", String.class);
            sMethodSetWaveform = ReflectUtil.getMethodSafely(deviceControllerClass, "updateWaveform", String.class, String.class);
            sMethodReadSystemConfig = ReflectUtil.getMethodSafely(deviceControllerClass, "readSystemConfig", String.class);
            sMethodSaveSystemConfig = ReflectUtil.getMethodSafely(deviceControllerClass, "saveSystemConfig", String.class, String.class);
            sMethodUpdateMetadataDB = ReflectUtil.getMethodSafely(deviceControllerClass, "updateMetadataDB", String.class, String.class);

            sMethodHasBluetooth = ReflectUtil.getMethodSafely(deviceControllerClass, "hasBluetooth");

            sMethodHas5WayButton = ReflectUtil.getMethodSafely(deviceControllerClass, "has5WayButton");
            sMethodHasPageButton = ReflectUtil.getMethodSafely(deviceControllerClass, "hasPageButton");

            Log.d(TAG, "init device EINK_ONYX_GC_MASK.");
            return sInstance;
        }
        return sInstance;
    }


    @Override
    public DeviceInfo.TouchType getTouchType(Context context) {
        Boolean succ = (Boolean) this.invokeDeviceControllerMethod(context, sMethodIsTouchable);
        if (succ == null || !succ.booleanValue()) {
            return DeviceInfo.TouchType.None;
        }

        Integer n = (Integer) this.invokeDeviceControllerMethod(context, sMethodGetTouchType);
        if (n.intValue() == sTouchTypeUnknown) {
            return DeviceInfo.TouchType.Unknown;
        } else if (n.intValue() == sTouchTypeIR) {
            return DeviceInfo.TouchType.IR;
        } else if (n.intValue() == sTouchTypeCapacitive) {
            return DeviceInfo.TouchType.Capacitive;
        } else {
            assert (false);
            return DeviceInfo.TouchType.Unknown;
        }
    }

    @Override
    public boolean hasWifi(Context context) {
        Boolean has = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasWifi);
        if (has == null) {
            return false;
        }

        return has.booleanValue();
    }

    @Override
    public boolean hasAudio(Context context) {
        Boolean has = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasAudio);
        if (has == null) {
            return false;
        }

        return has.booleanValue();
    }

    @Override
    public boolean hasBluetooth(Context context) {
        Boolean has = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasBluetooth);
        if (has == null) {
            return false;
        }

        return has.booleanValue();
    }

    @Override
    public boolean has5WayButton(Context context) {
        Boolean has = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHas5WayButton);
        if (has == null) {
            return false;
        }

        return has.booleanValue();
    }

    @Override
    public boolean hasPageButton(Context context) {
        Boolean has = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasPageButton);
        if (has == null) {
            return false;
        }

        return has.booleanValue();
    }

    @Override
    public boolean hasFrontLight(Context context) {
        Boolean has = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasFrontLight);
        if (has == null) {
            return false;
        }

        return has.booleanValue();
    }

    public void useBigPen(boolean use) {
        invokeDeviceControllerMethod(null, sMethodUseBigPen, use);
    }

    public void stopTpd() {
        invokeDeviceControllerMethod(null, sMethodStopTpd);
    }

    public void startTpd() {
        invokeDeviceControllerMethod(null, sMethodStartTpd);
    }

    public void enableTpd(boolean enable) {
        ReflectUtil.invokeMethodSafely(sMethodEnableTpd, null, enable ? 1 : 0);
    }

    @Override
    public EpdController.UpdateMode getViewDefaultUpdateMode(View view) {
        Integer res = (Integer) ReflectUtil.invokeMethodSafely(sMethodGetViewDefaultUpdateMode, view);
        if (res == null) {
            return EpdController.UpdateMode.GU;
        }

        return this.updateModeFromValue(res.intValue());
    }

    public void resetViewUpdateMode(View view) {
        ReflectUtil.invokeMethodSafely(sMethodResetViewUpdateMode, view);
    }

    @Override
    public boolean setViewDefaultUpdateMode(View view, EpdController.UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetViewDefaultUpdateMode, view, getUpdateMode(mode));
        return res != null;
    }

    @Override
    public EpdController.UpdateMode getSystemDefaultUpdateMode() {
        Integer res = (Integer) ReflectUtil.invokeMethodSafely(sMethodGetSystemDefaultUpdateMode, null);
        if (res == null) {
            return EpdController.UpdateMode.GU;
        }

        return this.updateModeFromValue(res.intValue());
    }

    @Override
    public boolean setSystemDefaultUpdateMode(EpdController.UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetSystemDefaultUpdateMode, null, getUpdateMode(mode));
        return res != null;
    }

    public boolean setFirstDrawUpdateMode(EpdController.UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetFirstDrawUpdateMode, null, getUpdateMode(mode));
        return res != null;
    }

    @Override
    public boolean setSystemUpdateModeAndScheme(EpdController.UpdateMode mode, EpdController.UpdateScheme scheme, int count) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetSystemUpdateModeAndScheme, null, getUpdateMode(mode), getUpdateScheme(scheme), count);
        return res != null;
    }

    @Override
    public boolean clearSystemUpdateModeAndScheme() {
        Object res = ReflectUtil.invokeMethodSafely(sMethodClearSystemUpdateModeAndScheme, null);
        return res != null;
    }

    public boolean applyApplicationFastMode(final String application, boolean enable, boolean clear) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodApplyApplicationFastMode, null, application, enable, clear);
        return res != null;
    }

    @Override
    public int getFrontLightBrightnessMinimum(Context context) {
        return 0;
    }

    @Override
    public int getFrontLightBrightnessMaximum(Context context) {
        return 255;
    }

    @Override
    public boolean openFrontLight(Context context) {
        Boolean succ = (Boolean) this.invokeDeviceControllerMethod(context, sMethodOpenFrontLight, context);
        if (succ == null) {
            return false;
        }
        return succ.booleanValue();
    }

    @Override
    public boolean closeFrontLight(Context context) {
        Boolean succ = (Boolean) this.invokeDeviceControllerMethod(context, sMethodCloseFrontLight, context);
        if (succ == null) {
            return false;
        }

        return succ.booleanValue();
    }

    @Override
    public int getFrontLightDeviceValue(Context context) {
        Integer value = (Integer) this.invokeDeviceControllerMethod(context, sMethodGetFrontLightValue, context);
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }

    @Override
    public boolean setFrontLightDeviceValue(Context context, int value) {
        Object res = this.invokeDeviceControllerMethod(context, sMethodSetFrontLightValue, context, Integer.valueOf(value));
        return res != null;
    }

    @Override
    public int getFrontLightConfigValue(Context context) {
        Integer res = (Integer) this.invokeDeviceControllerMethod(context, sMethodGetFrontLightConfigValue, context);
        return res.intValue();
    }

    @Override
    public boolean setFrontLightConfigValue(Context context, int value) {
        this.invokeDeviceControllerMethod(context, sMethodSetFrontLightConfigValue, context, Integer.valueOf(value));
        return true;
    }

    @Override
    public List<Integer> getFrontLightValueList(Context context) {
        Integer intValues[] = {0, 16, 32, 48, 64, 80, 96, 112, 128, 144, 160};
        return Arrays.asList(intValues);
    }

    @Override
    public void led(Context context, boolean on) {
        this.invokeDeviceControllerMethod(context, sMethodLed, on);
    }

    public boolean setLedColor(final String color, final int on) {
        invokeDeviceControllerMethod(null, sMethodSetLedColor, color, on);
        return true;
    }

    @Override
    public void setVCom(Context context, int value, String path) {
        this.invokeDeviceControllerMethod(context, sMethodSetVCom, context, value, path);
        //return res != null;
    }

    @Override
    public int getVCom(Context context, String path) {
        Integer value = (Integer) this.invokeDeviceControllerMethod(context, sMethodGetVCom, path);
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }

    @Override
    public void updateWaveform(Context context, String path, String target) {
        this.invokeDeviceControllerMethod(context, sMethodSetWaveform, path, target);
        //return res != null;
    }

    @Override
    public String readSystemConfig(Context context, String key) {
        Object result = this.invokeDeviceControllerMethod(context, sMethodReadSystemConfig, key);
        if (result == null || result.equals("")) {
            return "";
        }
        return result.toString();
    }

    @Override
    public boolean saveSystemConfig(Context context, String key, String value) {
        return (Boolean) this.invokeDeviceControllerMethod(context, sMethodSaveSystemConfig, key, value);
    }

    @Override
    public void updateMetadataDB(Context context, String path, String target) {
        this.invokeDeviceControllerMethod(context, sMethodUpdateMetadataDB, path, target);
    }

    /**
     * helper method to do trivial argument and exception check, return null if failed
     *
     * @param context
     * @param method
     * @return
     */
    private Object invokeDeviceControllerMethod(Context context, Method method, Object... args) {
        if (method == null) {
            return null;
        }

        return ReflectUtil.invokeMethodSafely(method, null, args);
    }

    EpdController.UpdateMode getEpdMode(EpdController.EPDMode mode) {
        switch (mode) {
            case FULL:
                return EpdController.UpdateMode.GC;
            case AUTO:
            case TEXT:
            case AUTO_PART:
                return EpdController.UpdateMode.GU;
            default:
                return EpdController.UpdateMode.DW;
        }
    }

    int getUpdateMode(EpdController.UpdateMode mode) {
        // default use GC update mode
        int dst_mode = sModeGC;

        switch (mode) {
            case GU_FAST:
            case DW:
                dst_mode = sModeDW;
                break;
            case GU:
                dst_mode = sModeGU;
                break;
            case GC:
                dst_mode = sModeGC;
                break;
            case ANIMATION:
                dst_mode = sModeAnimation;
                break;
            case ANIMATION_QUALITY:
                dst_mode = sModeAnimationQuality;
                break;
            case GC4:
                dst_mode = sModeGC4;
                break;
            default:
                assert (false);
                break;
        }
        return dst_mode;
    }

    private int getUpdateScheme(EpdController.UpdateScheme scheme) {
        int dst_scheme = sSchemeQUEUE;
        switch (scheme) {
            case SNAPSHOT:
                dst_scheme = sSchemeSNAPSHOT;
                break;
            case QUEUE:
                dst_scheme = sSchemeQUEUE;
                break;
            case QUEUE_AND_MERGE:
                dst_scheme = sSchemeQUEUE_AND_MERGE;
                break;
            default:
                assert (false);
                break;
        }
        return dst_scheme;

    }

    private EpdController.UpdateMode updateModeFromValue(int value) {
        if (value == sModeDW) {
            return EpdController.UpdateMode.DW;
        } else if (value == sModeGU) {
            return EpdController.UpdateMode.GU;
        } else if (value == sModeGC) {
            return EpdController.UpdateMode.GC;
        }
        return EpdController.UpdateMode.GC;
    }

    private static int getPolicyValue(EpdController.UpdatePolicy policy) {
        int dst_value = sModeGU;
        switch (policy) {
            case Automatic:
                dst_value |= sPolicyAutomatic;
                break;
            case GUIntervally:
                dst_value |= sPolicyGUIntervally;
                break;
            default:
                assert (false);
                break;
        }

        return dst_value;
    }

    @Override
    public String getPlatform() {
        return Platforms.IMX6;
    }
}
