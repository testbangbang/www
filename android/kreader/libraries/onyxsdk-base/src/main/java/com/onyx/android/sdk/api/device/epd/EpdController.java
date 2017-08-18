/**
 * 
 */
package com.onyx.android.sdk.api.device.epd;

import android.graphics.Paint;
import android.graphics.Rect;
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

    public static void invalidate(final View view, int left, int top, int right, int bottom, final UpdateMode mode) {
        Device.currentDevice().invalidate(view, left, top, right, bottom, mode);
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

    public static void useFastScheme() {
        EpdController.setDisplayScheme(EpdController.SCHEME_SCRIBBLE);
    }

    public static void resetUpdateMode(View view) {
        EpdController.resetViewUpdateMode(view);
        useFastScheme();
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

    public static boolean supportRegal() {
        return Device.currentDevice().supportRegal();
    }

    public static void holdDisplay(boolean hold, UpdateMode updateMode, int ignoreFrame) {
        Device.currentDevice().holdDisplay(hold, updateMode, ignoreFrame);
    }

    public static void setStrokeWidth(float width) {
        Device.currentDevice().setStrokeWidth(width);
    }

    public static void setStrokeStyle(int style) {
        Device.currentDevice().setStrokeStyle(style);
    }

    public static void setStrokeColor(int color) {
        Device.currentDevice().setStrokeColor(color);
    }

    public static void setScreenHandWritingPenState(View view, int penState) {
        Device.currentDevice().setScreenHandWritingPenState(view, penState);
    }

    public static void setScreenHandWritingRegionLimit(View view, int left, int top, int right, int bottom) {
        Device.currentDevice().setScreenHandWritingRegionLimit(view, left, top, right, bottom);
    }

    public static void setScreenHandWritingRegionLimit(View view, int[] array) {
        Device.currentDevice().setScreenHandWritingRegionLimit(view, array);
    }

    public static void setScreenHandWritingRegionLimit(View view, Rect[] regions) {
        Device.currentDevice().setScreenHandWritingRegionLimit(view, regions);
    }

    public static float startStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        return Device.currentDevice().startStroke(baseWidth, x, y, pressure, size, time);
    }

    public static float addStrokePoint(float baseWidth, float x, float y, float pressure, float size, float time) {
        return Device.currentDevice().addStrokePoint(baseWidth, x, y, pressure, size, time);
    }

    public static float finishStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        return Device.currentDevice().finishStroke(baseWidth, x, y, pressure, size, time);
    }

    public static void enterScribbleMode(View view) {
        Device.currentDevice().enterScribbleMode(view);
    }

    public static void leaveScribbleMode(View view) {
        Device.currentDevice().leaveScribbleMode(view);
    }

    public static void enablePost(View view, int enable) {
        Device.currentDevice().enablePost(view, enable);
    }

    public static void setPainterStyle(boolean antiAlias, Paint.Style strokeStyle, Paint.Join joinStyle, Paint.Cap capStyle) {
        Device.currentDevice().setPainterStyle(antiAlias, strokeStyle, joinStyle, capStyle);
    }

    public static void moveTo(float x, float y, float width) {
        Device.currentDevice().moveTo(x, y, width);
    }

    public static void moveTo(View view, float x, float y, float width) {
        Device.currentDevice().moveTo(view, x, y, width);
    }

    public static void lineTo(float x, float y, UpdateMode mode) {
        Device.currentDevice().lineTo(x, y, mode);
    }

    public static void lineTo(View view, float x, float y, UpdateMode mode) {
        Device.currentDevice().lineTo(view, x, y, mode);
    }

    public static void quadTo(float x, float y, UpdateMode mode) {
        Device.currentDevice().quadTo(x, y, mode);
    }

    public static void quadTo(View view, float x, float y, UpdateMode mode) {
        Device.currentDevice().quadTo(view, x, y, mode);
    }

    public static void disableA2ForSpecificView(View view) {
        Device.currentDevice().disableA2ForSpecificView(view);
    }

    public static void enableA2ForSpecificView(View view) {
        Device.currentDevice().enableA2ForSpecificView(view);
    }

    public static float getTouchWidth() {
        return Device.currentDevice().getTouchWidth();
    }

    public static float getTouchHeight() {
        return Device.currentDevice().getTouchHeight();
    }

    public static void enableRegal() {
        Device.currentDevice().enableRegal(true);
    }

    public static void disableRegal() {
        Device.currentDevice().enableRegal(false);
    }
}
