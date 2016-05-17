/**
 * 
 */
package com.onyx.android.sdk.api.device;

import com.onyx.android.sdk.device.IMX6Device;

/**
 * EPD work differently according to devices 
 * 
 * @author joy
 *
 */
public class DeviceInfo
{
    public static final IMX6Device currentDevice;
    
    static {
        currentDevice = IMX6Device.createDevice();
    }
}
