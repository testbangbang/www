package com.onyx.android.sdk.device;

import android.content.Context;
import android.util.Log;
import android.view.View;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.api.device.epd.UpdateScheme;
import com.onyx.android.sdk.utils.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Joy on 2016/5/10.
 */
public class IMX6Device extends DeviceInfo {

    private static final String TAG = IMX6Device.class.getSimpleName();

    private static IMX6Device sInstance = null;
    private static int sModeDW = 0;
    private static int sModeGU = 0;
    private static int sModeGC = 0;
    private static int sModeAnimationQuality = 0;
    private static int sModeGC4 = 0;

    private static final int sSchemeSNAPSHOT = 0;
    private static final int sSchemeQUEUE = 1;
    private static final int sSchemeQUEUE_AND_MERGE = 2;

    /**
     * View.refreshScreen(int updateMode)
     */
    private static Method sMethodRefreshScreen = null;
    private static Method sMethodRefreshScreenRegion = null;

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
    private static Method sMethodEnableHandWriting = null;
    private static Method sMethodSetScreenHandWritingRegionLimit = null;
    private static Method sMethodEnablePost = null;

    private IMX6Device() {
    }

    public static IMX6Device createDevice() {
        if (sInstance == null) {
            sInstance = new IMX6Device();

            Class<View> cls = View.class;

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


            sModeDW = value_mode_regional | value_mode_nowait | value_mode_waveform_du | value_mode_update_partial;
            sModeGU = value_mode_regional | value_mode_nowait | value_mode_waveform_gc16 | value_mode_update_partial;
            sModeGC = value_mode_regional | value_mode_wait | value_mode_waveform_gc16 | value_mode_update_full;
            sModeAnimationQuality = ReflectUtil.getStaticIntFieldSafely(cls, "UI_A2_QUALITY_MODE");
            sModeGC4 = value_mode_regional | value_mode_nowait | value_mode_waveform_gc4 | value_mode_update_partial;

            // signature of "public void postInvalidate(int updateMode)"
            sMethodPostInvalidate = ReflectUtil.getMethodSafely(cls, "postInvalidate", int.class);
            // signature of "public void refreshScreen(int updateMode)"
            sMethodRefreshScreen = ReflectUtil.getMethodSafely(cls, "refreshScreen", int.class);
            sMethodRefreshScreenRegion = ReflectUtil.getMethodSafely(cls, "refreshScreen", int.class, int.class, int.class, int.class, int.class);

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
            sMethodEnableHandWriting = ReflectUtil.getMethodSafely(cls, "enableHandWriting", int.class);
            sMethodSetScreenHandWritingRegionLimit = ReflectUtil.getMethodSafely(cls, "setScreenHandWritingRegionLimit", int.class, int.class, int.class, int.class);
            sMethodEnablePost = ReflectUtil.getMethodSafely(cls, "enablePost", int.class);
            sMethodWaitForUpdateFinished = ReflectUtil.getMethodSafely(cls, "waitForUpdateFinished");

            Log.d(TAG, "init device imx6.");
            return sInstance;
        }
        return sInstance;
    }

    public void invalidate(final View view, final UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);
        try {
            assert (sMethodInvalidate != null);
            sMethodInvalidate.invoke(view, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }


    public void postInvalidate(final View view, final UpdateMode mode) {
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
    }

    public void refreshScreen(final View view, final UpdateMode mode) {
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

    public void refreshScreenRegion(final View view, int left, int top, int width, int height, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);
        try {
            assert (sMethodRefreshScreenRegion != null);
            sMethodRefreshScreenRegion.invoke(view, left, top, width, height, dst_mode_value);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean enableScreenUpdate(final View view, boolean enable) {
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



    public UpdateMode getViewDefaultUpdateMode(final View view) {
        Integer res = (Integer) ReflectUtil.invokeMethodSafely(sMethodGetViewDefaultUpdateMode, view);
        if (res == null) {
            return UpdateMode.GU;
        }

        return this.updateModeFromValue(res.intValue());
    }

    public void resetViewUpdateMode(final View view) {
        ReflectUtil.invokeMethodSafely(sMethodResetViewUpdateMode, view);
    }

    public boolean setViewDefaultUpdateMode(final View view, UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetViewDefaultUpdateMode, view, getUpdateMode(mode));
        return res != null;
    }

    public UpdateMode getSystemDefaultUpdateMode() {
        Integer res = (Integer) ReflectUtil.invokeMethodSafely(sMethodGetSystemDefaultUpdateMode, null);
        if (res == null) {
            return UpdateMode.GU;
        }

        return this.updateModeFromValue(res.intValue());
    }

    public boolean setSystemDefaultUpdateMode(final UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetSystemDefaultUpdateMode, null, getUpdateMode(mode));
        return res != null;
    }

    public boolean setFirstDrawUpdateMode(final UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetFirstDrawUpdateMode, null, getUpdateMode(mode));
        return res != null;
    }

    public boolean setSystemUpdateModeAndScheme(final UpdateMode mode, final UpdateScheme scheme, int count) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetSystemUpdateModeAndScheme, null, getUpdateMode(mode), getUpdateScheme(scheme), count);
        return res != null;
    }

    public boolean clearSystemUpdateModeAndScheme() {
        Object res = ReflectUtil.invokeMethodSafely(sMethodClearSystemUpdateModeAndScheme, null);
        return res != null;
    }

    public boolean applyApplicationFastMode(final String application, boolean enable, boolean clear) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodApplyApplicationFastMode, null, application, enable, clear);
        return res != null;
    }

    private int getUpdateMode(UpdateMode mode) {
        // default use GC update mode
        int dst_mode = sModeGC;

        switch (mode) {
            case DU:
                dst_mode = sModeDW;
                break;
            case GU:
                dst_mode = sModeGU;
                break;
            case GC16:
                dst_mode = sModeGC;
                break;
            case ANIMATION:
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

    private int getUpdateScheme(UpdateScheme scheme) {
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

    private UpdateMode updateModeFromValue(int value) {
        if (value == sModeDW) {
            return UpdateMode.DU;
        } else if (value == sModeGU) {
            return UpdateMode.GU;
        } else if (value == sModeGC) {
            return UpdateMode.GC16;
        }
        return UpdateMode.GC16;
    }

    public void enableScreenHandWriting(View view, int enable) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodEnableHandWriting, view, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setScreenHandWritingRegionLimit(View view, int left, int top, int right, int bottom) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetScreenHandWritingRegionLimit, view, left, top, right, bottom);
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
}
