/**
 * 
 */
package com.onyx.android.sdk.api.device.epd;

import android.view.View;
import com.onyx.android.sdk.device.Device;

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
        Device.currentDevice().invalidate(view, mode);
    }

    /**
     * be careful when UpdateMode is GC because postInvalidate() does not take effect immediately
     *  
     * @param view
     * @param mode
     */
    public static void postInvalidate(View view, UpdateMode mode)
    {
        Device.currentDevice().postInvalidate(view, mode);
    }


    public static boolean enableScreenUpdate(View view, boolean enable) {
        return Device.currentDevice().enableScreenUpdate(view, enable);
    }

    public static boolean setViewDefaultUpdateMode(View view, UpdateMode mode) {
        Device.currentDevice().setViewDefaultUpdateMode(view, mode);
        return true;
    }

    public static UpdateMode getViewDefaultUpdateMode(View view) {
        return Device.currentDevice().getViewDefaultUpdateMode(view);
    }

    public static boolean resetViewUpdateMode(View view) {
        Device.currentDevice().resetViewUpdateMode(view);
        return true;
    }

    public static UpdateMode getSystemDefaultUpdateMode() {
        return Device.currentDevice().getSystemDefaultUpdateMode();
    }

    public static boolean setSystemDefaultUpdateMode(UpdateMode mode) {
        Device.currentDevice().setSystemDefaultUpdateMode(mode);
        return false;
    }

    public static boolean setSystemUpdateModeAndScheme(UpdateMode mode, UpdateScheme scheme, int count) {
        Device.currentDevice().setSystemUpdateModeAndScheme(mode, scheme, count);
        return false;
    }
    
    public static boolean clearSystemUpdateModeAndScheme() {
        Device.currentDevice().clearSystemUpdateModeAndScheme();
        return false;
    }

    public static boolean applyApplicationFastMode(final String application, boolean enable, boolean clear) {
        Device.currentDevice().applyApplicationFastMode(application, enable, clear);
        return true;
    }

    public static boolean setDisplayScheme(int scheme) {
        Device.currentDevice().setDisplayScheme(scheme);
        return true;
    }

    public static void waitForUpdateFinished() {
        Device.currentDevice().waitForUpdateFinished();
    }

    public static void refreshScreen(View view, UpdateMode mode)
    {
        Device.currentDevice().refreshScreen(view, mode);
    }

    public static void refreshScreenRegion(View view, int left, int top, int width, int height, UpdateMode mode) {
        Device.currentDevice().refreshScreenRegion(view, left, top, width, height, mode);
    }

    public static void setStrokeWidth(View view, int width) {

    }

    public static void setScreenHandWritingPenState(View view, int penState) {
        Device.currentDevice().setScreenHandWritingPenState(view, penState);
    }

    public static void setScreenHandWritingRegionLimit(View view, int left, int top, int right, int bottom) {
        Device.currentDevice().setScreenHandWritingRegionLimit(view, left, top, right, bottom);
    }

    public static void enablePost(View view, int enable) {
        Device.currentDevice().enablePost(view, enable);
    }

    public static void moveTo(float x, float y, float width) {
        Device.currentDevice().moveTo(x, y, width);
    }

    public static void lineTo(float x, float y, UpdateMode mode) {
        Device.currentDevice().lineTo(x, y, mode);
    }

    public static void quadTo(float x, float y, UpdateMode mode) {
        Device.currentDevice().quadTo(x, y, mode);
    }

}
