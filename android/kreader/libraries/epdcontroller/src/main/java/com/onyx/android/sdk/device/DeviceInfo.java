/**
 * 
 */
package com.onyx.android.sdk.device;


import android.view.View;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.api.device.epd.UpdateScheme;

/**
 * Used internally by sdk. Should not export to public.
 * @author joy
 *
 */
public abstract class DeviceInfo
{
    public static final DeviceInfo currentDevice;
    
    static {
        currentDevice = detectDevice();
    }

    private static DeviceInfo detectDevice() {
        return IMX6Device.createDevice();
    }

    public abstract void invalidate(final View view, final UpdateMode mode);
    public abstract void postInvalidate(final View view, final UpdateMode mode);
    public abstract void refreshScreen(final View view, final UpdateMode mode);
    public abstract void refreshScreenRegion(final View view, int left, int top, int width, int height, UpdateMode mode);
    public abstract boolean enableScreenUpdate(final View view, boolean enable);

    public abstract boolean setDisplayScheme(int scheme);

    public abstract UpdateMode getViewDefaultUpdateMode(final View view);
    public abstract void resetViewUpdateMode(final View view);
    public abstract boolean setViewDefaultUpdateMode(final View view, final UpdateMode mode);

    public abstract UpdateMode getSystemDefaultUpdateMode();
    public abstract boolean setSystemDefaultUpdateMode(final UpdateMode mode);

    public abstract boolean setSystemUpdateModeAndScheme(final UpdateMode mode, final UpdateScheme scheme, int count);
    public abstract boolean clearSystemUpdateModeAndScheme();

    public abstract boolean applyApplicationFastMode(final String application, boolean enable, boolean clear);

    public abstract void waitForUpdateFinished();
}
