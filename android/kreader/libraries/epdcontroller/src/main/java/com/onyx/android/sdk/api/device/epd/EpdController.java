/**
 * 
 */
package com.onyx.android.sdk.api.device.epd;

import android.view.View;
import com.onyx.android.sdk.device.DeviceInfo;

/**
 * singleton class
 * 
 * @author joy
 *
 */
public abstract class EpdController
{
    @SuppressWarnings("unused")
    private static String TAG = EpdController.class.getSimpleName();

    static public int SCHEME_START                  = 1;
    static public int SCHEME_NORMAL                 = 1;
    static public int SCHEME_KEYBOARD               = 2;
    static public int SCHEME_SCRIBBLE               = 3;
    static public int SCHEME_APPLICATION_ANIMATION  = 4;
    static public int SCHEME_SYSTEM_ANIMATION       = 5;
    static public int SCHEME_END                    = SCHEME_SYSTEM_ANIMATION;


    private EpdController()
    {
    }

    
    public static void invalidate(final View view, final UpdateMode mode) {
        DeviceInfo.currentDevice.invalidate(view, mode);
    }

    /**
     * be careful when UpdateMode is GC because postInvalidate() does not take effect immediately
     *  
     * @param view
     * @param mode
     */
    public static void postInvalidate(View view, UpdateMode mode)
    {
        DeviceInfo.currentDevice.postInvalidate(view, mode);
    }


    public static boolean enableScreenUpdate(View view, boolean enable) {
        return DeviceInfo.currentDevice.enableScreenUpdate(view, enable);
    }

    public static boolean setViewDefaultUpdateMode(View view, UpdateMode mode) {
        DeviceInfo.currentDevice.setViewDefaultUpdateMode(view, mode);
        return true;
    }

    public static UpdateMode getViewDefaultUpdateMode(View view) {
        return DeviceInfo.currentDevice.getViewDefaultUpdateMode(view);
    }

    public static boolean resetViewUpdateMode(View view) {
        DeviceInfo.currentDevice.resetViewUpdateMode(view);
        return true;
    }

    public static UpdateMode getSystemDefaultUpdateMode() {
        return DeviceInfo.currentDevice.getSystemDefaultUpdateMode();
    }

    public static boolean setSystemDefaultUpdateMode(UpdateMode mode) {
        DeviceInfo.currentDevice.setSystemDefaultUpdateMode(mode);
        return false;
    }

    public static boolean setSystemUpdateModeAndScheme(UpdateMode mode, UpdateScheme scheme, int count) {
        DeviceInfo.currentDevice.setSystemUpdateModeAndScheme(mode, scheme, count);
        return false;
    }
    
    public static boolean clearSystemUpdateModeAndScheme() {
        DeviceInfo.currentDevice.clearSystemUpdateModeAndScheme();
        return false;
    }

    public static boolean applyApplicationFastMode(final String application, boolean enable, boolean clear) {
        DeviceInfo.currentDevice.applyApplicationFastMode(application, enable, clear);
        return true;
    }

    public static boolean setDisplayScheme(int scheme) {
        DeviceInfo.currentDevice.setDisplayScheme(scheme);
        return true;
    }

    public static void waitForUpdateFinished() {
        DeviceInfo.currentDevice.waitForUpdateFinished();
    }

    public static void refreshScreen(View view, UpdateMode mode)
    {
        DeviceInfo.currentDevice.refreshScreen(view, mode);
    }

    public static void refreshScreenRegion(View view, int left, int top, int width, int height, UpdateMode mode) {
        DeviceInfo.currentDevice.refreshScreenRegion(view , left, top, width, height, mode);
    }

}
