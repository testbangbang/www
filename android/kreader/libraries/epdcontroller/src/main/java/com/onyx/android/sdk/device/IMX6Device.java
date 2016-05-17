package com.onyx.android.sdk.device;

import android.content.Context;
import android.util.Log;
import android.view.View;
import com.onyx.android.sdk.api.device.epd.EPDMode;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.api.device.epd.UpdatePolicy;
import com.onyx.android.sdk.api.device.epd.UpdateScheme;
import com.onyx.android.sdk.utils.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Joy on 2016/5/10.
 */
public class IMX6Device {

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
     * View.setUpdatePolicy(int updatePolicy, int guInterval)
     */
    private static Method sMethodSetUpdatePolicy = null;
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

    private IMX6Device() {
    }

    public boolean setUpdatePolicy(View view, UpdatePolicy policy, int guInterval) {
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

    public EPDMode getEpdMode() {
        return EPDMode.AUTO;
    }

    public boolean setEpdMode(Context context, EPDMode mode) {
        setSystemUpdateModeAndScheme(getEpdMode(mode), UpdateScheme.QUEUE_AND_MERGE, Integer.MAX_VALUE);
        return false;
    }

    public void invalidate(View view, UpdateMode mode) {
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


    public void postInvalidate(View view, UpdateMode mode) {
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

    public void refreshScreen(View view, UpdateMode mode) {
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

    public void refreshScreenRegion(View view, int left, int top, int width, int height, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);
        try {
            assert (sMethodRefreshScreenRegion != null);
            sMethodRefreshScreenRegion.invoke(view, left, top, width, height, dst_mode_value);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

            sPolicyAutomatic = value_policy_automic;
            sPolicyGUIntervally = value_policy_gu_intervally;

            sModeDW = value_mode_regional | value_mode_nowait | value_mode_waveform_du | value_mode_update_partial;
            sModeGU = value_mode_regional | value_mode_nowait | value_mode_waveform_gc16 | value_mode_update_partial;
            sModeGC = value_mode_regional | value_mode_wait | value_mode_waveform_gc16 | value_mode_update_full;
            sModeAnimation = value_mode_regional | value_mode_nowait | value_mode_waveform_animation | value_mode_update_partial;
            sModeAnimationQuality = ReflectUtil.getStaticIntFieldSafely(cls, "UI_A2_QUALITY_MODE");
            sModeGC4 = value_mode_regional | value_mode_nowait | value_mode_waveform_gc4 | value_mode_update_partial;

            // signature of "public void setUpdatePolicy(int updatePolicy, int guInterval)"
            sMethodSetUpdatePolicy = ReflectUtil.getMethodSafely(cls, "setUpdatePolicy", int.class, int.class);
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
            sMethodWaitForUpdateFinished = ReflectUtil.getMethodSafely(cls, "waitForUpdateFinished");

            Log.d(TAG, "init device EINK_ONYX_GC_MASK.");
            return sInstance;
        }
        return sInstance;
    }

    public UpdateMode getViewDefaultUpdateMode(View view) {
        Integer res = (Integer) ReflectUtil.invokeMethodSafely(sMethodGetViewDefaultUpdateMode, view);
        if (res == null) {
            return UpdateMode.GU;
        }

        return this.updateModeFromValue(res.intValue());
    }

    public void resetViewUpdateMode(View view) {
        ReflectUtil.invokeMethodSafely(sMethodResetViewUpdateMode, view);
    }

    public boolean setViewDefaultUpdateMode(View view, UpdateMode mode) {
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

    public boolean setSystemDefaultUpdateMode(UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetSystemDefaultUpdateMode, null, getUpdateMode(mode));
        return res != null;
    }

    public boolean setFirstDrawUpdateMode(UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetFirstDrawUpdateMode, null, getUpdateMode(mode));
        return res != null;
    }

    public boolean setSystemUpdateModeAndScheme(UpdateMode mode, UpdateScheme scheme, int count) {
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

    UpdateMode getEpdMode(EPDMode mode) {
        switch (mode) {
            case FULL:
                return UpdateMode.GC;
            case AUTO:
            case TEXT:
            case AUTO_PART:
                return UpdateMode.GU;
            default:
                return UpdateMode.DW;
        }
    }

    int getUpdateMode(UpdateMode mode) {
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
            return UpdateMode.DW;
        } else if (value == sModeGU) {
            return UpdateMode.GU;
        } else if (value == sModeGC) {
            return UpdateMode.GC;
        }
        return UpdateMode.GC;
    }

    private static int getPolicyValue(UpdatePolicy policy) {
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
}
