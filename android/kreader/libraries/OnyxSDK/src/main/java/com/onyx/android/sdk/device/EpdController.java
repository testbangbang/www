/**
 * 
 */
package com.onyx.android.sdk.device;

import android.content.Context;
import android.os.Build;
import android.view.Surface;
import android.view.View;

/**
 * singleton class
 * 
 * @author joy
 *
 */
public abstract class EpdController
{
    @SuppressWarnings("unused")
    private static String TAG = "EpdController";
    
    public static enum EPDMode { FULL, AUTO, TEXT, AUTO_PART, AUTO_BLACK_WHITE, AUTO_A2 }
    public enum UpdatePolicy { Automatic, GUIntervally }
    public enum UpdateMode { None, DW, GU, GU_FAST, GC, ANIMATION, ANIMATION_QUALITY, GC4, }
    public enum UpdateScheme { None, SNAPSHOT, QUEUE, QUEUE_AND_MERGE }

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
    
    public static int getWindowRotation(Context context)
    {
        return Surface.ROTATION_0;
    }
    
    public static boolean setWindowRotation(Context context, int rotation)
    {
        return false;
    }
    
    public static boolean setUpdatePolicy(View view, UpdatePolicy policy, int guInterval)
    {
        return false;
    }
    
    public static void invalidate(View view, UpdateMode mode)
    {
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
    
    public static EPDMode getMode()
    {
        return DeviceInfo.currentDevice.getEpdMode();
    }
    
    @Deprecated
    public static boolean setMode(Context context, EPDMode mode)
    {
        return DeviceInfo.currentDevice.setEpdMode(context, mode);
    }
    
    /**
     * prefer this method over setMode(Context context, EPDMode mode)
     * 
     * @param view
     * @param mode
     * @return
     */
    public static boolean setMode(View view, EPDMode mode)
    {
        return DeviceInfo.currentDevice.setEpdMode(view, mode);
    }
    
    public static UpdateMode getViewDefaultUpdateMode(View view)
    {
        return DeviceInfo.currentDevice.getViewDefaultUpdateMode(view);
        
    }

    public static boolean enableScreenUpdate(View view, boolean enable) {
        return DeviceInfo.currentDevice.enableScreenUpdate(view, enable);
    }

    public static boolean setViewDefaultUpdateMode(View view, EpdController.UpdateMode mode)
    {
        DeviceInfo.currentDevice.setViewDefaultUpdateMode(view, mode);
        return true;
    }

    public static boolean resetViewUpdateMode(View view) {
        DeviceInfo.currentDevice.resetViewUpdateMode(view);
        return true;
    }

    public static EpdController.UpdateMode getSystemDefaultUpdateMode()
    {
        return DeviceInfo.currentDevice.getSystemDefaultUpdateMode();

    }

    public static boolean setSystemDefaultUpdateMode(EpdController.UpdateMode mode)
    {
        DeviceInfo.currentDevice.setSystemDefaultUpdateMode(mode);
        return false;
    }

    public static boolean setSystemUpdateModeAndScheme(EpdController.UpdateMode mode, EpdController.UpdateScheme scheme, int count)
    {
        DeviceInfo.currentDevice.setSystemUpdateModeAndScheme(mode, scheme, count);
        return false;
    }
    
    public static boolean clearSystemUpdateModeAndScheme()
    {
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

    public static boolean supportDFB() {
        return DeviceInfo.currentDevice.supportDFB();
    }

    public static void setStrokeColor(int color) {
        DeviceInfo.currentDevice.setStrokeColor(color);
    }
    public static void setStrokeStyle(int style) {
        DeviceInfo.currentDevice.setStrokeStyle(style);
    }

    public static float startStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        return DeviceInfo.currentDevice.startStroke(baseWidth, x, y, pressure, size, time);
    }

    public static float addStrokePoint(float baseWidth, float x, float y, float pressure, float size, float time) {
        return DeviceInfo.currentDevice.addStrokePoint(baseWidth, x, y, pressure, size, time);
    }

    public static float finishStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        return DeviceInfo.currentDevice.finishStroke(baseWidth, x, y, pressure, size, time);
    }

    public static void moveTo(float x, float y, float w) {
        DeviceInfo.currentDevice.moveTo(x, y, w);
    }

    public static void lineTo(float x, float y, UpdateMode mode) {
        DeviceInfo.currentDevice.lineTo(x, y, mode);
    }

    public static void quadTo(float x, float y, UpdateMode mode) {
        DeviceInfo.currentDevice.quadTo(x, y, mode);
    }

    public static void screenshot(View view, int rotation, final String path) {
        DeviceInfo.currentDevice.screenshot(view, rotation, path);
    }

    public static void enablePost(View view, int enable) {
        DeviceInfo.currentDevice.enablePost(view, enable);
    }

    public static void enterScribbleMode(View view) {
        DeviceInfo.currentDevice.enterScribbleMode(view);
    }

    public static void leaveScribbleMode(View view) {
        DeviceInfo.currentDevice.leaveScribbleMode(view);
    }

    public static void applyGammaCorrection(boolean apply, int value) {
        DeviceInfo.currentDevice.applyGammaCorrection(apply, value);
    }

    public static UpdateMode preferedUpdateMode() {
        String device = Build.DEVICE;
        if (device.startsWith("M96")) {
            return UpdateMode.GC;
        }
    	return UpdateMode.GU;
    }

}
